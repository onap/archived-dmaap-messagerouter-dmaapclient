/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
 *  Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;


public class MRClientFactoryTest {

    private Collection<String> hostSet = new ArrayList<String>();

    private String[] hostArray = new String[10];

    @Before
    public void setUp() throws Exception {

        for (int i = 0; i < 10; i++) {
            hostSet.add("host" + (i + 1));
            hostArray[i] = "host" + (i + 1);
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateConsumer() {

        MRClientFactory.createConsumer("hostList hostList2", "testTopic");
        assertTrue(true);

    }

    @Test
    public void testCreateConsumer2() {

        MRClientFactory.createConsumer(hostSet, "testTopic");
        assertTrue(true);

    }

    @Test
    public void testCreateConsumer3() {
        MRClientFactory.createConsumer(hostSet, "testTopic", "filter");
        assertTrue(true);
    }

    @Test
    public void testCreateConsumer4() {
        MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22");
        assertTrue(true);
    }

    @Test
    public void testCreateConsumer5() {
        MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22", 100, 100);
        assertTrue(true);
    }

    @Test
    public void testCreateConsumer6() {
        MRClientFactory.createConsumer("hostList", "testTopic", "CG1", "22", 100, 100, "filter", "apikey", "apisecret");
        assertTrue(true);
    }

    @Test
    public void testCreateConsumer7() {
        MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22", 100, 100, "filter", "apikey", "apisecret");
        assertTrue(true);
    }

    @Test
    public void testCreateSimplePublisher() {
        MRClientFactory.createSimplePublisher("hostList", "testTopic");
        assertTrue(true);
    }

    @Test
    public void testCreateBatchingPublisher1() {
        MRClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 10);
        assertTrue(true);
    }

    @Test
    public void testCreateBatchingPublisher2() {
        MRClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 10, true);
        assertTrue(true);
    }

    @Test
    public void testCreateBatchingPublisher3() {
        MRClientFactory.createBatchingPublisher(hostArray, "testTopic", 100, 10, true);
        assertTrue(true);
    }

    @Test
    public void testCreateBatchingPublisher4() {
        MRClientFactory.createBatchingPublisher(hostSet, "testTopic", 100, 10, true);
        assertTrue(true);
    }

    @Test
    public void testCreateBatchingPublisher5() {
        MRClientFactory.createBatchingPublisher("host", "testTopic", "username", "password", 100, 10, true,
                "protocolFlag");
        assertTrue(true);
    }

    @Test(expected = IOException.class)
    public void testCreateBatchingPublisher6() throws IOException {
        MRClientFactory.createBatchingPublisher("/producer");
    }

    @Test(expected = IOException.class)
    public void testCreateBatchingPublisher7() throws IOException {
        MRClientFactory.createBatchingPublisher("/producer", true);
    }

    @Test
    public void testCreateIdentityManager() {
        MRClientFactory.createIdentityManager(hostSet, "apikey", "apisecret");
        assertTrue(true);
    }

    @Test
    public void testCreateTopicManager() {
        MRClientFactory.createTopicManager(hostSet, "apikey", "apisecret");
        assertTrue(true);
    }

    @Test(expected = IOException.class)
    public void testCreateConsumer8() throws IOException {
        MRClientFactory.createConsumer("/consumer");
    }

    @Test
    public void testCreateConsumer9() {
        MRClientFactory.createConsumer("host", "topic", "username", "password", "group", "23", "protocolFlag",
                "/consumer", 1, 2);
        assertTrue(true);
    }

    @Test
    public void testCreateConsumer10() {
        MRClientFactory.createConsumer("host", "topic", "username", "password", "group", "23", 1, 2, "protocolFlag",
                "/consumer");
        assertTrue(true);
    }

    @Test
    public void test$testInject() {
        MRClientFactory.$testInject(null);
        assertTrue(true);
    }

}