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

package org.onap.dmaap.mr.tools;

import java.util.Properties;
import org.onap.dmaap.mr.client.ProtocolType;

public class ValidatorUtil {

    private static final String PROP_ID = "id";
    private static final String PROP_AUTH_KEY = "authKey";
    private static final String PROP_AUTH_DATE = "authDate";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_USERNAME = "username";
    private static final String PROP_HOST = "host";
    private static final String PROP_DME2PREFERRED_ROUTER_FILE_PATH = "DME2preferredRouterFilePath";
    private static final String PROP_TOPIC = "topic";
    private static final String PROP_TRANSPORT_TYPE = "TransportType";
    private static final String PROP_MAX_BATCH_SIZE = "maxBatchSize";
    private static final String PROP_MAX_AGE_MS = "maxAgeMs";
    private static final String PROP_MESSAGE_SENT_THREAD_OCCURRENCE_OLD = "MessageSentThreadOccurance";
    private static final String PROP_MESSAGE_SENT_THREAD_OCCURRENCE = "MessageSentThreadOccurrence";
    private static final String PROP_GROUP = "group";
    private static final String PROP_SERVICE_NAME = "ServiceName";
    private static final String PROP_PARTNER = "Partner";
    private static final String PROP_ROUTE_OFFER = "routeOffer";
    private static final String PROP_PROTOCOL = "Protocol";
    private static final String PROP_METHOD_TYPE = "MethodType";
    private static final String PROP_CONTENT_TYPE = "contenttype";
    private static final String PROP_LATITUDE = "Latitude";
    private static final String PROP_LONGITUDE = "Longitude";
    private static final String PROP_AFT_ENVIRONMENT = "AFT_ENVIRONMENT";
    private static final String PROP_VERSION = "Version";
    private static final String PROP_ENVIRONMENT = "Environment";
    private static final String PROP_SUB_CONTEXT_PATH = "SubContextPath";
    private static final String PROP_SESSION_STICKINESS_REQUIRED = "sessionstickinessrequired";

    public static final String IS_NEEDED = " is needed";

    private ValidatorUtil() {

    }

