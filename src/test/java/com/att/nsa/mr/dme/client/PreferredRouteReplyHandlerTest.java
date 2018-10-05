/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2018 IBM.
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

package com.att.nsa.mr.dme.client;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.aft.dme2.api.util.DME2ExchangeResponseContext;

public class PreferredRouteReplyHandlerTest {
    private PreferredRouteReplyHandler handler = null;

    @Before
    public void setUp() throws Exception {
        handler = new PreferredRouteReplyHandler();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testHandleReply() {
        Map<String, String> responseHeaders= new HashMap<>();
        responseHeaders.put("header1", "value1");
        responseHeaders.put("transactionId", "12345");
        DME2ExchangeResponseContext responseData= new DME2ExchangeResponseContext(null, 0, null, responseHeaders, "testRouteOffer", "1", "");
        
        handler.handleReply(responseData);
    }
    
}
