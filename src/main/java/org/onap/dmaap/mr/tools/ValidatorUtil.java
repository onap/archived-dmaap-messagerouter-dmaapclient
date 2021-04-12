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

    private static final String ID = "id";
    private static final String AUTH_KEY = "authKey";
    private static final String AUTH_DATE = "authDate";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String HOST = "host";
    private static final String DME2PREFERRED_ROUTER_FILE_PATH = "DME2preferredRouterFilePath";
    private static final String TOPIC = "topic";
    private static final String TRANSPORT_TYPE = "TransportType";
    private static final String MAX_BATCH_SIZE = "maxBatchSize";
    private static final String MAX_AGE_MS = "maxAgeMs";
    private static final String MESSAGE_SENT_THREAD_OCCURRENCE_OLD = "MessageSentThreadOccurance";
    private static final String MESSAGE_SENT_THREAD_OCCURRENCE = "MessageSentThreadOccurrence";
    private static final String GROUP = "group";
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

    public static final String IS_NEEDED = " is needed";

    private ValidatorUtil() {

    }

    public static void validatePublisher(Properties props) {
        String transportType = props.getProperty(TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String maxBatchSize = props.getProperty(MAX_BATCH_SIZE, "");
        if (maxBatchSize.isEmpty()) {
            throw new IllegalArgumentException(MAX_BATCH_SIZE + IS_NEEDED);
        }
        String maxAgeMs = props.getProperty(MAX_AGE_MS, "");
        if (maxAgeMs.isEmpty()) {
            throw new IllegalArgumentException(MAX_AGE_MS + IS_NEEDED);
        }

        String messageSentThreadOccurrence = props.getProperty(MESSAGE_SENT_THREAD_OCCURRENCE);
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            messageSentThreadOccurrence = props.getProperty(MESSAGE_SENT_THREAD_OCCURRENCE_OLD);
        }
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_SENT_THREAD_OCCURRENCE + IS_NEEDED);
        }
        try {
            Integer.parseInt(messageSentThreadOccurrence);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MESSAGE_SENT_THREAD_OCCURRENCE + " must be an integer");
        }

    }

    public static void validateSubscriber(Properties props) {
        String transportType = props.getProperty(TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String group = props.getProperty(GROUP, "");
        if (group.isEmpty()) {
            throw new IllegalArgumentException(GROUP + IS_NEEDED);
        }
        String id = props.getProperty(ID, "");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Consumer (" + ID + ")" + IS_NEEDED);
        }
    }

    private static void validateForDME2(Properties props) {
        String serviceName = props.getProperty(SERVICE_NAME, "");
        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException(SERVICE_NAME + IS_NEEDED);
        }
        String topic = props.getProperty(TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(TOPIC + IS_NEEDED);
        }
        String username = props.getProperty(USERNAME, "");
        if (username.isEmpty()) {
            throw new IllegalArgumentException(USERNAME + IS_NEEDED);
        }
        String password = props.getProperty(PASSWORD, "");
        if (password.isEmpty()) {
            throw new IllegalArgumentException(PASSWORD + IS_NEEDED);
        }
        String dme2preferredRouterFilePath = props.getProperty(DME2PREFERRED_ROUTER_FILE_PATH, "");
        if (dme2preferredRouterFilePath.isEmpty()) {
            throw new IllegalArgumentException(DME2PREFERRED_ROUTER_FILE_PATH + IS_NEEDED);
        }
        String partner = props.getProperty(PARTNER, "");
        String routeOffer = props.getProperty(ROUTE_OFFER, "");
        if (partner.isEmpty() && routeOffer.isEmpty()) {
            throw new IllegalArgumentException(PARTNER + " or " + ROUTE_OFFER + IS_NEEDED);
        }
        String protocol = props.getProperty(PROTOCOL, "");
        if (protocol.isEmpty()) {
            throw new IllegalArgumentException(PROTOCOL + IS_NEEDED);
        }
        String methodType = props.getProperty(METHOD_TYPE, "");
        if (methodType.isEmpty()) {
            throw new IllegalArgumentException(METHOD_TYPE + IS_NEEDED);
        }
        String contentType = props.getProperty(CONTENT_TYPE, "");
        if (contentType.isEmpty()) {
            throw new IllegalArgumentException(CONTENT_TYPE + IS_NEEDED);
        }
        String latitude = props.getProperty(LATITUDE, "");
        if (latitude.isEmpty()) {
            throw new IllegalArgumentException(LATITUDE + IS_NEEDED);
        }
        String longitude = props.getProperty(LONGITUDE, "");
        if (longitude.isEmpty()) {
            throw new IllegalArgumentException(LONGITUDE + IS_NEEDED);
        }
        String aftEnv = props.getProperty(AFT_ENVIRONMENT, "");
        if (aftEnv.isEmpty()) {
            throw new IllegalArgumentException(AFT_ENVIRONMENT + IS_NEEDED);
        }
        String version = props.getProperty(VERSION, "");
        if (version.isEmpty()) {
            throw new IllegalArgumentException(VERSION + IS_NEEDED);
        }
        String environment = props.getProperty(ENVIRONMENT, "");
        if (environment.isEmpty()) {
            throw new IllegalArgumentException(ENVIRONMENT + IS_NEEDED);
        }
        String subContextPath = props.getProperty(SUB_CONTEXT_PATH, "");
        if (subContextPath.isEmpty()) {
            throw new IllegalArgumentException(SUB_CONTEXT_PATH + IS_NEEDED);
        }
        String sessionstickinessrequired = props.getProperty(SESSION_STICKINESS_REQUIRED, "");
        if (sessionstickinessrequired.isEmpty()) {
            throw new IllegalArgumentException(SESSION_STICKINESS_REQUIRED + IS_NEEDED);
        }
    }

    private static void validateForNonDME2(Properties props) {
        String host = props.getProperty(HOST, "");
        if (host.isEmpty()) {
            throw new IllegalArgumentException(HOST + IS_NEEDED);
        }
        String topic = props.getProperty(TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(TOPIC + IS_NEEDED);
        }
        String contenttype = props.getProperty(CONTENT_TYPE, "");
        if (contenttype.isEmpty()) {
            throw new IllegalArgumentException(CONTENT_TYPE + IS_NEEDED);
        }
        String transportType = props.getProperty(TRANSPORT_TYPE);
        if (!ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(transportType)) {
            String username = props.getProperty(USERNAME, "");
            if (username.isEmpty()) {
                throw new IllegalArgumentException(USERNAME + IS_NEEDED);
            }
            String password = props.getProperty(PASSWORD, "");
            if (password.isEmpty()) {
                throw new IllegalArgumentException(PASSWORD + IS_NEEDED);
            }
        }
        if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(transportType)) {
            String authKey = props.getProperty(AUTH_KEY, "");
            if (authKey.isEmpty()) {
                throw new IllegalArgumentException(AUTH_KEY + IS_NEEDED);
            }
            String authDate = props.getProperty(AUTH_DATE, "");
            if (authDate.isEmpty()) {
                throw new IllegalArgumentException(AUTH_DATE + IS_NEEDED);
            }
        }
    }

}
