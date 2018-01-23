/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *  
 *******************************************************************************/
package com.att.nsa.mr.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import com.att.nsa.mr.client.HostSelector;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.response.MRPublisherResponse;
import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

public class MRSimplerBatchPublisher extends MRBaseClient implements MRBatchingPublisher {
	private static final Logger logger = LoggerFactory.getLogger(MRSimplerBatchPublisher.class);

	public static class Builder {
		public Builder() {
		}

		public Builder againstUrls(Collection<String> baseUrls) {
			fUrls = baseUrls;
			return this;
		}

		public Builder onTopic(String topic) {
			fTopic = topic;
			return this;
		}

		public Builder batchTo(int maxBatchSize, long maxBatchAgeMs) {
			fMaxBatchSize = maxBatchSize;
			fMaxBatchAgeMs = maxBatchAgeMs;
			return this;
		}

		public Builder compress(boolean compress) {
			fCompress = compress;
			return this;
		}

		public Builder httpThreadTime(int threadOccuranceTime) {
			this.threadOccuranceTime = threadOccuranceTime;
			return this;
		}

		public Builder allowSelfSignedCertificates(boolean allowSelfSignedCerts) {
			fAllowSelfSignedCerts = allowSelfSignedCerts;
			return this;
		}

		public Builder withResponse(boolean withResponse) {
			fWithResponse = withResponse;
			return this;
		}

		public MRSimplerBatchPublisher build() {
			if (!fWithResponse) {
				try {
					return new MRSimplerBatchPublisher(fUrls, fTopic, fMaxBatchSize, fMaxBatchAgeMs, fCompress,
							fAllowSelfSignedCerts, threadOccuranceTime);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					return new MRSimplerBatchPublisher(fUrls, fTopic, fMaxBatchSize, fMaxBatchAgeMs, fCompress,
							fAllowSelfSignedCerts, fMaxBatchSize);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}

		}

		private Collection<String> fUrls;
		private String fTopic;
		private int fMaxBatchSize = 100;
		private long fMaxBatchAgeMs = 1000;
		private boolean fCompress = false;
		private int threadOccuranceTime = 50;
		private boolean fAllowSelfSignedCerts = false;
		private boolean fWithResponse = false;

	};

	@Override
	public int send(String partition, String msg) {
		return send(new message(partition, msg));
	}

	@Override
	public int send(String msg) {
		return send(new message(null, msg));
	}

	@Override
	public int send(message msg) {
		final LinkedList<message> list = new LinkedList<message>();
		list.add(msg);
		return send(list);
	}

	@Override
	public synchronized int send(Collection<message> msgs) {
		if (fClosed) {
			throw new IllegalStateException("The publisher was closed.");
		}

		for (message userMsg : msgs) {
			fPending.add(new TimestampedMessage(userMsg));
		}
		return getPendingMessageCount();
	}

	@Override
	public synchronized int getPendingMessageCount() {
		return fPending.size();
	}

	@Override
	public void close() {
		try {
			final List<message> remains = close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			if (remains.size() > 0) {
				getLog().warn("Closing publisher with " + remains.size() + " messages unsent. "
						+ "Consider using MRBatchingPublisher.close( long timeout, TimeUnit timeoutUnits ) to recapture unsent messages on close.");
			}
		} catch (InterruptedException e) {
			getLog().warn("Possible message loss. " + e.getMessage(), e);
		} catch (IOException e) {
			getLog().warn("Possible message loss. " + e.getMessage(), e);
		}
	}

	@Override
	public List<message> close(long time, TimeUnit unit) throws IOException, InterruptedException {
		synchronized (this) {
			fClosed = true;

			// stop the background sender
			fExec.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
			fExec.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			fExec.shutdown();
		}

		final long now = Clock.now();
		final long waitInMs = TimeUnit.MILLISECONDS.convert(time, unit);
		final long timeoutAtMs = now + waitInMs;

		while (Clock.now() < timeoutAtMs && getPendingMessageCount() > 0) {
			send(true);
			Thread.sleep(250);
		}

		synchronized (this) {
			final LinkedList<message> result = new LinkedList<message>();
			fPending.drainTo(result);
			return result;
		}
	}

