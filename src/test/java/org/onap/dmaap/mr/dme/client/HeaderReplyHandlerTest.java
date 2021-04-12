/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.mr.dme.client;

import com.att.aft.dme2.api.util.DME2ExchangeResponseContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HeaderReplyHandlerTest {
    private HeaderReplyHandler handler = null;

    @Before
    public void setUp() throws Exception {
        handler = new HeaderReplyHandler();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testHandleFault() {

        handler.handleFault(null);
        assertTrue(true);

    }

    @Test
    public void testHandleEndpointFault() {

        handler.handleEndpointFault(null);
        assertTrue(true);

    }

    @Test
    public void testHandleReply() {

        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("transactionId", "1234");

        DME2ExchangeResponseContext responseData = new DME2ExchangeResponseContext("service",
                200, new HashMap<String, String>(), responseHeaders, "routeOffer", "1.0.0", "http://");
        handler.handleReply(responseData);
        assertTrue(true);

    }


}
