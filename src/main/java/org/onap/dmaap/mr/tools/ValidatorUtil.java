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
import org.onap.dmaap.mr.client.Prop;
import org.onap.dmaap.mr.client.ProtocolType;

public class ValidatorUtil {

    public static final String IS_NEEDED = " is needed";

    private ValidatorUtil() {

    }

    public static void validatePublisher(Properties props) {
        String transportType = props.getProperty(Prop.TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String maxBatchSize = props.getProperty(Prop.MAX_BATCH_SIZE, "");
        if (maxBatchSize.isEmpty()) {
            throw new IllegalArgumentException(Prop.MAX_BATCH_SIZE + IS_NEEDED);
        }
        String maxAgeMs = props.getProperty(Prop.MAX_AGE_MS, "");
        if (maxAgeMs.isEmpty()) {
            throw new IllegalArgumentException(Prop.MAX_AGE_MS + IS_NEEDED);
        }

        String messageSentThreadOccurrence = props.getProperty(Prop.MESSAGE_SENT_THREAD_OCCURRENCE);
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            messageSentThreadOccurrence = props.getProperty(Prop.MESSAGE_SENT_THREAD_OCCURRENCE_OLD);
        }
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            throw new IllegalArgumentException(Prop.MESSAGE_SENT_THREAD_OCCURRENCE + IS_NEEDED);
        }
        try {
            Integer.parseInt(messageSentThreadOccurrence);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(Prop.MESSAGE_SENT_THREAD_OCCURRENCE + " must be an integer");
        }

    }

    public static void validateSubscriber(Properties props) {
        String transportType = props.getProperty(Prop.TRANSPORT_TYPE);
        if (ProtocolType.DME2.getValue().equalsIgnoreCase(transportType)) {
            validateForDME2(props);
        } else {
            validateForNonDME2(props);
        }
        String group = props.getProperty(Prop.GROUP, "");
        if (group.isEmpty()) {
            throw new IllegalArgumentException(Prop.GROUP + IS_NEEDED);
        }
        String id = props.getProperty(Prop.ID, "");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Consumer (" + Prop.ID + ")" + IS_NEEDED);
        }
    }

    private static void validateForDME2(Properties props) {
        String serviceName = props.getProperty(Prop.SERVICE_NAME, "");
        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException(Prop.SERVICE_NAME + IS_NEEDED);
        }
        String topic = props.getProperty(Prop.TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(Prop.TOPIC + IS_NEEDED);
        }
        String username = props.getProperty(Prop.USERNAME, "");
        if (username.isEmpty()) {
            throw new IllegalArgumentException(Prop.USERNAME + IS_NEEDED);
        }
        String password = props.getProperty(Prop.PASSWORD, "");
        if (password.isEmpty()) {
            throw new IllegalArgumentException(Prop.PASSWORD + IS_NEEDED);
        }
        String dme2preferredRouterFilePath = props.getProperty(Prop.DME2PREFERRED_ROUTER_FILE_PATH, "");
        if (dme2preferredRouterFilePath.isEmpty()) {
            throw new IllegalArgumentException(Prop.DME2PREFERRED_ROUTER_FILE_PATH + IS_NEEDED);
        }
        String partner = props.getProperty(Prop.PARTNER, "");
        String routeOffer = props.getProperty(Prop.ROUTE_OFFER, "");
        if (partner.isEmpty() && routeOffer.isEmpty()) {
            throw new IllegalArgumentException(Prop.PARTNER + " or " + Prop.ROUTE_OFFER + IS_NEEDED);
        }
        String protocol = props.getProperty(Prop.PROTOCOL, "");
        if (protocol.isEmpty()) {
            throw new IllegalArgumentException(Prop.PROTOCOL + IS_NEEDED);
        }
        String methodType = props.getProperty(Prop.METHOD_TYPE, "");
        if (methodType.isEmpty()) {
            throw new IllegalArgumentException(Prop.METHOD_TYPE + IS_NEEDED);
        }
        String contentType = props.getProperty(Prop.CONTENT_TYPE, "");
        if (contentType.isEmpty()) {
            throw new IllegalArgumentException(Prop.CONTENT_TYPE + IS_NEEDED);
        }
        String latitude = props.getProperty(Prop.LATITUDE, "");
        if (latitude.isEmpty()) {
            throw new IllegalArgumentException(Prop.LATITUDE + IS_NEEDED);
        }
        String longitude = props.getProperty(Prop.LONGITUDE, "");
        if (longitude.isEmpty()) {
            throw new IllegalArgumentException(Prop.LONGITUDE + IS_NEEDED);
        }
        String aftEnv = props.getProperty(Prop.AFT_ENVIRONMENT, "");
        if (aftEnv.isEmpty()) {
            throw new IllegalArgumentException(Prop.AFT_ENVIRONMENT + IS_NEEDED);
        }
        String version = props.getProperty(Prop.VERSION, "");
        if (version.isEmpty()) {
            throw new IllegalArgumentException(Prop.VERSION + IS_NEEDED);
        }
        String environment = props.getProperty(Prop.ENVIRONMENT, "");
        if (environment.isEmpty()) {
            throw new IllegalArgumentException(Prop.ENVIRONMENT + IS_NEEDED);
        }
        String subContextPath = props.getProperty(Prop.SUB_CONTEXT_PATH, "");
        if (subContextPath.isEmpty()) {
            throw new IllegalArgumentException(Prop.SUB_CONTEXT_PATH + IS_NEEDED);
        }
        String sessionstickinessrequired = props.getProperty(Prop.SESSION_STICKINESS_REQUIRED, "");
        if (sessionstickinessrequired.isEmpty()) {
            throw new IllegalArgumentException(Prop.SESSION_STICKINESS_REQUIRED + IS_NEEDED);
        }
    }

    private static void validateForNonDME2(Properties props) {
        String host = props.getProperty(Prop.HOST, "");
        if (host.isEmpty()) {
            throw new IllegalArgumentException(Prop.HOST + IS_NEEDED);
        }
        String topic = props.getProperty(Prop.TOPIC, "");
        if (topic.isEmpty()) {
            throw new IllegalArgumentException(Prop.TOPIC + IS_NEEDED);
        }
        String contenttype = props.getProperty(Prop.CONTENT_TYPE, "");
        if (contenttype.isEmpty()) {
            throw new IllegalArgumentException(Prop.CONTENT_TYPE + IS_NEEDED);
        }
        String transportType = props.getProperty(Prop.TRANSPORT_TYPE);
        if (!ProtocolType.HTTPNOAUTH.getValue().equalsIgnoreCase(transportType)) {
            String username = props.getProperty(Prop.USERNAME, "");
            if (username.isEmpty()) {
                throw new IllegalArgumentException(Prop.USERNAME + IS_NEEDED);
            }
            String password = props.getProperty(Prop.PASSWORD, "");
            if (password.isEmpty()) {
                throw new IllegalArgumentException(Prop.PASSWORD + IS_NEEDED);
            }
        }
        if (ProtocolType.AUTH_KEY.getValue().equalsIgnoreCase(transportType)) {
            String authKey = props.getProperty(Prop.AUTH_KEY, "");
            if (authKey.isEmpty()) {
                throw new IllegalArgumentException(Prop.AUTH_KEY + IS_NEEDED);
            }
            String authDate = props.getProperty(Prop.AUTH_DATE, "");
            if (authDate.isEmpty()) {
                throw new IllegalArgumentException(Prop.AUTH_DATE + IS_NEEDED);
            }
        }
    }

}
