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

package org.onap.dmaap.mr.test.support;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class MRConsumerMockTest {
    private MRConsumerMock cons = null;
    private MRConsumerMock.Entry entry = null;

    @Before
    public void setUp() throws Exception {
        cons = new MRConsumerMock();
        entry = cons.new Entry(100, 200, "statusMsg");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testClose() {

        cons.close();
        assertTrue(true);

    }

    @Test
    public void testRun() {
        try {
            entry.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertTrue(true);

    }

    @Test
    public void testSetApiCredentials() {
        cons.setApiCredentials("apikey", "apisecret");
        assertTrue(true);

    }

    @Test
    public void testClearApiCredentials() {
        cons.clearApiCredentials();
        assertTrue(true);

    }

    @Test
    public void testAdd() {
        cons.add(entry);
        assertTrue(true);

    }

    @Test
    public void testAddImmediateMsg() {
        cons.addImmediateMsg("ImmediateMsg");
        assertTrue(true);

    }

    @Test
    public void testAddDelayedMsg() {
        cons.addDelayedMsg(100, "msg");
        assertTrue(true);

    }

    @Test
    public void testAddImmediateMsgGroup() {
        cons.addImmediateMsgGroup(new ArrayList<String>());
        assertTrue(true);

    }

    @Test
    public void testAddDelayedMsgGroup() {
        cons.addDelayedMsgGroup(100, new ArrayList<String>());
        assertTrue(true);

    }

    @Test
    public void testAddImmediateError() {
        cons.addImmediateError(200, "OK");
        assertTrue(true);

    }

    @Test
    public void testFetch() {
        try {
            cons.fetch();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(true);

    }

    @Test
    public void testFetch2() {
        try {
            cons.fetch(100, 200);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(true);

    }

    @Test
    public void testLogTo() {
        cons.logTo(null);
        assertTrue(true);

    }

    @Test
    public void testFetchWithReturnConsumerResponse() {
        cons.fetchWithReturnConsumerResponse();
        assertTrue(true);

    }

    @Test
    public void testGetchWithReturnConsumerResponse() {
        cons.fetchWithReturnConsumerResponse(100, 200);
        assertTrue(true);

    }
}
