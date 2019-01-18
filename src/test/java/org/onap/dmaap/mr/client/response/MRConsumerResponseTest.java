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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MRConsumerResponseTest {
	private MRConsumerResponse test = null;

	@Before
	public void setUp() throws Exception {
		test = new MRConsumerResponse();

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetResponseCode() {

		test.getResponseCode();
		assertTrue(true);

	}

	@Test
	public void testSetResponseCode() {

		test.setResponseCode("200");
		assertTrue(true);

	}

	@Test
	public void testGetResponseMessage() {

		test.getResponseMessage();
		assertTrue(true);

	}

	@Test
	public void testSetResponseMessage() {

		test.setResponseMessage("responseMessage");
		assertTrue(true);

	}

	@Test
	public void testGetActualMessages() {

		test.getActualMessages();
		assertTrue(true);

	}

	@Test
	public void testSetActualMessages() {

		test.setActualMessages(null);
		assertTrue(true);

	}
}
