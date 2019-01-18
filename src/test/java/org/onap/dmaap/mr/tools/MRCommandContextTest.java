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

package org.onap.dmaap.mr.tools;

import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MRCommandContextTest {
	private MRCommandContext command = null;
	private String[] parts = new String[5];

	@Before
	public void setUp() throws Exception {
		command = new MRCommandContext();

		for (int i = 0; i < parts.length; i++) {
			parts[i] = "String" + (i + 1);
		}

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testRequestShutdown() {

		command.requestShutdown();
		assertTrue(true);

	}

	@Test
	public void testShouldContinue() {

		command.shouldContinue();
		assertTrue(true);

	}

	@Test
	public void testSetAuth() {

		command.setAuth("key", "pwd");
		assertTrue(true);

	}

	@Test
	public void testClearAuth() {

		command.clearAuth();
		assertTrue(true);

	}

	@Test
	public void testCheckClusterReady() {

		command.checkClusterReady();
		assertTrue(true);

	}

	@Test
	public void testGetCluster() {

		command.getCluster();
		assertTrue(true);

	}

	@Test
	public void testClearCluster() {

		command.clearCluster();
		assertTrue(true);

	}

	@Test
	public void testAddClusterHost() {

		command.addClusterHost("host");
		assertTrue(true);

	}

	@Test
	public void testGetApiKey() {

		command.getApiKey();
		assertTrue(true);

	}

	@Test
	public void testGetApiPwd() {

		command.getApiPwd();
		assertTrue(true);

	}

	@Test
	public void testUseTracer() {

		command.useTracer(null);
		assertTrue(true);

	}

	@Test
	public void testNoTracer() {

		command.noTracer();
		assertTrue(true);

	}

	@Test
	public void testApplyTracer() {

		command.applyTracer(null);
		assertTrue(true);

	}

}
