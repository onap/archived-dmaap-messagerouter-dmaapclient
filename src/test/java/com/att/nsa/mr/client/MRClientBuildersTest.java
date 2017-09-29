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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.mr.client.HostSelector;
import com.att.nsa.mr.client.MRClient;
import com.att.nsa.mr.client.MRClientBuilders;

public class MRClientBuildersTest {

	private Collection<String> hostSet = new ArrayList<String>();
	private MRClientBuilders.ConsumerBuilder builder = null;
	private MRClientBuilders.PublisherBuilder pBuilder = null;
	private MRClientBuilders mrcBuilders = null;

	private String[] hostArray = new String[10];

	@Before
	public void setUp() throws Exception {

	/*	for (int i = 0; i < 10; i++) {
			hostSet.add("host" + (i + 1));
			hostArray[i] = "host" + (i + 1);
		}

		builder = new MRClientBuilders.ConsumerBuilder();

		pBuilder = new MRClientBuilders.PublisherBuilder();

		mrcBuilders = new MRClientBuilders();*/

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testUsingHosts() {

		/*builder.usingHosts("hostList");
		assertTrue(true);*/

	}

	@Test
	public void testUsingHosts2() {

		/*builder.usingHosts(hostSet);
		assertTrue(true);*/

	}

	@Test
	public void testOnTopic() {

/*		builder.onTopic("testTopic");
		assertTrue(true);*/

	}

	@Test
	public void testKnownAs() {

	/*	builder.knownAs("CG1", "23");
		assertTrue(true);
*/
	}

	@Test
	public void testAuthenticatedBy() {

/*		builder.authenticatedBy("apikey", "apisecret");
		assertTrue(true);*/

	}

	@Test
	public void testWaitAtServer() {

//		builder.waitAtServer(100);
//		assertTrue(true);

	}

	@Test
	public void testReceivingAtMost() {

		/*builder.receivingAtMost(100);
		assertTrue(true);*/

	}

	@Test
	public void testWithServerSideFilter() {

		/*builder.withServerSideFilter("filter");
		assertTrue(true);*/

	}

	@Test
	public void testBuild() {

	/*	try {

			builder.build();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}*/

	}

	@Test
	public void testUsingHosts3() {

/*		pBuilder.usingHosts("testTopic");
		assertTrue(true);
*/
	}

	@Test
	public void testUsingHosts4() {

/*		pBuilder.usingHosts(hostArray);
		assertTrue(true);*/

	}

	@Test
	public void testUsingHosts5() {

/*		pBuilder.usingHosts(hostSet);
		assertTrue(true);*/

	}

	@Test
	public void testOnTopic2() {

	/*	pBuilder.onTopic("testTopic");
		assertTrue(true);*/

	}

	@Test
	public void testLimitBatch() {

		/*pBuilder.limitBatch(100, 10);
		assertTrue(true);*/

	}

	@Test
	public void testWithCompresion() {

	/*	pBuilder.withCompresion();
		assertTrue(true);*/

	}

	@Test
	public void testWithoutCompresion() {

	/*	pBuilder.withoutCompresion();
		assertTrue(true);*/

	}

	@Test
	public void testEnableCompresion() {

	/*	pBuilder.enableCompresion(true);
		assertTrue(true);*/

	}

	@Test
	public void testAuthenticatedBy2() {

	/*	pBuilder.authenticatedBy("apikey", "apisecret");
		assertTrue(true);*/

	}

	@Test
	public void testBuild2() {

	/*	try {

			pBuilder.build();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}*/

	}

	@Test
	public void test$testInject() {

		/*try {

			mrcBuilders.$testInject(builder.build());
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}*/

	}

	@Test
	public void test$testInject2() {

//		try {
//
//			mrcBuilders.$testInject(pBuilder.build());
//		} catch (IllegalArgumentException e) {
//			assertTrue(true);
//		}

	}
}
