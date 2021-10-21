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

import org.onap.dmaap.mr.client.DmaapClientConst;
import org.onap.dmaap.mr.client.ProtocolType;

public class ValidatorUtil {

    public static final String IS_NEEDED = " is needed";

    private ValidatorUtil() {

    }

    public static void validatePublisher(Properties props) {
        String transportType = props.getProperty(DmaapClientConst.TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String maxBatchSize = props.getProperty(DmaapClientConst.MAX_BATCH_SIZE, "");
        if (maxBatchSize.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.MAX_BATCH_SIZE + IS_NEEDED);
        }
        String maxAgeMs = props.getProperty(DmaapClientConst.MAX_AGE_MS, "");
        if (maxAgeMs.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.MAX_AGE_MS + IS_NEEDED);
        }

        String messageSentThreadOccurrence = props.getProperty(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE);
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            messageSentThreadOccurrence = props.getProperty(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE_OLD);
        }
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE + IS_NEEDED);
        }
        try {
            Integer.parseInt(messageSentThreadOccurrence);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE + " must be an integer");
        }

    }

    public static void validateSubscriber(Properties props) {
        String transportType = props.getProperty(DmaapClientConst.TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String group = props.getProperty(DmaapClientConst.GROUP, "");
        if (group.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.GROUP + IS_NEEDED);
        }
        String id = props.getProperty(DmaapClientConst.ID, "");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Consumer (" + DmaapClientConst.ID + ")" + IS_NEEDED);
        }
    }

    private static void validateForDME2(Properties props) {
        String serviceName = props.getProperty(DmaapClientConst.SERVICE_NAME, "");
        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.SERVICE_NAME + IS_NEEDED);
        }
        String topic = props.getProperty(DmaapClientConst.TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.TOPIC + IS_NEEDED);
        }
        String username = props.getProperty(DmaapClientConst.USERNAME, "");
        if (username.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.USERNAME + IS_NEEDED);
        }
        String password = props.getProperty(DmaapClientConst.PASSWORD, "");
        if (password.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.PASSWORD + IS_NEEDED);
        }
        String dme2preferredRouterFilePath = props.getProperty(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH, "");
        if (dme2preferredRouterFilePath.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH + IS_NEEDED);
        }
        String partner = props.getProperty(DmaapClientConst.PARTNER, "");
        String routeOffer = props.getProperty(DmaapClientConst.ROUTE_OFFER, "");
        if (partner.isEmpty() && routeOffer.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.PARTNER + " or " + DmaapClientConst.ROUTE_OFFER + IS_NEEDED);
        }
        String protocol = props.getProperty(DmaapClientConst.PROTOCOL, "");
        if (protocol.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.PROTOCOL + IS_NEEDED);
        }
        String methodType = props.getProperty(DmaapClientConst.METHOD_TYPE, "");
        if (methodType.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.METHOD_TYPE + IS_NEEDED);
        }
        String contentType = props.getProperty(DmaapClientConst.CONTENT_TYPE, "");
        if (contentType.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.CONTENT_TYPE + IS_NEEDED);
        }
        String latitude = props.getProperty(DmaapClientConst.LATITUDE, "");
        if (latitude.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.LATITUDE + IS_NEEDED);
        }
        String longitude = props.getProperty(DmaapClientConst.LONGITUDE, "");
        if (longitude.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.LONGITUDE + IS_NEEDED);
        }
        String aftEnv = props.getProperty(DmaapClientConst.AFT_ENVIRONMENT, "");
        if (aftEnv.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.AFT_ENVIRONMENT + IS_NEEDED);
        }
        String version = props.getProperty(DmaapClientConst.VERSION, "");
        if (version.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.VERSION + IS_NEEDED);
        }
        String environment = props.getProperty(DmaapClientConst.ENVIRONMENT, "");
        if (environment.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.ENVIRONMENT + IS_NEEDED);
        }
        String subContextPath = props.getProperty(DmaapClientConst.SUB_CONTEXT_PATH, "");
        if (subContextPath.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.SUB_CONTEXT_PATH + IS_NEEDED);
        }
        String sessionstickinessrequired = props.getProperty(DmaapClientConst.SESSION_STICKINESS_REQUIRED, "");
        if (sessionstickinessrequired.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.SESSION_STICKINESS_REQUIRED + IS_NEEDED);
        }
    }

    private static void validateForNonDME2(Properties props) {
        String host = props.getProperty(DmaapClientConst.HOST, "");
        if (host.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.HOST + IS_NEEDED);
        }
        String topic = props.getProperty(DmaapClientConst.TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.TOPIC + IS_NEEDED);
        }
        String contenttype = props.getProperty(DmaapClientConst.CONTENT_TYPE, "");
        if (contenttype.isEmpty()) {
            throw new IllegalArgumentException(DmaapClientConst.CONTENT_TYPE + IS_NEEDED);
        }
        String transportType = props.getProperty(DmaapClientConst.TRANSPORT_TYPE);
        if (!ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(transportType)) {
            String username = props.getProperty(DmaapClientConst.USERNAME, "");
            if (username.isEmpty()) {
                throw new IllegalArgumentException(DmaapClientConst.USERNAME + IS_NEEDED);
            }
            String password = props.getProperty(DmaapClientConst.PASSWORD, "");
            if (password.isEmpty()) {
                throw new IllegalArgumentException(DmaapClientConst.PASSWORD + IS_NEEDED);
            }
        }
        if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(transportType)) {
            String authKey = props.getProperty(DmaapClientConst.AUTH_KEY, "");
            if (authKey.isEmpty()) {
                throw new IllegalArgumentException(DmaapClientConst.AUTH_KEY + IS_NEEDED);
            }
            String authDate = props.getProperty(DmaapClientConst.AUTH_DATE, "");
            if (authDate.isEmpty()) {
                throw new IllegalArgumentException(DmaapClientConst.AUTH_DATE + IS_NEEDED);
            }
        }
    }

}
