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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.onap.dmaap.mr.client.HostSelector;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRConsumer;
import org.onap.dmaap.mr.client.ProtocolType;
import org.onap.dmaap.mr.client.response.MRConsumerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRConsumerImpl extends MRBaseClient implements MRConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MRConsumerImpl.class);

    public static final String ROUTER_FILE_PATH = null;

    public String protocolFlag = ProtocolType.DME2.getValue();
    public String consumerFilePath;

    private static final String JSON_RESULT = "result";

    private static final String EXECPTION_MESSAGE = "exception: ";
    private static final String SUCCESS_MESSAGE = "Success";
    private static final long DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS = 10000L;
    private static final long DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS = 10000L;

    private static final String URL_PARAM_ROUTE_OFFER = "routeoffer";
    private static final String URL_PARAM_PARTNER = "partner";
    private static final String URL_PARAM_ENV_CONTEXT = "envContext";
    private static final String URL_PARAM_VERSION = "version";
    private static final String URL_PARAM_FILTER = "filter";
    private static final String URL_PARAM_LIMIT = "limit";
    private static final String URL_PARAM_TIMEOUT = "timeout";

    private static final String USERNAME = "username";
    private static final String SERVICE_NAME = "ServiceName";
    private static final String PARTNER = "Partner";
    private static final String ROUTE_OFFER = "routeOffer";
    private static final String PROTOCOL = "Protocol";
    private static final String METHOD_TYPE = "MethodType";
    private static final String CONTENT_TYPE = "contenttype";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String AFT_ENVIRONMENT = "AFT_ENVIRONMENT";
    private static final String VERSION = "Version";
    private static final String ENVIRONMENT = "Environment";
    private static final String SUB_CONTEXT_PATH = "SubContextPath";
    private static final String SESSION_STICKINESS_REQUIRED = "sessionstickinessrequired";
    private static final String AFT_DME2_EP_READ_TIMEOUT_MS = "AFT_DME2_EP_READ_TIMEOUT_MS";
    private static final String AFT_DME2_ROUNDTRIP_TIMEOUT_MS = "AFT_DME2_ROUNDTRIP_TIMEOUT_MS";
    private static final String AFT_DME2_EP_CONN_TIMEOUT = "AFT_DME2_EP_CONN_TIMEOUT";
    private static final String AFT_DME2_EXCHANGE_REQUEST_HANDLERS = "AFT_DME2_EXCHANGE_REQUEST_HANDLERS";
    private static final String AFT_DME2_EXCHANGE_REPLY_HANDLERS = "AFT_DME2_EXCHANGE_REPLY_HANDLERS";
    private static final String AFT_DME2_REQ_TRACE_ON = "AFT_DME2_REQ_TRACE_ON";
    private static final String DME2_PER_HANDLER_TIMEOUT_MS = "DME2_PER_HANDLER_TIMEOUT_MS";
    private static final String DME2_REPLY_HANDLER_TIMEOUT_MS = "DME2_REPLY_HANDLER_TIMEOUT_MS";

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

    public MRConsumerImpl(MRConsumerImplBuilder builder) throws MalformedURLException {
        super(builder.hostPart,
                builder.topic + "::" + builder.consumerGroup + "::" + builder.consumerId);

        fTopic = builder.topic;
        fGroup = builder.consumerGroup;
        fId = builder.consumerId;
        fTimeoutMs = builder.timeoutMs;
        fLimit = builder.limit;
        fFilter = builder.filter;

        fHostSelector = new HostSelector(builder.hostPart);
    }

    public static class MRConsumerImplBuilder {
        private Collection<String> hostPart;
        private String topic;
        private String consumerGroup;
        private String consumerId;
        private int timeoutMs;
        private int limit;
        private String filter;
        private String apiKey_username;
        private String apiSecret_password;
        private String apiKey;
        private String apiSecret;
        private boolean allowSelfSignedCerts = false;

        public MRConsumerImplBuilder setHostPart(Collection<String> hostPart) {
            this.hostPart = hostPart;
            return this;
        }

        public MRConsumerImplBuilder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public MRConsumerImplBuilder setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
            return this;
        }

        public MRConsumerImplBuilder setConsumerId(String consumerId) {
            this.consumerId = consumerId;
            return this;
        }

        public MRConsumerImplBuilder setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public MRConsumerImplBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public MRConsumerImplBuilder setFilter(String filter) {
            this.filter = filter;
            return this;
        }

        public MRConsumerImplBuilder setApiKey_username(String apiKey_username) {
            this.apiKey_username = apiKey_username;
            return this;
        }

        public MRConsumerImplBuilder setApiSecret_password(String apiSecret_password) {
            this.apiSecret_password = apiSecret_password;
            return this;
        }

        public MRConsumerImplBuilder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public MRConsumerImplBuilder setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public MRConsumerImplBuilder setAllowSelfSignedCerts(boolean allowSelfSignedCerts) {
            this.allowSelfSignedCerts = allowSelfSignedCerts;
            return this;
        }

        public MRConsumerImpl createMRConsumerImpl() throws MalformedURLException {
            return new MRConsumerImpl(this);
        }
    }

    @Override
    public Iterable<String> fetch() throws IOException, Exception {
        // fetch with the timeout and limit set in constructor
        return fetch(fTimeoutMs, fLimit);
    }

    @Override
    public Iterable<String> fetch(int timeoutMs, int limit) throws Exception {
        final LinkedList<String> msgs = new LinkedList<>();

        ProtocolType protocolFlagEnum = null;
        for (ProtocolType type : ProtocolType.values()) {
            if (type.getValue().equalsIgnoreCase(protocolFlag)) {
                protocolFlagEnum = type;
            }
        }
        if (protocolFlagEnum == null) {
            return msgs;
        }

        try {
            switch (protocolFlagEnum) {
                case DME2:
                    dmeConfigure(timeoutMs, limit);
                    String reply = sender.sendAndWait(timeoutMs + 10000L);
                    readJsonData(msgs, getResponseDataInJson(reply));
                    break;
                case AAF_AUTH:
                    String urlAuthPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                            fGroup, fId, props.getProperty(PROTOCOL)), timeoutMs, limit);
                    final JSONObject o = get(urlAuthPath, username, password, protocolFlag);
                    readJsonData(msgs, o);
                    break;
                case AUTH_KEY:
                    final String urlKeyPath = createUrlPath(
                            MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId, props.getProperty(PROTOCOL)),
                            timeoutMs, limit);
                    final JSONObject authObject = getAuth(urlKeyPath, authKey, authDate, username, password, protocolFlag);
                    readJsonData(msgs, authObject);
                    break;
                case HTTPNOAUTH:
                    final String urlNoAuthPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                            fGroup, fId, props.getProperty(PROTOCOL)), timeoutMs, limit);
                    readJsonData(msgs, getNoAuth(urlNoAuthPath));
                    break;
            }
        } catch (JSONException e) {
            // unexpected response
            reportProblemWithResponse();
            logger.error(EXECPTION_MESSAGE, e);
        } catch (HttpException e) {
            throw new IOException(e);
        }

        return msgs;
    }

    private void readJsonData(LinkedList<String> msgs, JSONObject o) {
        if (o != null) {
            final JSONArray a = o.getJSONArray(JSON_RESULT);
            if (a != null) {
                for (int i = 0; i < a.length(); i++) {
                    if (a.get(i) instanceof String) {
                        msgs.add(a.getString(i));
                    } else {
                        msgs.add(a.getJSONObject(i).toString());
                    }
                }
            }
        }
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
            if (ProtocolType.DME2.getValue().equalsIgnoreCase(protocolFlag)) {
                dmeConfigure(timeoutMs, limit);

                long timeout = (dme2ReplyHandlerTimeoutMs > 0 && longPollingMs == timeoutMs) ? dme2ReplyHandlerTimeoutMs
                        : (timeoutMs + DEFAULT_DME2_REPLY_HANDLER_TIMEOUT_MS);
                String reply = sender.sendAndWait(timeout);

                final JSONObject o = getResponseDataInJsonWithResponseReturned(reply);

                readJsonData(msgs, o);
                createMRConsumerResponse(reply, mrConsumerResponse);
            }

            if (ProtocolType.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty(PROTOCOL)), timeoutMs, limit);

                String response = getResponse(urlPath, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                readJsonData(msgs, o);
                createMRConsumerResponse(response, mrConsumerResponse);
            }

            if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(
                        MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId, props.getProperty(PROTOCOL)),
                        timeoutMs, limit);

                String response = getAuthResponse(urlPath, authKey, authDate, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                readJsonData(msgs, o);
                createMRConsumerResponse(response, mrConsumerResponse);
            }

            if (ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(protocolFlag)) {
                final String urlPath = createUrlPath(MRConstants.makeConsumerUrl(fHostSelector.selectBaseHost(), fTopic,
                        fGroup, fId, props.getProperty(PROTOCOL)), timeoutMs, limit);

                String response = getNoAuthResponse(urlPath, username, password, protocolFlag);
                final JSONObject o = getResponseDataInJsonWithResponseReturned(response);
                readJsonData(msgs, o);
                createMRConsumerResponse(response, mrConsumerResponse);
            }

        } catch (JSONException e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            logger.error("json exception: ", e);
        } catch (HttpException e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            logger.error("http exception: ", e);
        } catch (DME2Exception e) {
            mrConsumerResponse.setResponseCode(e.getErrorCode());
            mrConsumerResponse.setResponseMessage(e.getErrorMessage());
            logger.error("DME2 exception: ", e);
        } catch (Exception e) {
            mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
            mrConsumerResponse.setResponseMessage(e.getMessage());
            logger.error(EXECPTION_MESSAGE, e);
        }
        mrConsumerResponse.setActualMessages(msgs);
        return mrConsumerResponse;
    }

    @Override
    protected void reportProblemWithResponse() {
        logger.warn("There was a problem with the server response. Blacklisting for 3 minutes.");
        super.reportProblemWithResponse();
        fHostSelector.reportReachabilityProblem(3, TimeUnit.MINUTES);
    }

    private void createMRConsumerResponse(String reply, MRConsumerResponse mrConsumerResponse) {
        if (reply.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(reply);
            String message = jsonObject.getString("message");
            int status = jsonObject.getInt("status");

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
                jsonObject.put(JSON_RESULT, jsonArray);
            } else {
                jsonObject = new JSONObject(jsonTokener);
            }

            return jsonObject;
        } catch (JSONException excp) {
            logger.error("DMAAP - Error reading response data.", excp);
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
            jsonObject.put(JSON_RESULT, jsonArray);
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
        String latitude = props.getProperty(LATITUDE);
        String longitude = props.getProperty(LONGITUDE);
        String version = props.getProperty(VERSION);
        String serviceName = props.getProperty(SERVICE_NAME);
        String env = props.getProperty(ENVIRONMENT);
        String partner = props.getProperty(PARTNER);
        String routeOffer = props.getProperty(ROUTE_OFFER);
        String subContextPath = props.getProperty(SUB_CONTEXT_PATH) + fTopic + "/" + fGroup + "/" + fId;
        String protocol = props.getProperty(PROTOCOL);
        String methodType = props.getProperty(METHOD_TYPE);
        String dmeuser = props.getProperty(USERNAME);
        String dmepassword = props.getProperty(USERNAME);
        String contenttype = props.getProperty(CONTENT_TYPE);
        String handlers = props.getProperty(SESSION_STICKINESS_REQUIRED);

        /*
         * Changes to DME2Client url to use Partner for auto failover between data centers When Partner value is not
         * provided use the routeOffer value for auto failover within a cluster
         */

        String preferredRouteKey = readRoute("preferredRouteKey");
        StringBuilder contextUrl = new StringBuilder();
        if (partner != null && !partner.isEmpty() && preferredRouteKey != null && !preferredRouteKey.isEmpty()) {
            contextUrl.append(protocol).append("://").append(serviceName).append("?")
                    .append(URL_PARAM_VERSION).append("=").append(version).append("&")
                    .append(URL_PARAM_ENV_CONTEXT).append("=").append(env).append("&")
                    .append(URL_PARAM_PARTNER).append("=").append(partner).append("&")
                    .append(URL_PARAM_ROUTE_OFFER).append("=").append(preferredRouteKey);
        } else if (partner != null && !partner.isEmpty()) {
            contextUrl.append(protocol).append("://").append(serviceName).append("?")
                    .append(URL_PARAM_VERSION).append("=").append(version).append("&")
                    .append(URL_PARAM_ENV_CONTEXT).append("=").append(env).append("&")
                    .append(URL_PARAM_PARTNER).append("=").append(partner);
        } else if (routeOffer != null && !routeOffer.isEmpty()) {
            contextUrl.append(protocol).append("://").append(serviceName).append("?")
                    .append(URL_PARAM_VERSION).append("=").append(version).append("&")
                    .append(URL_PARAM_ENV_CONTEXT).append("=").append(env).append("&")
                    .append(URL_PARAM_ROUTE_OFFER).append("=").append(routeOffer);
        }

        if (timeoutMs != -1) {
            contextUrl.append("&").append(URL_PARAM_TIMEOUT).append("=").append(timeoutMs);
        }
        if (limit != -1) {
            contextUrl.append("&").append(URL_PARAM_LIMIT).append("=").append(limit);
        }

        // Add filter to DME2 Url
        if (fFilter != null && fFilter.length() > 0) {
            contextUrl.append("&").append(URL_PARAM_FILTER).append("=").append(URLEncoder.encode(fFilter, "UTF-8"));
        }

        url = contextUrl.toString();

        DMETimeOuts = new HashMap<>();
        DMETimeOuts.put("AFT_DME2_EP_READ_TIMEOUT_MS", props.getProperty(AFT_DME2_EP_READ_TIMEOUT_MS));
        DMETimeOuts.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", props.getProperty(AFT_DME2_ROUNDTRIP_TIMEOUT_MS));
        DMETimeOuts.put("AFT_DME2_EP_CONN_TIMEOUT", props.getProperty(AFT_DME2_EP_CONN_TIMEOUT));
        DMETimeOuts.put("Content-Type", contenttype);
        System.setProperty("AFT_LATITUDE", latitude);
        System.setProperty("AFT_LONGITUDE", longitude);
        System.setProperty("AFT_ENVIRONMENT", props.getProperty(AFT_ENVIRONMENT));

        // SSL changes
        System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "TLSv1.1,TLSv1.2");
        System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
        System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
        // SSL changes

        long dme2PerEndPointTimeoutMs;
        try {
            dme2PerEndPointTimeoutMs = Long.parseLong(props.getProperty(DME2_PER_HANDLER_TIMEOUT_MS));
            // backward compatibility
            if (dme2PerEndPointTimeoutMs <= 0) {
                dme2PerEndPointTimeoutMs = timeoutMs + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS;
            }
        } catch (NumberFormatException nfe) {
            // backward compatibility
            dme2PerEndPointTimeoutMs = timeoutMs + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS;
            getLog().debug(
                    DME2_PER_HANDLER_TIMEOUT_MS + " not set and using default " + DEFAULT_DME2_PER_ENDPOINT_TIMEOUT_MS);
        }

        try {
            dme2ReplyHandlerTimeoutMs = Long.parseLong(props.getProperty(DME2_REPLY_HANDLER_TIMEOUT_MS));
        } catch (NumberFormatException nfe) {
            try {
                long dme2EpReadTimeoutMs = Long.parseLong(props.getProperty(AFT_DME2_EP_READ_TIMEOUT_MS));
                long dme2EpConnTimeoutMs = Long.parseLong(props.getProperty(AFT_DME2_EP_CONN_TIMEOUT));
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
                    props.getProperty(AFT_DME2_EXCHANGE_REQUEST_HANDLERS));
            sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", props.getProperty(AFT_DME2_EXCHANGE_REPLY_HANDLERS));
            sender.addHeader("AFT_DME2_REQ_TRACE_ON", props.getProperty(AFT_DME2_REQ_TRACE_ON));
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
                logger.error("exception at createUrlPath ()  :  ", e);
            }
        }

        if (adds.length() > 0) {
            contexturl.append("?").append(adds.toString());
        }

        return contexturl.toString();
    }

    private String readRoute(String routeKey) {
        try (InputStream input = new FileInputStream(MRClientFactory.routeFilePath)) {
            MRClientFactory.prop.load(input);
        } catch (Exception ex) {
            logger.error("Reply Router Error " + ex);
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
        return ROUTER_FILE_PATH;
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
        setClientConfig(DmaapClientUtil.getClientConfig(props));
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
