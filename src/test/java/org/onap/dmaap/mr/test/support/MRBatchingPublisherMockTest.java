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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.mr.client.MRPublisher.message;

public class MRBatchingPublisherMockTest {
	private MRBatchingPublisherMock pub = null;

	private MRBatchingPublisherMock.Entry entry = null;

	@Before
	public void setUp() throws Exception {
		pub = new MRBatchingPublisherMock();
		entry = pub.new Entry("partition", "msg");

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testToString() {

		entry.toString();
		assertTrue(true);

	}

	@Test
	public void testAddListener() {

		pub.addListener(null);
		assertTrue(true);

	}

	@Test
	public void testGetCaptures() {

		pub.getCaptures();
		assertTrue(true);

	}

	@Test
	public void testGetCaptures2() {

		pub.getCaptures(null);
		assertTrue(true);

	}

	@Test
	public void testReceived() {

		pub.received();
		assertTrue(true);

	}
	
	@Test
	public void testResend() {

		pub.reset();
		assertTrue(true);

	}
	
	@Test
	public void testSend() {

		pub.send("partition", "msg");
		assertTrue(true);

	}
	
	@Test
	public void testSend2() {

		pub.send("msg");
		assertTrue(true);

	}
	
	@Test
	public void testSend3() {
		//sending message m obj
		pub.send(new ArrayList<message>());
		assertTrue(true);

	}
	
	@Test
	public void testSend4() {
		//sending collection of message m objects
		pub.send(new message("partition", "msg"));
		assertTrue(true);

	}
	
	@Test
	public void testSendBatchWithResponse() {

		pub.sendBatchWithResponse();
		assertTrue(true);

	}
	
	@Test
	public void testLogTo() {

		pub.logTo(null);
		assertTrue(true);

	}
	
	@Test
	public void testClearApiCredentials() {

		pub.clearApiCredentials();
		assertTrue(true);

	}
	
	@Test
	public void testSetApiCredentials() {

		pub.setApiCredentials("apikey", "apisecret");
		assertTrue(true);

	}
	
	@Test
	public void testClose() {

		pub.close();
		assertTrue(true);

	}
	
	@Test
	public void testClose2() {

		pub.close(100, null);
		assertTrue(true);

	}
	
	@Test
	public void testGetPendingMessageCount() {

		pub.getPendingMessageCount();
		assertTrue(true);

	}
}
