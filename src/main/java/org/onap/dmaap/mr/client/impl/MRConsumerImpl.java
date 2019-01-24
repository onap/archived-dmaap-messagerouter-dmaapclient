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
package org.onap.dmaap.mr.client.impl;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import org.onap.dmaap.mr.client.HostSelector;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRConsumer;
import org.onap.dmaap.mr.client.response.MRConsumerResponse;
import org.onap.dmaap.mr.test.clients.ProtocolTypeConstants;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRConsumerImpl extends MRBaseClient implements MRConsumer {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static final String routerFilePath = null;

    public String protocolFlag = ProtocolTypeConstants.DME2.getValue();
    public String consumerFilePath;

    private static final String SUCCESS_MESSAGE = "Success";
    private static final long DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS = 10000L;
    private static final long DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS = 10000L;

    private final String fTopic;
    private final String fGroup;
    private final String fId;
    private final int fTimeoutMs;
    private final int fLimit;
    private String fFilter;
    private String username;
    private String password;
    private String host;
    private HostSelector fHostSelector = null;
    private String url;
    private DME2Client sender;
    private String authKey;
    private String authDate;
    private Properties props;
    private HashMap<String, String> DMETimeOuts;
    private long dme2ReplyHandlerTimeoutMs;
    private long longPollingMs;

    public MRConsumerImpl(Collection<String> hostPart, final String topic, final String consumerGroup,
            final String consumerId, int timeoutMs, int limit, String filter, String apiKey_username,
            String apiSecret_password) throws MalformedURLException {
        this(hostPart, topic, consumerGroup, consumerId, timeoutMs, limit, filter, apiKey_username, apiSecret_password,
                false);
    }

    public MRConsumerImpl(Collection<String> hostPart, final String topic, final String consumerGroup,
            final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret,
            boolean allowSelfSignedCerts) throws MalformedURLException {
        super(hostPart, topic + "::" + consumerGroup + "::" + consumerId);

        fTopic = topic;
        fGroup = consumerGroup;
        fId = consumerId;
        fTimeoutMs = timeoutMs;
        fLimit = limit;
        fFilter = filter;

        fHostSelector = new HostSelector(hostPart);
    }

    @Override
    public Iterable<String> fetch() throws IOException, Exception {
        // fetch with the timeout and limit set in constructor
        return fetch(fTimeoutMs, fLimit);
    }

    @Override
    public Iterable<String> fetch(int timeoutMs, int limit) throws Exception {
        final LinkedList<String> msgs = new LinkedList<>();

        try {
            if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(protocolFlag)) {
                dmeConfigure(timeoutMs, limit);
                try {
                    String reply = sender.sendAndWait(timeoutMs + 10000L);
                    final JSONObject o = getResponseDataInJson(reply);
                    if (o != null) {
                        final JSONArray a = o.getJSONArray("result");
                        if (a != null) {
                            for (int i = 0; i < a.length(); i++) {
                                if (a.get(i) instanceof String)
                                    msgs.add(a.getString(i));
                                else
                                    msgs.add(a.getJSONObject(i).toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    // unexpected response
                    reportProblemWithResponse();
                    log.error("exception: ", e);
                } catch (HttpException e) {
                    throw new IOException(e);
                }
            }

            if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty("Protocol")), timeoutMs, limit);

                try {
                    final JSONObject o = get(urlPath, username, password, protocolFlag);

                    if (o != null) {
                        final JSONArray a = o.getJSONArray("result");
                        if (a != null) {
                            for (int i = 0; i < a.length(); i++) {
                                if (a.get(i) instanceof String)
                                    msgs.add(a.getString(i));
                                else
                                    msgs.add(a.getJSONObject(i).toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    // unexpected response
                    reportProblemWithResponse();
                    log.error("exception: ", e);
                } catch (HttpException e) {
                    throw new IOException(e);
                }
            }

            if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(
                        MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId, props.getProperty("Protocol")),
                        timeoutMs, limit);

                try {
                    final JSONObject o = getAuth(urlPath, authKey, authDate, username, password, protocolFlag);
                    if (o != null) {
                        final JSONArray a = o.getJSONArray("result");
                        if (a != null) {
                            for (int i = 0; i < a.length(); i++) {
                                if (a.get(i) instanceof String)
                                    msgs.add(a.getString(i));
                                else
                                    msgs.add(a.getJSONObject(i).toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    // unexpected response
                    reportProblemWithResponse();
                    log.error("exception: ", e);
                } catch (HttpException e) {
                    throw new IOException(e);
                }
            }

            if (ProtocolTypeConstants.HTTPNOAUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty("Protocol")), timeoutMs, limit);

                try {
                    final JSONObject o = getNoAuth(urlPath);
                    if (o != null) {
                        final JSONArray a = o.getJSONArray("result");
                        if (a != null) {
                            for (int i = 0; i < a.length(); i++) {
                                if (a.get(i) instanceof String)
                                    msgs.add(a.getString(i));
                                else
                                    msgs.add(a.getJSONObject(i).toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    // unexpected response
                    reportProblemWithResponse();
                    log.error("exception: ", e);
                } catch (HttpException e) {
                    throw new IOException(e);
                }
            }
        } catch (JSONException e) {
            // unexpected response
            reportProblemWithResponse();
            log.error("exception: ", e);
        } catch (HttpException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw e;
        }

        return msgs;
    }

    @Override
    public MRConsumerResponse fetchWithReturnConsumerResponse() {
        // fetch with the timeout and limit set in constructor
        return fetchWithReturnConsumerResponse(fTimeoutMs, fLimit);
    }

    @Override
    public MRConsumerResponse fetchWithReturnConsumerResponse(int timeoutMs, int limit) {
        final LinkedList<String> msgs = new LinkedList<>();
        MRConsumerResponse mrConsumerResponse = new MRConsumerResponse();
        try {
            if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(protocolFlag)) {
                dmeConfigure(timeoutMs, limit);

                long timeout = (dme2ReplyHandlerTimeoutMs > 0 && longPollingMs == timeoutMs) ? dme2ReplyHandlerTimeoutMs
                        : (timeoutMs + DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS);
                String reply = sender.sendAndWait(timeout);

                final JSONObject o = getResponseDataInJsonWithResponseReturned(reply);

                if (o != null) {
                    final JSONArray a = o.getJSONArray("result");

                    if (a != null) {
                        for (int i = 0; i < a.length(); i++) {
                            if (a.get(i) instanceof String)
                                msgs.add(a.getString(i));
                            else
                                msgs.add(a.getJSONObject(i).toString());
                        }
                    }
                }
                createMRConsumerResponse(reply, mrConsumerResponse);
            }

            if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty("Protocol")), timeoutMs, limit);

                String response = getResponse(urlPath, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                if (o != null) {
                    final JSONArray a = o.getJSONArray("result");

                    if (a != null) {
                        for (int i = 0; i < a.length(); i++) {
                            if (a.get(i) instanceof String)
                                msgs.add(a.getString(i));
                            else
                                msgs.add(a.getJSONObject(i).toString());
                        }
                    }
                }
                createMRConsumerResponse(response, mrConsumerResponse);
            }

            if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(
                        MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId, props.getProperty("Protocol")),
                        timeoutMs, limit);

                String response = getAuthResponse(urlPath, authKey, authDate, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                if (o != null) {
                    final JSONArray a = o.getJSONArray("result");

                    if (a != null) {
                        for (int i = 0; i < a.length(); i++) {
                            if (a.get(i) instanceof String)
                                msgs.add(a.getString(i));
                            else
                                msgs.add(a.getJSONObject(i).toString());
                        }
                    }
                }
                createMRConsumerResponse(response, mrConsumerResponse);
            }

            if (ProtocolTypeConstants.HTTPNOAUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty("Protocol")), timeoutMs, limit);

                String response = getNoAuthResponse(urlPath, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                if (o != null) {
                    final JSONArray a = o.getJSONArray("result");

                    if (a != null) {
                        for (int i = 0; i < a.length(); i++) {
                            if (a.get(i) instanceof String)
                                msgs.add(a.getString(i));
                            else
                                msgs.add(a.getJSONObject(i).toString());
                        }
                    }
                }
                createMRConsumerResponse(response, mrConsumerResponse);
            }

        } catch (JSONException e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            log.error("json exception: ", e);
        } catch (HttpException e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            log.error("http exception: ", e);
        } catch (DME2Exception e) {
            mrConsumerResponse.setResponseCode(e.getErrorCode());
            mrConsumerResponse.setResponseMessage(e.getErrorMessage());
            log.error("DME2 exception: ", e);
        } catch (Exception e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            log.error("exception: ", e);
        }
        mrConsumerResponse.setActualMessages(msgs);
        return mrConsumerResponse;
    }

    @Override
    protected void reportProblemWithResponse() {
        log.warn("There was a problem with the server response. Blacklisting for 3 minutes.");
        super.reportProblemWithResponse();
        fHostSelector.reportReachabilityProblem(3, TimeUnit.MINUTES);
    }

    private void createMRConsumerResponse(String reply, MRConsumerResponse mrConsumerResponse) {
        if (reply.startsWith("{")) {
            JSONObject jObject = new JSONObject(reply);
            String message = jObject.getString("message");
            int status = jObject.getInt("status");

            mrConsumerResponse.setResponseCode(Integer.toString(status));

            if (null != message) {
                mrConsumerResponse.setResponseMessage(message);
            }
        } else if (reply.startsWith("<")) {
            mrConsumerResponse.setResponseCode(getHTTPErrorResponseCode(reply));
            mrConsumerResponse.setResponseMessage(getHTTPErrorResponseMessage(reply));
        } else {
            mrConsumerResponse.setResponseCode(String.valueOf(HttpStatus.SC_OK));
            mrConsumerResponse.setResponseMessage(SUCCESS_MESSAGE);
        }
    }

    private JSONObject getResponseDataInJson(String response) {
        try {
            JSONTokener jsonTokener = new JSONTokener(response);
            JSONObject jsonObject = null;
            final char firstChar = jsonTokener.next();
            jsonTokener.back();
            if ('[' == firstChar) {
                JSONArray jsonArray = new JSONArray(jsonTokener);
                jsonObject = new JSONObject();
                jsonObject.put("result", jsonArray);
            } else {
                jsonObject = new JSONObject(jsonTokener);
            }

            return jsonObject;
        } catch (JSONException excp) {
            log.error("DMAAP - Error reading response data.", excp);
            return null;
        }
    }

    private JSONObject getResponseDataInJsonWithResponseReturned(String response) {
        JSONTokener jsonTokener = new JSONTokener(response);
        JSONObject jsonObject = null;
        final char firstChar = jsonTokener.next();
        jsonTokener.back();
        if (null != response && response.length() == 0) {
            return null;
        }

        if ('[' == firstChar) {
            JSONArray jsonArray = new JSONArray(jsonTokener);
            jsonObject = new JSONObject();
            jsonObject.put("result", jsonArray);
        } else if ('{' == firstChar) {
            return null;
        } else if ('<' == firstChar) {
            return null;
        } else {
            jsonObject = new JSONObject(jsonTokener);
        }

        return jsonObject;
    }

    private void dmeConfigure(int timeoutMs, int limit) throws IOException, DME2Exception, URISyntaxException {
        this.longPollingMs = timeoutMs;
        String latitude = props.getProperty("Latitude");
        String longitude = props.getProperty("Longitude");
        String version = props.getProperty("Version");
        String serviceName = props.getProperty("ServiceName");
        String env = props.getProperty("Environment");
        String partner = props.getProperty("Partner");
        String routeOffer = props.getProperty("routeOffer");
        String subContextPath = props.getProperty("SubContextPath") + fTopic + "/" + fGroup + "/" + fId;
        String protocol = props.getProperty("Protocol");
        String methodType = props.getProperty("MethodType");
        String dmeuser = props.getProperty("username");
        String dmepassword = props.getProperty("password");
        String contenttype = props.getProperty("contenttype");
        String handlers = props.getProperty("sessionstickinessrequired");

        /**
         * Changes to DME2Client url to use Partner for auto failover between data centers When Partner value is not
         * provided use the routeOffer value for auto failover within a cluster
         */

        String preferredRouteKey = readRoute("preferredRouteKey");

        if (partner != null && !partner.isEmpty() && preferredRouteKey != null && !preferredRouteKey.isEmpty()) {
            url = protocol + "://" + serviceName + "?version=" + version + "&envContext=" + env + "&partner=" + partner
                    + "&routeoffer=" + preferredRouteKey;
        } else if (partner != null && !partner.isEmpty()) {
            url = protocol + "://" + serviceName + "?version=" + version + "&envContext=" + env + "&partner=" + partner;
        } else if (routeOffer != null && !routeOffer.isEmpty()) {
            url = protocol + "://" + serviceName + "?version=" + version + "&envContext=" + env + "&routeoffer="
                    + routeOffer;
        }

        if (timeoutMs != -1)
            url = url + "&timeout=" + timeoutMs;
        if (limit != -1)
            url = url + "&limit=" + limit;

        // Add filter to DME2 Url
        if (fFilter != null && fFilter.length() > 0)
            url = url + "&filter=" + URLEncoder.encode(fFilter, "UTF-8");

        DMETimeOuts = new HashMap<>();
        DMETimeOuts.put("AFT_DME2_EP_READ_TIMEOUT_MS", props.getProperty("AFT_DME2_EP_READ_TIMEOUT_MS"));
        DMETimeOuts.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", props.getProperty("AFT_DME2_ROUNDTRIP_TIMEOUT_MS"));
        DMETimeOuts.put("AFT_DME2_EP_CONN_TIMEOUT", props.getProperty("AFT_DME2_EP_CONN_TIMEOUT"));
        DMETimeOuts.put("Content-Type", contenttype);
        System.setProperty("AFT_LATITUDE", latitude);
        System.setProperty("AFT_LONGITUDE", longitude);
        System.setProperty("AFT_ENVIRONMENT", props.getProperty("AFT_ENVIRONMENT"));

        // SSL changes
        System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "TLSv1.1,TLSv1.2");
        System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
        System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
        // SSL changes

        long dme2PerEndPointTimeoutMs;
        try {
            dme2PerEndPointTimeoutMs = Long.parseLong(props.getProperty("DME2_PER_HANDLER_TIMEOUT_MS"));
            // backward compatibility
            if (dme2PerEndPointTimeoutMs <= 0) {
                dme2PerEndPointTimeoutMs = timeoutMs + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS;
            }
        } catch (NumberFormatException nfe) {
            // backward compatibility
            dme2PerEndPointTimeoutMs = timeoutMs + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS;
            getLog().debug(
                    "DME2_PER_HANDLER_TIMEOUT_MS not set and using default " + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS);
        }

        try {
            dme2ReplyHandlerTimeoutMs = Long.parseLong(props.getProperty("DME2_REPLY_HANDLER_TIMEOUT_MS"));
        } catch (NumberFormatException nfe) {
            try {
                long dme2EpReadTimeoutMs = Long.parseLong(props.getProperty("AFT_DME2_EP_READ_TIMEOUT_MS"));
                long dme2EpConnTimeoutMs = Long.parseLong(props.getProperty("AFT_DME2_EP_CONN_TIMEOUT"));
                dme2ReplyHandlerTimeoutMs = timeoutMs + dme2EpReadTimeoutMs + dme2EpConnTimeoutMs;
                getLog().debug(
                        "DME2_REPLY_HANDLER_TIMEOUT_MS not set and using default from timeoutMs, AFT_DME2_EP_READ_TIMEOUT_MS and AFT_DME2_EP_CONN_TIMEOUT "
                                + dme2ReplyHandlerTimeoutMs);
            } catch (NumberFormatException e) {
                // backward compatibility
                dme2ReplyHandlerTimeoutMs = timeoutMs + DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS;
                getLog().debug("DME2_REPLY_HANDLER_TIMEOUT_MS not set and using default " + dme2ReplyHandlerTimeoutMs);
            }
        }
        // backward compatibility
        if (dme2ReplyHandlerTimeoutMs <= 0) {
            dme2ReplyHandlerTimeoutMs = timeoutMs + DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS;
        }

        sender = new DME2Client(new URI(url), dme2PerEndPointTimeoutMs);
        sender.setAllowAllHttpReturnCodes(true);
        sender.setMethod(methodType);
        sender.setSubContext(subContextPath);
        if (dmeuser != null && dmepassword != null) {
            sender.setCredentials(dmeuser, dmepassword);
        }
        sender.setHeaders(DMETimeOuts);
        sender.setPayload("");
        if (handlers != null && handlers.equalsIgnoreCase("yes")) {
            sender.addHeader("AFT_DME2_EXCHANGE_REQUEST_HANDLERS",
                    props.getProperty("AFT_DME2_EXCHANGE_REQUEST_HANDLERS"));
            sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", props.getProperty("AFT_DME2_EXCHANGE_REPLY_HANDLERS"));
            sender.addHeader("AFT_DME2_REQ_TRACE_ON", props.getProperty("AFT_DME2_REQ_TRACE_ON"));
        } else {
            sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", "com.att.nsa.mr.dme.client.HeaderReplyHandler");
        }
    }

    protected String createUrlPath(String url, int timeoutMs, int limit) throws IOException {
        final StringBuilder contexturl = new StringBuilder(url);
        final StringBuilder adds = new StringBuilder();

        if (timeoutMs > -1) {
            adds.append("timeout=").append(timeoutMs);
        }

        if (limit > -1) {
            if (adds.length() > 0) {
                adds.append("&");
            }
            adds.append("limit=").append(limit);
        }

        if (fFilter != null && fFilter.length() > 0) {
            try {
                if (adds.length() > 0) {
                    adds.append("&");
                }
                adds.append("filter=").append(URLEncoder.encode(fFilter, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("exception at createUrlPath ()  :  ", e);
            }
        }

        if (adds.length() > 0) {
            contexturl.append("?").append(adds.toString());
        }

        return contexturl.toString();
    }

    private String readRoute(String routeKey) {
        try {
            MRClientFactory.prop.load(new FileReader(new File(MRClientFactory.routeFilePath)));
        } catch (Exception ex) {
            log.error("Reply Router Error " + ex);
        }
        return MRClientFactory.prop.getProperty(routeKey);
    }

    public static List<String> stringToList(String str) {
        final LinkedList<String> set = new LinkedList<>();
        if (str != null) {
            final String[] parts = str.trim().split(",");
            for (String part : parts) {
                final String trimmed = part.trim();
                if (trimmed.length() > 0) {
                    set.add(trimmed);
                }
            }
        }
        return set;
    }

    public static String getRouterFilePath() {
        return routerFilePath;
    }

    public static void setRouterFilePath(String routerFilePath) {
        MRSimplerBatchPublisher.routerFilePath = routerFilePath;
    }

    public String getConsumerFilePath() {
        return consumerFilePath;
    }

    public void setConsumerFilePath(String consumerFilePath) {
        this.consumerFilePath = consumerFilePath;
    }

    public String getProtocolFlag() {
        return protocolFlag;
    }

    public void setProtocolFlag(String protocolFlag) {
        this.protocolFlag = protocolFlag;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
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

    public String getfFilter() {
        return fFilter;
    }

    public void setfFilter(String fFilter) {
        this.fFilter = fFilter;
    }
}
