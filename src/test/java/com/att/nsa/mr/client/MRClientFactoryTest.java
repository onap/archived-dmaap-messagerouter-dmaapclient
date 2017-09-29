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

package com.att.nsa.mr.client;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.mr.client.HostSelector;
import com.att.nsa.mr.client.MRClient;
import com.att.nsa.mr.client.MRClientBuilders;
import com.att.nsa.mr.client.MRClientFactory;

public class MRClientFactoryTest {

	private Collection<String> hostSet = new ArrayList<String>();

	private MRClientFactory factory = null;

	private String[] hostArray = new String[10];

	@Before
	public void setUp() throws Exception {

		for (int i = 0; i < 10; i++) {
			hostSet.add("host" + (i + 1));
			hostArray[i] = "host" + (i + 1);
		}

		factory = new MRClientFactory();

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testCreateConsumer() {

	/*	MRClientFactory.createConsumer("hostList hostList2", "testTopic");
		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer2() {

	/*	MRClientFactory.createConsumer(hostSet, "testTopic");
		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer3() {

	/*	MRClientFactory.createConsumer(hostSet, "testTopic", "filter");
		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer4() {

//		MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22");
//		assertTrue(true);

	}

	@Test
	public void testCreateConsumer5() {

/*		MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22", 100, 100);
		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer6() {

	/*	MRClientFactory.createConsumer("hostList", "testTopic", "CG1", "22", 100, 100, "filter", "apikey", "apisecret");
		assertTrue(true);
*/
	}

	@Test
	public void testCreateConsumer7() {

/*		MRClientFactory.createConsumer(hostSet, "testTopic", "CG1", "22", 100, 100, "filter", "apikey", "apisecret");
		assertTrue(true);*/

	}

	@Test
	public void testCreateSimplePublisher() {

/*		MRClientFactory.createSimplePublisher("hostList", "testTopic");
		assertTrue(true);*/

	}

	@Test
	public void testCreateBatchingPublisher1() {
/*
		MRClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 10);
		assertTrue(true);*/

	}

	@Test
	public void testCreateBatchingPublisher2() {
/*
		MRClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 10, true);
		assertTrue(true);
*/
	}

	@Test
	public void testCreateBatchingPublisher3() {
/*
		MRClientFactory.createBatchingPublisher(hostArray, "testTopic", 100, 10, true);
		assertTrue(true);*/

	}

	@Test
	public void testCreateBatchingPublisher4() {

//		MRClientFactory.createBatchingPublisher(hostSet, "testTopic", 100, 10, true);
//		assertTrue(true);

	}

	@Test
	public void testCreateBatchingPublisher5() {
//
//		MRClientFactory.createBatchingPublisher("host", "testTopic", "username", "password", 100, 10, true,
//				"protocolFlag", "/producer");
//		assertTrue(true);

	}

	@Test
	public void testCreateBatchingPublisher6() {
//
//		try {
//			MRClientFactory.createBatchingPublisher("/producer");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertTrue(true);

	}

	@Test
	public void testCreateBatchingPublisher7() {

	/*	try {
			MRClientFactory.createBatchingPublisher("/producer", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);*/

	}

	@Test
	public void testCreateIdentityManager() {
/*
		MRClientFactory.createIdentityManager(hostSet, "apikey", "apisecret");

		assertTrue(true);*/

	}

	@Test
	public void testCreateTopicManager() {

	/*	MRClientFactory.createTopicManager(hostSet, "apikey", "apisecret");

		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer8() {
/*
		try {
			MRClientFactory.createConsumer("/consumer");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);*/

	}

	@Test
	public void testCreateConsumer9() {

//		MRClientFactory.createConsumer("host", "topic", "username", "password", "group", "23", "protocolFlag",
//				"/consumer", 1, 2);
//
//		assertTrue(true);

	}

	@Test
	public void testCreateConsumer10() {

//		MRClientFactory.createConsumer("host", "topic", "username", "password", "group", "23", 1, 2, "protocolFlag",
//				"/consumer");
//
//		assertTrue(true);

	}
	
	@Test
	public void test$testInject() {

/*		MRClientFactory.$testInject(null);
		assertTrue(true);*/

	}

}