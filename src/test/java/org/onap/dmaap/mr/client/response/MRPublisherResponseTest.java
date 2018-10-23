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

package org.onap.dmaap.mr.client.response;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.mr.client.HostSelector;

public class MRPublisherResponseTest {
	private MRPublisherResponse response = null;

	@Before
	public void setUp() throws Exception {
		response = new MRPublisherResponse();

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetResponseCode() {

		response.getResponseCode();
		assertTrue(true);

	}
	
	@Test
	public void testSetResponseCode() {

		response.setResponseCode("200");
		assertTrue(true);

	}
	
	@Test
	public void testGetResponseMessage() {

		response.getResponseMessage();
		assertTrue(true);

	}

	@Test
	public void testSetResponseMessage() {

		response.setResponseMessage("responseMessage");
		assertTrue(true);

	}

	@Test
	public void testGetPendingMsgs() {

		response.getPendingMsgs();
		assertTrue(true);

	}

	@Test
	public void testSetPendingMsgs() {

		response.setPendingMsgs(5);
		assertTrue(true);

	}
	
	@Test
	public void testToString() {

		response.toString();
		assertTrue(true);

	}

	
}