	/**
	 * Possibly send a batch to the MR server. This is called by the background
	 * thread and the close() method
	 * 
	 * @param force
	 */
	private synchronized void send(boolean force) {
		if (force || shouldSendNow()) {
			if (!sendBatch()) {
				getLog().warn("Send failed, " + fPending.size() + " message to send.");

				// note the time for back-off
				fDontSendUntilMs = sfWaitAfterError + Clock.now();
			}
		}
	}

	private synchronized boolean shouldSendNow() {
		boolean shouldSend = false;
		if (fPending.size() > 0) {
			final long nowMs = Clock.now();

			shouldSend = (fPending.size() >= fMaxBatchSize);
			if (!shouldSend) {
				final long sendAtMs = fPending.peek().timestamp + fMaxBatchAgeMs;
				shouldSend = sendAtMs <= nowMs;
			}

			// however, wait after an error
			shouldSend = shouldSend && nowMs >= fDontSendUntilMs;
		}
		return shouldSend;
	}

	/**
	 * Method to parse published JSON Objects and Arrays
	 * 
	 * @return JSONArray
	 */
	private JSONArray parseJSON() {
		JSONArray jsonArray = new JSONArray();
		for (TimestampedMessage m : fPending) {
			JSONTokener jsonTokener = new JSONTokener(m.fMsg);
			JSONObject jsonObject = null;
			JSONArray tempjsonArray = null;
			final char firstChar = jsonTokener.next();
			jsonTokener.back();
			if ('[' == firstChar) {
				tempjsonArray = new JSONArray(jsonTokener);
				if (null != tempjsonArray) {
					for (int i = 0; i < tempjsonArray.length(); i++) {
						jsonArray.put(tempjsonArray.getJSONObject(i));
					}
				}
			} else {
				jsonObject = new JSONObject(jsonTokener);
				jsonArray.put(jsonObject);
			}

		}
		return jsonArray;
	}

	private synchronized boolean sendBatch() {
		// it's possible for this call to be made with an empty list. in this
		// case, just return.
		if (fPending.size() < 1) {
			return true;
		}

		final long nowMs = Clock.now();

		host = this.fHostSelector.selectBaseHost();

		final String httpurl = MRConstants.makeUrl(host, fTopic, props.getProperty("Protocol"),
				props.getProperty("partition"));

		try {
			/*
			 * final String contentType = fCompress ?
			 * MRFormat.CAMBRIA_ZIP.toString () : MRFormat.CAMBRIA.toString () ;
			 */

			final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();
			OutputStream os = baseStream;
			final String contentType = props.getProperty("contenttype");
			if (contentType.equalsIgnoreCase("application/json")) {
				JSONArray jsonArray = parseJSON();
				os.write(jsonArray.toString().getBytes());
				os.close();

			} else if (contentType.equalsIgnoreCase("text/plain")) {
				for (TimestampedMessage m : fPending) {
					os.write(m.fMsg.getBytes());
					os.write('\n');
				}
				os.close();
			} else if (contentType.equalsIgnoreCase("application/cambria")
					|| (contentType.equalsIgnoreCase("application/cambria-zip"))) {
				if (contentType.equalsIgnoreCase("application/cambria-zip")) {
					os = new GZIPOutputStream(baseStream);
				}
				for (TimestampedMessage m : fPending) {

					os.write(("" + m.fPartition.length()).getBytes());
					os.write('.');
					os.write(("" + m.fMsg.length()).getBytes());
					os.write('.');
					os.write(m.fPartition.getBytes());
					os.write(m.fMsg.getBytes());
					os.write('\n');
				}
				os.close();
			} else {
				for (TimestampedMessage m : fPending) {
					os.write(m.fMsg.getBytes());

				}
				os.close();
			}

			final long startMs = Clock.now();
			if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(protocolFlag)) {

				DME2Configue();

				Thread.sleep(5);
				getLog().info("sending " + fPending.size() + " msgs to " + url + subContextPath + ". Oldest: "
						+ (nowMs - fPending.peek().timestamp) + " ms");
				sender.setPayload(os.toString());
				String dmeResponse = sender.sendAndWait(5000L);

				final String logLine = "MR reply ok (" + (Clock.now() - startMs) + " ms):" + dmeResponse.toString();
				getLog().info(logLine);
				fPending.clear();
				return true;
			}

			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				getLog().info("sending " + fPending.size() + " msgs to " + httpurl + ". Oldest: "
						+ (nowMs - fPending.peek().timestamp) + " ms");
				final JSONObject result = postAuth(httpurl, baseStream.toByteArray(), contentType, authKey, authDate,
						username, password, protocolFlag);
				// Here we are checking for error response. If HTTP status
				// code is not within the http success response code
				// then we consider this as error and return false
				if (result.getInt("status") < 200 || result.getInt("status") > 299) {
					return false;
				}
				final String logLine = "MR reply ok (" + (Clock.now() - startMs) + " ms):" + result.toString();
				getLog().info(logLine);
				fPending.clear();
				return true;
			}

