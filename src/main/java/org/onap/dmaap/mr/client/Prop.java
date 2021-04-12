/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2021 Orange Intellectual Property. All rights reserved.
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

package org.onap.dmaap.mr.client;

public final class Prop {

    private Prop() {
    }

    public static final String ID = "id";
    public static final String AUTH_KEY = "authKey";
    public static final String AUTH_DATE = "authDate";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String FILTER = "filter";
    public static final String HOST = "host";
    public static final String DME2PREFERRED_ROUTER_FILE_PATH = "DME2preferredRouterFilePath";
    public static final String TOPIC = "topic";
    public static final String TRANSPORT_TYPE = "TransportType";
    public static final String MAX_BATCH_SIZE = "maxBatchSize";
    public static final String MAX_AGE_MS = "maxAgeMs";
    public static final String MESSAGE_SENT_THREAD_OCCURRENCE_OLD = "MessageSentThreadOccurance";
    public static final String MESSAGE_SENT_THREAD_OCCURRENCE = "MessageSentThreadOccurrence";
    public static final String GROUP = "group";
    public static final String SERVICE_NAME = "ServiceName";
    public static final String PARTNER = "Partner";
    public static final String ROUTE_OFFER = "routeOffer";
    public static final String PROTOCOL = "Protocol";
    public static final String METHOD_TYPE = "MethodType";
    public static final String CONTENT_TYPE = "contenttype";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String AFT_ENVIRONMENT = "AFT_ENVIRONMENT";
    public static final String VERSION = "Version";
    public static final String ENVIRONMENT = "Environment";
    public static final String SUB_CONTEXT_PATH = "SubContextPath";
    public static final String SESSION_STICKINESS_REQUIRED = "sessionstickinessrequired";
    public static final String PARTITION = "partition";
    public static final String COMPRESS = "compress";
    public static final String TIMEOUT = "timeout";
    public static final String LIMIT = "limit";
    public static final String AFT_DME2_EP_READ_TIMEOUT_MS = "AFT_DME2_EP_READ_TIMEOUT_MS";
    public static final String AFT_DME2_ROUNDTRIP_TIMEOUT_MS = "AFT_DME2_ROUNDTRIP_TIMEOUT_MS";
    public static final String AFT_DME2_EP_CONN_TIMEOUT = "AFT_DME2_EP_CONN_TIMEOUT";
    public static final String AFT_DME2_EXCHANGE_REQUEST_HANDLERS = "AFT_DME2_EXCHANGE_REQUEST_HANDLERS";
    public static final String AFT_DME2_EXCHANGE_REPLY_HANDLERS = "AFT_DME2_EXCHANGE_REPLY_HANDLERS";
    public static final String AFT_DME2_REQ_TRACE_ON = "AFT_DME2_REQ_TRACE_ON";
    public static final String DME2_PER_HANDLER_TIMEOUT_MS = "DME2_PER_HANDLER_TIMEOUT_MS";
    public static final String DME2_REPLY_HANDLER_TIMEOUT_MS = "DME2_REPLY_HANDLER_TIMEOUT_MS";

}
