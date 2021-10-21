/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.client.impl;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
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
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.onap.dmaap.mr.client.DmaapClientConst;
import org.onap.dmaap.mr.client.HostSelector;
import org.onap.dmaap.mr.client.MRBatchingPublisher;
import org.onap.dmaap.mr.client.ProtocolType;
import org.onap.dmaap.mr.client.response.MRPublisherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRSimplerBatchPublisher extends MRBaseClient implements MRBatchingPublisher {
    private static final Logger logger = LoggerFactory.getLogger(MRSimplerBatchPublisher.class);

    private static final String HEADER_DME2_EXCHANGE_REQUEST_HANDLERS = "AFT_DME2_EXCHANGE_REQUEST_HANDLERS";
    private static final String HEADER_DME2_EXCHANGE_REPLY_HANDLERS = "AFT_DME2_EXCHANGE_REPLY_HANDLERS";
    private static final String HEADER_DME2_REQ_TRACE_ON = "AFT_DME2_REQ_TRACE_ON";

    private static final String CONTENT_TYPE_TEXT = "text/plain";

    private static final String JSON_STATUS = "status";

    public static class Builder {

        public Builder againstUrls(Collection<String> baseUrls) {
            fUrls = baseUrls;
            return this;
        }

        public Builder againstUrlsOrServiceName(Collection<String> baseUrls, Collection<String> serviceName, String transportype) {
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

        public Builder httpThreadTime(int threadOccurrenceTime) {
            this.threadOccurrenceTime = threadOccurrenceTime;
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
                            fAllowSelfSignedCerts, threadOccurrenceTime);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                try {
                    return new MRSimplerBatchPublisher(fUrls, fTopic, fMaxBatchSize, fMaxBatchAgeMs, fCompress,
                            fAllowSelfSignedCerts, fMaxBatchSize);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            }

        }

        private Collection<String> fUrls;
        private String fTopic;
        private int fMaxBatchSize = 100;

        private long fMaxBatchAgeMs = 1000;
        private boolean fCompress = false;
        private int threadOccurrenceTime = 50;
        private boolean fAllowSelfSignedCerts = false;
        private boolean fWithResponse = false;

    }

    @Override
    public int send(String partition, String msg) {
        return send(new Message(partition, msg));
    }

    @Override
    public int send(String msg) {
        return send(new Message(null, msg));
    }

    @Override
    public int send(Message msg) {
        final LinkedList<Message> list = new LinkedList<>();
        list.add(msg);
        return send(list);
    }

    @Override
    public synchronized int send(Collection<Message> msgs) {
        if (fClosed) {
            throw new IllegalStateException("The publisher was closed.");
        }

        for (Message userMsg : msgs) {
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
            final List<Message> remains = close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            if (remains.isEmpty()) {
                getLog().warn("Closing publisher with {} messages unsent. Consider using MRBatchingPublisher.close( long timeout, TimeUnit timeoutUnits ) to recapture unsent messages on close.",
                        remains.size());
            }
        } catch (InterruptedException e) {
            getLog().warn("Possible message loss. " + e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            getLog().warn("Possible message loss. " + e.getMessage(), e);
        }
    }

    @Override
    public List<Message> close(long time, TimeUnit unit) throws IOException, InterruptedException {
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
            final LinkedList<Message> result = new LinkedList<>();
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
        if ((force || shouldSendNow()) && !sendBatch()) {
            getLog().warn("Send failed, {} message to send.", fPending.size());
            // note the time for back-off
            fDontSendUntilMs = SF_WAIT_AFTER_ERROR + Clock.now();
        }
    }

    private synchronized boolean shouldSendNow() {
        boolean shouldSend = false;
        if (!fPending.isEmpty()) {
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
     * Method to parse published JSON Objects and Arrays.
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
                for (int i = 0; i < tempjsonArray.length(); i++) {
                    jsonArray.put(tempjsonArray.getJSONObject(i));
                }
            } else {
                jsonObject = new JSONObject(jsonTokener);
                jsonArray.put(jsonObject);
            }

        }
        return jsonArray;
    }

    private void logTime(long startMs, String dmeResponse) {
        if (getLog().isInfoEnabled()) {
            getLog().info("MR reply ok ({} ms):{}", (Clock.now() - startMs), dmeResponse);
        }
    }

    private void logSendMessage(int nbMessage, String dest, long time) {
        if (getLog().isInfoEnabled()) {
            getLog().info("sending {} msgs to {}. Oldest: {} ms", nbMessage, dest, time);
        }
    }

    private synchronized boolean sendBatch() {
        // it's possible for this call to be made with an empty list. in this
        // case, just return.
        if (fPending.isEmpty()) {
            return true;
        }

        final long nowMs = Clock.now();

        if (this.fHostSelector != null) {
            host = this.fHostSelector.selectBaseHost();
        }

        final String httpurl = MRConstants.makeUrl(host, fTopic, props.getProperty(DmaapClientConst.PROTOCOL),
                props.getProperty(DmaapClientConst.PARTITION));

        try {

            final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();
            OutputStream os = baseStream;
            final String contentType = props.getProperty(DmaapClientConst.CONTENT_TYPE);
            if (contentType.equalsIgnoreCase(MRFormat.JSON.toString())) {
                JSONArray jsonArray = parseJSON();
                os.write(jsonArray.toString().getBytes());
                os.close();

            } else if (contentType.equalsIgnoreCase(CONTENT_TYPE_TEXT)) {
                for (TimestampedMessage m : fPending) {
                    os.write(m.fMsg.getBytes());
                    os.write('\n');
                }
                os.close();
            } else if (contentType.equalsIgnoreCase(MRFormat.CAMBRIA.toString())
                    || (contentType.equalsIgnoreCase(MRFormat.CAMBRIA_ZIP.toString()))) {
                if (contentType.equalsIgnoreCase(MRFormat.CAMBRIA_ZIP.toString())) {
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
            if (ProtocolType.DME2.getValue().equalsIgnoreCase(protocolFlag)) {

                configureDME2();

                this.wait(5);
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), url + subContextPath, nowMs - fPending.peek().timestamp);
                }
                sender.setPayload(os.toString());
                String dmeResponse = sender.sendAndWait(5000L);

                logTime(startMs, dmeResponse);
                fPending.clear();
                return true;
            }

            if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpurl, nowMs - fPending.peek().timestamp);
                }
                final JSONObject result =
                        postAuth(new PostAuthDataObject().setPath(httpurl).setData(baseStream.toByteArray())
                                .setContentType(contentType).setAuthKey(authKey).setAuthDate(authDate)
                                .setUsername(username).setPassword(password).setProtocolFlag(protocolFlag));
                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false
                if (result.getInt(JSON_STATUS) < 200 || result.getInt(JSON_STATUS) > 299) {
                    return false;
                }
                logTime(startMs, result.toString());
                fPending.clear();
                return true;
            }

            if (ProtocolType.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpurl, nowMs - fPending.peek().timestamp);
                }
                final JSONObject result = post(httpurl, baseStream.toByteArray(), contentType, username, password,
                        protocolFlag);

                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false
                if (result.getInt(JSON_STATUS) < 200 || result.getInt(JSON_STATUS) > 299) {
                    return false;
                }
                logTime(startMs, result.toString());
                fPending.clear();
                return true;
            }

            if (ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpurl, nowMs - fPending.peek().timestamp);
                }
                final JSONObject result = postNoAuth(httpurl, baseStream.toByteArray(), contentType);

                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false
                if (result.getInt(JSON_STATUS) < 200 || result.getInt(JSON_STATUS) > 299) {
                    return false;
                }
                logTime(startMs, result.toString());
                fPending.clear();
                return true;
            }
        } catch (InterruptedException e) {
            getLog().warn("Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception x) {
            getLog().warn(x.getMessage(), x);
        }
        return false;
    }

    public synchronized MRPublisherResponse sendBatchWithResponse() {
        // it's possible for this call to be made with an empty list. in this
        // case, just return.
        if (fPending.isEmpty()) {
            pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
            pubResponse.setResponseMessage("No Messages to send");
            return pubResponse;
        }

        final long nowMs = Clock.now();

        host = this.fHostSelector.selectBaseHost();

        final String httpUrl = MRConstants.makeUrl(host, fTopic, props.getProperty(DmaapClientConst.PROTOCOL),
                props.getProperty(DmaapClientConst.PARTITION));
        OutputStream os = null;
        try (ByteArrayOutputStream baseStream = new ByteArrayOutputStream()) {
            os = baseStream;
            final String propsContentType = props.getProperty(DmaapClientConst.CONTENT_TYPE);
            if (propsContentType.equalsIgnoreCase(MRFormat.JSON.toString())) {
                JSONArray jsonArray = parseJSON();
                os.write(jsonArray.toString().getBytes());
            } else if (propsContentType.equalsIgnoreCase(CONTENT_TYPE_TEXT)) {
                for (TimestampedMessage m : fPending) {
                    os.write(m.fMsg.getBytes());
                    os.write('\n');
                }
            } else if (propsContentType.equalsIgnoreCase(MRFormat.CAMBRIA.toString())
                    || (propsContentType.equalsIgnoreCase(MRFormat.CAMBRIA_ZIP.toString()))) {
                if (propsContentType.equalsIgnoreCase(MRFormat.CAMBRIA_ZIP.toString())) {
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
            if (ProtocolType.DME2.getValue().equalsIgnoreCase(protocolFlag)) {

                try {
                    configureDME2();

                    this.wait(5);

                    if (fPending.peek() != null) {
                        logSendMessage(fPending.size(), url + subContextPath, nowMs - fPending.peek().timestamp);
                    }
                    sender.setPayload(os.toString());

                    String dmeResponse = sender.sendAndWait(5000L);

                    pubResponse = createMRPublisherResponse(dmeResponse, pubResponse);

                    if (Integer.parseInt(pubResponse.getResponseCode()) < 200
                            || Integer.parseInt(pubResponse.getResponseCode()) > 299) {

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
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception x) {

                    pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
                    pubResponse.setResponseMessage(x.getMessage());
                    logger.error("exception: ", x);

                }

                return pubResponse;
            }

            if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpUrl, nowMs - fPending.peek().timestamp);
                }
                final String result = postAuthwithResponse(httpUrl, baseStream.toByteArray(), contentType, authKey,
                        authDate, username, password, protocolFlag);
                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false

                pubResponse = createMRPublisherResponse(result, pubResponse);

                if (Integer.parseInt(pubResponse.getResponseCode()) < 200
                        || Integer.parseInt(pubResponse.getResponseCode()) > 299) {

                    return pubResponse;
                }

                logTime(startMs, result);
                fPending.clear();
                return pubResponse;
            }

            if (ProtocolType.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpUrl, nowMs - fPending.peek().timestamp);
                }
                final String result = postWithResponse(httpUrl, baseStream.toByteArray(), contentType, username,
                        password, protocolFlag);

                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false
                pubResponse = createMRPublisherResponse(result, pubResponse);

                if (Integer.parseInt(pubResponse.getResponseCode()) < 200
                        || Integer.parseInt(pubResponse.getResponseCode()) > 299) {

                    return pubResponse;
                }

                final String logLine = String.valueOf((Clock.now() - startMs));
                getLog().info(logLine);
                fPending.clear();
                return pubResponse;
            }

            if (ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                if (fPending.peek() != null) {
                    logSendMessage(fPending.size(), httpUrl, nowMs - fPending.peek().timestamp);
                }
                final String result = postNoAuthWithResponse(httpUrl, baseStream.toByteArray(), contentType);

                // Here we are checking for error response. If HTTP status
                // code is not within the http success response code
                // then we consider this as error and return false
                pubResponse = createMRPublisherResponse(result, pubResponse);

                if (Integer.parseInt(pubResponse.getResponseCode()) < 200
                        || Integer.parseInt(pubResponse.getResponseCode()) > 299) {

                    return pubResponse;
                }

                final String logLine = String.valueOf((Clock.now() - startMs));
                getLog().info(logLine);
                fPending.clear();
                return pubResponse;
            }
        } catch (IllegalArgumentException | HttpException x) {
            getLog().warn(x.getMessage(), x);
            pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
            pubResponse.setResponseMessage(x.getMessage());

        } catch (IOException x) {
            getLog().warn(x.getMessage(), x);
            pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            pubResponse.setResponseMessage(x.getMessage());
        } catch (InterruptedException e) {
            getLog().warn("Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception x) {
            getLog().warn(x.getMessage(), x);

            pubResponse.setResponseCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            pubResponse.setResponseMessage(x.getMessage());

        } finally {
            if (!fPending.isEmpty()) {
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

    public MRPublisherResponse createMRPublisherResponse(String reply, MRPublisherResponse mrPubResponse) {

        if (reply.isEmpty()) {

            mrPubResponse.setResponseCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
            mrPubResponse.setResponseMessage("Please verify the Producer properties");
        } else if (reply.startsWith("{")) {
            JSONObject jObject = new JSONObject(reply);
            if (jObject.has("message") && jObject.has(JSON_STATUS)) {
                String message = jObject.getString("message");
                if (null != message) {
                    mrPubResponse.setResponseMessage(message);
                }
                mrPubResponse.setResponseCode(Integer.toString(jObject.getInt(JSON_STATUS)));
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
    private int threadOccurrenceTime;
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
    private static final long SF_WAIT_AFTER_ERROR = 10000;
    private HashMap<String, String> DMETimeOuts;
    private DME2Client sender;
    public String protocolFlag = ProtocolType.DME2.getValue();
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
        setClientConfig(DmaapClientUtil.getClientConfig(props));
    }

    public String getProtocolFlag() {
        return protocolFlag;
    }

    public void setProtocolFlag(String protocolFlag) {
        this.protocolFlag = protocolFlag;
    }

    private void configureDME2() throws Exception {
        try {

            latitude = props.getProperty(DmaapClientConst.LATITUDE);
            longitude = props.getProperty(DmaapClientConst.LONGITUDE);
            version = props.getProperty(DmaapClientConst.VERSION);
            serviceName = props.getProperty(DmaapClientConst.SERVICE_NAME);
            env = props.getProperty(DmaapClientConst.ENVIRONMENT);
            partner = props.getProperty(DmaapClientConst.PARTNER);
            routeOffer = props.getProperty(DmaapClientConst.ROUTE_OFFER);
            subContextPath = props.getProperty(DmaapClientConst.SUB_CONTEXT_PATH) + fTopic;

            protocol = props.getProperty(DmaapClientConst.PROTOCOL);
            methodType = props.getProperty(DmaapClientConst.METHOD_TYPE);
            dmeuser = props.getProperty(DmaapClientConst.USERNAME);
            dmepassword = props.getProperty(DmaapClientConst.PASSWORD);
            contentType = props.getProperty(DmaapClientConst.CONTENT_TYPE);
            handlers = props.getProperty(DmaapClientConst.SESSION_STICKINESS_REQUIRED);

            MRSimplerBatchPublisher.routerFilePath = props.getProperty(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH);

            /*
             * Changes to DME2Client url to use Partner for auto failover
             * between data centers When Partner value is not provided use the
             * routeOffer value for auto failover within a cluster
             */

            String partitionKey = props.getProperty(DmaapClientConst.PARTITION);

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

            DMETimeOuts = new HashMap<>();
            DMETimeOuts.put("AFT_DME2_EP_READ_TIMEOUT_MS", props.getProperty(DmaapClientConst.AFT_DME2_EP_READ_TIMEOUT_MS));
            DMETimeOuts.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", props.getProperty(DmaapClientConst.AFT_DME2_ROUNDTRIP_TIMEOUT_MS));
            DMETimeOuts.put("AFT_DME2_EP_CONN_TIMEOUT", props.getProperty(DmaapClientConst.AFT_DME2_EP_CONN_TIMEOUT));
            DMETimeOuts.put("Content-Type", contentType);
            System.setProperty("AFT_LATITUDE", latitude);
            System.setProperty("AFT_LONGITUDE", longitude);
            System.setProperty("AFT_ENVIRONMENT", props.getProperty(DmaapClientConst.AFT_ENVIRONMENT));
            // System.setProperty("DME2.DEBUG", "true");

            // SSL changes
            // System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS",

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
            if ("yes".equalsIgnoreCase(handlers)) {
                sender.addHeader(HEADER_DME2_EXCHANGE_REQUEST_HANDLERS,
                        props.getProperty(DmaapClientConst.AFT_DME2_EXCHANGE_REQUEST_HANDLERS));
                sender.addHeader(HEADER_DME2_EXCHANGE_REPLY_HANDLERS,
                        props.getProperty(DmaapClientConst.AFT_DME2_EXCHANGE_REPLY_HANDLERS));
                sender.addHeader(HEADER_DME2_REQ_TRACE_ON, props.getProperty(DmaapClientConst.AFT_DME2_REQ_TRACE_ON));
            } else {
                sender.addHeader(HEADER_DME2_EXCHANGE_REQUEST_HANDLERS, "com.att.nsa.mr.dme.client.HeaderReplyHandler");
            }
        } catch (DME2Exception x) {
            getLog().warn(x.getMessage(), x);
            throw new DME2Exception(x.getErrorCode(), x.getErrorMessage());
        } catch (URISyntaxException x) {

            getLog().warn(x.getMessage(), x);
            throw new URISyntaxException(url, x.getMessage());
        } catch (Exception x) {

            getLog().warn(x.getMessage(), x);
            throw new IllegalArgumentException(x.getMessage());
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

        fPending = new LinkedBlockingQueue<>();
        fDontSendUntilMs = 0;
        fExec = new ScheduledThreadPoolExecutor(1);
        pubResponse = new MRPublisherResponse();

    }

    private MRSimplerBatchPublisher(Collection<String> hosts, String topic, int maxBatchSize, long maxBatchAgeMs,
                                    boolean compress, boolean allowSelfSignedCerts, int httpThreadOccurrence) throws MalformedURLException {
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
        threadOccurrenceTime = httpThreadOccurrence;
        fPending = new LinkedBlockingQueue<>();
        fDontSendUntilMs = 0;
        fExec = new ScheduledThreadPoolExecutor(1);
        fExec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                send(false);
            }
        }, 100, threadOccurrenceTime, TimeUnit.MILLISECONDS);
        pubResponse = new MRPublisherResponse();
    }

    private static class TimestampedMessage extends Message {
        public TimestampedMessage(Message message) {
            super(message);
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
