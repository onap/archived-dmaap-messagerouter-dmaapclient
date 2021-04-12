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
package org.onap.dmaap.mr.client;

import org.onap.dmaap.mr.client.response.MRConsumerResponse;

import java.io.IOException;

public interface MRConsumer extends MRClient {
    /**
     * Fetch a set of messages. The consumer's timeout and message limit are used if set in the constructor call.
     *
     * @return a set of messages
     * @throws IOException
     */
    Iterable<String> fetch() throws IOException, Exception;

    /**
     * Fetch a set of messages with an explicit timeout and limit for this call. These values
     * override any set in the constructor call.
     *
     * @param timeoutMs The amount of time in milliseconds that the server should keep the connection
     *                  open while waiting for message traffic. Use -1 for default timeout (controlled on the server-side).
     * @param limit     A limit on the number of messages returned in a single call. Use -1 for no limit.
     * @return a set messages
     * @throws IOException if there's a problem connecting to the server
     */
    Iterable<String> fetch(int timeoutMs, int limit) throws IOException, Exception;

    MRConsumerResponse fetchWithReturnConsumerResponse();


    MRConsumerResponse fetchWithReturnConsumerResponse(int timeoutMs, int limit);
}