    public static void validatePublisher(Properties props) {
        String transportType = props.getProperty(PROP_TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String maxBatchSize = props.getProperty(PROP_MAX_BATCH_SIZE, "");
        if (maxBatchSize.isEmpty()) {
            throw new IllegalArgumentException(PROP_MAX_BATCH_SIZE + IS_NEEDED);
        }
        String maxAgeMs = props.getProperty(PROP_MAX_AGE_MS, "");
        if (maxAgeMs.isEmpty()) {
            throw new IllegalArgumentException(PROP_MAX_AGE_MS + IS_NEEDED);
        }

        String messageSentThreadOccurrence = props.getProperty(PROP_MESSAGE_SENT_THREAD_OCCURRENCE);
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            messageSentThreadOccurrence = props.getProperty(PROP_MESSAGE_SENT_THREAD_OCCURRENCE_OLD);
        }
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            throw new IllegalArgumentException(PROP_MESSAGE_SENT_THREAD_OCCURRENCE + IS_NEEDED);
        }
        try {
            Integer.parseInt(messageSentThreadOccurrence);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(PROP_MESSAGE_SENT_THREAD_OCCURRENCE + " must be an integer");
        }

    }

    public static void validateSubscriber(Properties props) {
        String transportType = props.getProperty(PROP_TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String group = props.getProperty(PROP_GROUP, "");
        if (group.isEmpty()) {
            throw new IllegalArgumentException(PROP_GROUP + IS_NEEDED);
        }
        String id = props.getProperty(PROP_ID, "");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Consumer (" + PROP_ID + ")" + IS_NEEDED);
        }
    }

    private static void validateForDME2(Properties props) {
        String serviceName = props.getProperty(PROP_SERVICE_NAME, "");
        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException(PROP_SERVICE_NAME + IS_NEEDED);
        }
        String topic = props.getProperty(PROP_TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(PROP_TOPIC + IS_NEEDED);
        }
        String username = props.getProperty(PROP_USERNAME, "");
        if (username.isEmpty()) {
            throw new IllegalArgumentException(PROP_USERNAME + IS_NEEDED);
        }
        String password = props.getProperty(PROP_PASSWORD, "");
        if (password.isEmpty()) {
            throw new IllegalArgumentException(PROP_PASSWORD + IS_NEEDED);
        }
        String dme2preferredRouterFilePath = props.getProperty(PROP_DME2PREFERRED_ROUTER_FILE_PATH, "");
        if (dme2preferredRouterFilePath.isEmpty()) {
            throw new IllegalArgumentException(PROP_DME2PREFERRED_ROUTER_FILE_PATH + IS_NEEDED);
        }
        String partner = props.getProperty(PROP_PARTNER, "");
        String routeOffer = props.getProperty(PROP_ROUTE_OFFER, "");
        if (partner.isEmpty() && routeOffer.isEmpty()) {
            throw new IllegalArgumentException(PROP_PARTNER + " or " + PROP_ROUTE_OFFER + IS_NEEDED);
        }
        String protocol = props.getProperty(PROP_PROTOCOL, "");
        if (protocol.isEmpty()) {
            throw new IllegalArgumentException(PROP_PROTOCOL + IS_NEEDED);
        }
        String methodType = props.getProperty(PROP_METHOD_TYPE, "");
        if (methodType.isEmpty()) {
            throw new IllegalArgumentException(PROP_METHOD_TYPE + IS_NEEDED);
        }
        String contentType = props.getProperty(PROP_CONTENT_TYPE, "");
        if (contentType.isEmpty()) {
            throw new IllegalArgumentException(PROP_CONTENT_TYPE + IS_NEEDED);
        }
        String latitude = props.getProperty(PROP_LATITUDE, "");
        if (latitude.isEmpty()) {
            throw new IllegalArgumentException(PROP_LATITUDE + IS_NEEDED);
        }
        String longitude = props.getProperty(PROP_LONGITUDE, "");
        if (longitude.isEmpty()) {
            throw new IllegalArgumentException(PROP_LONGITUDE + IS_NEEDED);
        }
        String aftEnv = props.getProperty(PROP_AFT_ENVIRONMENT, "");
        if (aftEnv.isEmpty()) {
            throw new IllegalArgumentException(PROP_AFT_ENVIRONMENT + IS_NEEDED);
        }
        String version = props.getProperty(PROP_VERSION, "");
        if (version.isEmpty()) {
            throw new IllegalArgumentException(PROP_VERSION + IS_NEEDED);
        }
        String environment = props.getProperty(PROP_ENVIRONMENT, "");
        if (environment.isEmpty()) {
            throw new IllegalArgumentException(PROP_ENVIRONMENT + IS_NEEDED);
        }
        String subContextPath = props.getProperty(PROP_SUB_CONTEXT_PATH, "");
        if (subContextPath.isEmpty()) {
            throw new IllegalArgumentException(PROP_SUB_CONTEXT_PATH + IS_NEEDED);
        }
        String sessionstickinessrequired = props.getProperty(PROP_SESSION_STICKINESS_REQUIRED, "");
        if (sessionstickinessrequired.isEmpty()) {
            throw new IllegalArgumentException(PROP_SESSION_STICKINESS_REQUIRED + IS_NEEDED);
        }
    }

    private static void validateForNonDME2(Properties props) {
        String host = props.getProperty(PROP_HOST, "");
        if (host.isEmpty()) {
            throw new IllegalArgumentException(PROP_HOST + IS_NEEDED);
        }
        String topic = props.getProperty(PROP_TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(PROP_TOPIC + IS_NEEDED);
        }
        String contenttype = props.getProperty(PROP_CONTENT_TYPE, "");
        if (contenttype.isEmpty()) {
            throw new IllegalArgumentException(PROP_CONTENT_TYPE + IS_NEEDED);
        }
        String transportType = props.getProperty(PROP_TRANSPORT_TYPE);
        if (!ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(transportType)) {
            String username = props.getProperty(PROP_USERNAME, "");
            if (username.isEmpty()) {
                throw new IllegalArgumentException(PROP_USERNAME + IS_NEEDED);
            }
            String password = props.getProperty(PROP_PASSWORD, "");
            if (password.isEmpty()) {
                throw new IllegalArgumentException(PROP_PASSWORD + IS_NEEDED);
            }
        }
        if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(transportType)) {
            String authKey = props.getProperty(PROP_AUTH_KEY, "");
            if (authKey.isEmpty()) {
                throw new IllegalArgumentException(PROP_AUTH_KEY + IS_NEEDED);
            }
            String authDate = props.getProperty(PROP_AUTH_DATE, "");
            if (authDate.isEmpty()) {
                throw new IllegalArgumentException(PROP_AUTH_DATE + IS_NEEDED);
            }
        }
    }

}