			if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
				getLog().info("sending " + fPending.size() + " msgs to " + httpurl + ". Oldest: "
						+ (nowMs - fPending.peek().timestamp) + " ms");
				final JSONObject result = post(httpurl, baseStream.toByteArray(), contentType, username, password,
						protocolFlag);

				// Here we are checking for error response. If HTTP status
				// code is not within the http success response code
				// then we consider this as error and return false
				if (result.getInt("status") < 200 || result.getInt("status") > 299) {
					return false;
				}
				final String logLine = "MR reply ok (" + (Clock.now() - startMs) + " ms):" + result.toString();
				getLog().info(logLine);
				fPending.clear();
				return true;
			}
		} catch (IllegalArgumentException x) {
			getLog().warn(x.getMessage(), x);
		} catch (IOException x) {
			getLog().warn(x.getMessage(), x);
		} catch (HttpException x) {
			getLog().warn(x.getMessage(), x);
		} catch (Exception x) {
			getLog().warn(x.getMessage(), x);
		}
		return false;
	}

	public synchronized MRPublisherResponse sendBatchWithResponse() {
		// it's possible for this call to be made with an empty list. in this
		// case, just return.
		if (fPending.size() < 1) {
			pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			pubResponse.setResponseMessage("No Messages to send");
			return pubResponse;
		}

		final long nowMs = Clock.now();

		host = this.fHostSelector.selectBaseHost();

		final String httpurl = MRConstants.makeUrl(host, fTopic, props.getProperty("Protocol"),
				props.getProperty("partition"));
		OutputStream os = null;
		try {

			final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();
			os = baseStream;
			final String contentType = props.getProperty("contenttype");
			if (contentType.equalsIgnoreCase("application/json")) {
				JSONArray jsonArray = parseJSON();
				os.write(jsonArray.toString().getBytes());
			} else if (contentType.equalsIgnoreCase("text/plain")) {
				for (TimestampedMessage m : fPending) {
					os.write(m.fMsg.getBytes());
					os.write('\n');
				}
			} else if (contentType.equalsIgnoreCase("application/cambria")
					|| (contentType.equalsIgnoreCase("application/cambria-zip"))) {
				if (contentType.equalsIgnoreCase("application/cambria-zip")) {
					os = new GZIPOutputStream(baseStream);
				}
				for (TimestampedMessage m : fPending) {

					os.write(("" + m.fPartition.length()).getBytes());
					os.write('.');
					os.write(("" + m.fMsg.length()).getBytes());
					os.write('.');
					os.write(m.fPartition.getBytes());
					os.write(m.fMsg.getBytes());
					os.write('\n');
				}
				os.close();
			} else {
				for (TimestampedMessage m : fPending) {
					os.write(m.fMsg.getBytes());

				}
			}

			final long startMs = Clock.now();
			if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(protocolFlag)) {

				try {
					DME2Configue();

					Thread.sleep(5);
					getLog().info("sending " + fPending.size() + " msgs to " + url + subContextPath + ". Oldest: "
							+ (nowMs - fPending.peek().timestamp) + " ms");
					sender.setPayload(os.toString());

					String dmeResponse = sender.sendAndWait(5000L);

					pubResponse = createMRPublisherResponse(dmeResponse, pubResponse);

					if (Integer.valueOf(pubResponse.getResponseCode()) < 200
							|| Integer.valueOf(pubResponse.getResponseCode()) > 299) {

						return pubResponse;
					}
					final String logLine = String.valueOf((Clock.now() - startMs)) + dmeResponse.toString();
					getLog().info(logLine);
					fPending.clear();

				} catch (DME2Exception x) {
					getLog().warn(x.getMessage(), x);
					pubResponse.setResponseCode(x.getErrorCode());
					pubResponse.setResponseMessage(x.getErrorMessage());
				} catch (URISyntaxException x) {

					getLog().warn(x.getMessage(), x);
					pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
					pubResponse.setResponseMessage(x.getMessage());
				} catch (Exception x) {

					pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
					pubResponse.setResponseMessage(x.getMessage());
					logger.error("exception: ", x);

				}

				return pubResponse;
			}

			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				getLog().info("sending " + fPending.size() + " msgs to " + httpurl + ". Oldest: "
						+ (nowMs - fPending.peek().timestamp) + " ms");
				final String result = postAuthwithResponse(httpurl, baseStream.toByteArray(), contentType, authKey,
						authDate, username, password, protocolFlag);
				// Here we are checking for error response. If HTTP status
				// code is not within the http success response code
				// then we consider this as error and return false

				pubResponse = createMRPublisherResponse(result, pubResponse);

				if (Integer.valueOf(pubResponse.getResponseCode()) < 200
						|| Integer.valueOf(pubResponse.getResponseCode()) > 299) {

					return pubResponse;
				}

				final String logLine = "MR reply ok (" + (Clock.now() - startMs) + " ms):" + result.toString();
				getLog().info(logLine);
				fPending.clear();
				return pubResponse;
			}

			if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
				getLog().info("sending " + fPending.size() + " msgs to " + httpurl + ". Oldest: "
						+ (nowMs - fPending.peek().timestamp) + " ms");
				final String result = postWithResponse(httpurl, baseStream.toByteArray(), contentType, username,
						password, protocolFlag);

				// Here we are checking for error response. If HTTP status
				// code is not within the http success response code
				// then we consider this as error and return false
				pubResponse = createMRPublisherResponse(result, pubResponse);

				if (Integer.valueOf(pubResponse.getResponseCode()) < 200
						|| Integer.valueOf(pubResponse.getResponseCode()) > 299) {

					return pubResponse;
				}

				final String logLine = String.valueOf((Clock.now() - startMs));
				getLog().info(logLine);
				fPending.clear();
				return pubResponse;
			}
		} catch (IllegalArgumentException x) {
			getLog().warn(x.getMessage(), x);
			pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			pubResponse.setResponseMessage(x.getMessage());

		} catch (IOException x) {
			getLog().warn(x.getMessage(), x);
			pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
			pubResponse.setResponseMessage(x.getMessage());

		} catch (HttpException x) {
			getLog().warn(x.getMessage(), x);
			pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			pubResponse.setResponseMessage(x.getMessage());

		} catch (Exception x) {
			getLog().warn(x.getMessage(), x);

			pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
			pubResponse.setResponseMessage(x.getMessage());

		}

		finally {
			if (fPending.size() > 0) {
				getLog().warn("Send failed, " + fPending.size() + " message to send.");
				pubResponse.setPendingMsgs(fPending.size());
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception x) {
					getLog().warn(x.getMessage(), x);
					pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
					pubResponse.setResponseMessage("Error in closing Output Stream");
				}
			}
		}

		return pubResponse;
	}

	private MRPublisherResponse createMRPublisherResponse(String reply, MRPublisherResponse mrPubResponse) {

		if (reply.isEmpty()) {

			mrPubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			mrPubResponse.setResponseMessage("Please verify the Producer properties");
		} else if (reply.startsWith("{")) {
			JSONObject jObject = new JSONObject(reply);
			if (jObject.has("message") && jObject.has("status")) {
				String message = jObject.getString("message");
				if (null != message) {
					mrPubResponse.setResponseMessage(message);
				}
				mrPubResponse.setResponseCode(Integer.toString(jObject.getInt("status")));
			} else {
				mrPubResponse.setResponseCode(String.valueOf(HttpStatus.SC_OK));
				mrPubResponse.setResponseMessage(reply);
			}
		} else if (reply.startsWith("<")) {
			String responseCode = getHTTPErrorResponseCode(reply);
			if (responseCode.contains("403")) {
				responseCode = "403";
			}
			mrPubResponse.setResponseCode(responseCode);
			mrPubResponse.setResponseMessage(getHTTPErrorResponseMessage(reply));
		}

		return mrPubResponse;
	}

	private final String fTopic;
	private final int fMaxBatchSize;
	private final long fMaxBatchAgeMs;
	private final boolean fCompress;
	private int threadOccuranceTime;
	private boolean fClosed;
	private String username;
	private String password;
	private String host;

	// host selector
	private HostSelector fHostSelector = null;

	private final LinkedBlockingQueue<TimestampedMessage> fPending;
	private long fDontSendUntilMs;
	private final ScheduledThreadPoolExecutor fExec;

	private String latitude;
	private String longitude;
	private String version;
	private String serviceName;
	private String env;
	private String partner;
	private String routeOffer;
	private String subContextPath;
	private String protocol;
	private String methodType;
	private String url;
	private String dmeuser;
	private String dmepassword;
	private String contentType;
	private static final long sfWaitAfterError = 10000;
	private HashMap<String, String> DMETimeOuts;
	private DME2Client sender;
	public String protocolFlag = ProtocolTypeConstants.DME2.getValue();
	private String authKey;
	private String authDate;
	private String handlers;
	private Properties props;
	public static String routerFilePath;
	protected static final Map<String, String> headers = new HashMap<String, String>();
	public static MultivaluedMap<String, Object> headersMap;

	private MRPublisherResponse pubResponse;

	public MRPublisherResponse getPubResponse() {
		return pubResponse;
	}

	public void setPubResponse(MRPublisherResponse pubResponse) {
		this.pubResponse = pubResponse;
	}

	public static String getRouterFilePath() {
		return routerFilePath;
	}

	public static void setRouterFilePath(String routerFilePath) {
		MRSimplerBatchPublisher.routerFilePath = routerFilePath;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String getProtocolFlag() {
		return protocolFlag;
	}

	public void setProtocolFlag(String protocolFlag) {
		this.protocolFlag = protocolFlag;
	}

	private void DME2Configue() throws Exception {
		try {

			/*
			 * FileReader reader = new FileReader(new File (producerFilePath));
			 * Properties props = new Properties(); props.load(reader);
			 */
			latitude = props.getProperty("Latitude");
			longitude = props.getProperty("Longitude");
			version = props.getProperty("Version");
			serviceName = props.getProperty("ServiceName");
			env = props.getProperty("Environment");
			partner = props.getProperty("Partner");
			routeOffer = props.getProperty("routeOffer");
			subContextPath = props.getProperty("SubContextPath") + fTopic;
			/*
			 * if(props.getProperty("partition")!=null &&
			 * !props.getProperty("partition").equalsIgnoreCase("")){
			 * subContextPath=subContextPath+"?partitionKey="+props.getProperty(
			 * "partition"); }
			 */
			protocol = props.getProperty("Protocol");
			methodType = props.getProperty("MethodType");
			dmeuser = props.getProperty("username");
			dmepassword = props.getProperty("password");
			contentType = props.getProperty("contenttype");
			handlers = props.getProperty("sessionstickinessrequired");
			routerFilePath = props.getProperty("DME2preferredRouterFilePath");

			/**
			 * Changes to DME2Client url to use Partner for auto failover
			 * between data centers When Partner value is not provided use the
			 * routeOffer value for auto failover within a cluster
			 */

			String partitionKey = props.getProperty("partition");

			if (partner != null && !partner.isEmpty()) {
				url = protocol + "://" + serviceName + "?version=" + version + "&envContext=" + env + "&partner="
						+ partner;
				if (partitionKey != null && !partitionKey.equalsIgnoreCase("")) {
					url = url + "&partitionKey=" + partitionKey;
				}
			} else if (routeOffer != null && !routeOffer.isEmpty()) {
				url = protocol + "://" + serviceName + "?version=" + version + "&envContext=" + env + "&routeoffer="
						+ routeOffer;
				if (partitionKey != null && !partitionKey.equalsIgnoreCase("")) {
					url = url + "&partitionKey=" + partitionKey;
				}
			}

			DMETimeOuts = new HashMap<String, String>();
			DMETimeOuts.put("AFT_DME2_EP_READ_TIMEOUT_MS", props.getProperty("AFT_DME2_EP_READ_TIMEOUT_MS"));
			DMETimeOuts.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", props.getProperty("AFT_DME2_ROUNDTRIP_TIMEOUT_MS"));
			DMETimeOuts.put("AFT_DME2_EP_CONN_TIMEOUT", props.getProperty("AFT_DME2_EP_CONN_TIMEOUT"));
			DMETimeOuts.put("Content-Type", contentType);
			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT", props.getProperty("AFT_ENVIRONMENT"));
			// System.setProperty("DME2.DEBUG", "true");

			// SSL changes
			// System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS",
			// "SSLv3,TLSv1,TLSv1.1");
			System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "TLSv1.1,TLSv1.2");
			System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
			System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");

			// SSL changes

			sender = new DME2Client(new URI(url), 5000L);

			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setCredentials(dmeuser, dmepassword);
			sender.setHeaders(DMETimeOuts);
			if (handlers.equalsIgnoreCase("yes")) {
				sender.addHeader("AFT_DME2_EXCHANGE_REQUEST_HANDLERS",
						props.getProperty("AFT_DME2_EXCHANGE_REQUEST_HANDLERS"));
				sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS",
						props.getProperty("AFT_DME2_EXCHANGE_REPLY_HANDLERS"));
				sender.addHeader("AFT_DME2_REQ_TRACE_ON", props.getProperty("AFT_DME2_REQ_TRACE_ON"));
			} else {
				sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", "com.att.nsa.mr.dme.client.HeaderReplyHandler");
			}
		} catch (DME2Exception x) {
			getLog().warn(x.getMessage(), x);
			throw new DME2Exception(x.getErrorCode(), x.getErrorMessage());
		} catch (URISyntaxException x) {

			getLog().warn(x.getMessage(), x);
			throw new URISyntaxException(url, x.getMessage());
		} catch (Exception x) {

			getLog().warn(x.getMessage(), x);
			throw new Exception(x.getMessage());
		}
	}

	private MRSimplerBatchPublisher(Collection<String> hosts, String topic, int maxBatchSize, long maxBatchAgeMs,
			boolean compress) throws MalformedURLException {
		super(hosts);

		if (topic == null || topic.length() < 1) {
			throw new IllegalArgumentException("A topic must be provided.");
		}

		fHostSelector = new HostSelector(hosts, null);
		fClosed = false;
		fTopic = topic;
		fMaxBatchSize = maxBatchSize;
		fMaxBatchAgeMs = maxBatchAgeMs;
		fCompress = compress;

		fPending = new LinkedBlockingQueue<TimestampedMessage>();
		fDontSendUntilMs = 0;
		fExec = new ScheduledThreadPoolExecutor(1);
		pubResponse = new MRPublisherResponse();

	}

	private MRSimplerBatchPublisher(Collection<String> hosts, String topic, int maxBatchSize, long maxBatchAgeMs,
			boolean compress, boolean allowSelfSignedCerts, int httpThreadOccurnace) throws MalformedURLException {
		super(hosts);

		if (topic == null || topic.length() < 1) {
			throw new IllegalArgumentException("A topic must be provided.");
		}

		fHostSelector = new HostSelector(hosts, null);
		fClosed = false;
		fTopic = topic;
		fMaxBatchSize = maxBatchSize;
		fMaxBatchAgeMs = maxBatchAgeMs;
		fCompress = compress;
		threadOccuranceTime = httpThreadOccurnace;
		fPending = new LinkedBlockingQueue<TimestampedMessage>();
		fDontSendUntilMs = 0;
		fExec = new ScheduledThreadPoolExecutor(1);
		fExec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				send(false);
			}
		}, 100, threadOccuranceTime, TimeUnit.MILLISECONDS);
	}

	private static class TimestampedMessage extends message {
		public TimestampedMessage(message m) {
			super(m);
			timestamp = Clock.now();
		}

		public final long timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getAuthDate() {
		return authDate;
	}

	public void setAuthDate(String authDate) {
		this.authDate = authDate;
	}

}
