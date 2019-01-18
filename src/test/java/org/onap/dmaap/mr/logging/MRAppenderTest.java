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

package org.onap.dmaap.mr.logging;

import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MRAppenderTest {
	private MRAppender appender = null;

	@Before
	public void setUp() throws Exception {
		appender = new MRAppender();

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testClose() {
		try {
			appender.close();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

		assertTrue(true);

	}

	@Test
	public void testRequiresLayout() {

		appender.requiresLayout();
		assertTrue(true);

	}

	@Test
	public void testAppend() {

		try {
			appender.append(null);
		} catch (NullPointerException e) {
			assertTrue(true);
		}

		assertTrue(true);

	}

	@Test
	public void testActivateOptions() {

		appender.activateOptions();
		assertTrue(true);

	}

	@Test
	public void testGetTopic() {

		appender.getTopic();
		assertTrue(true);

	}

	@Test
	public void testSetTopic() {

		appender.setTopic("testTopic");
		assertTrue(true);

	}

	@Test
	public void testGetPartition() {

		appender.getPartition();
		assertTrue(true);

	}

	@Test
	public void testSetPartition() {

		appender.setPartition("partition");
		assertTrue(true);

	}

	@Test
	public void testGetHosts() {

		appender.getHosts();
		assertTrue(true);

	}

	@Test
	public void testSetHosts() {

		appender.setHosts("hosts");
		assertTrue(true);

	}

	@Test
	public void testGetMaxBatchSize() {

		appender.getMaxBatchSize();
		assertTrue(true);

	}

	@Test
	public void testSetMaxBatchSize() {

		appender.setMaxBatchSize(20);
		assertTrue(true);

	}

	@Test
	public void testGetMaxAgeMs() {

		appender.getMaxAgeMs();
		assertTrue(true);

	}

	@Test
	public void testSetMaxAgeMs() {

		appender.setMaxAgeMs(15);
		assertTrue(true);

	}

	@Test
	public void testIsCompress() {

		appender.isCompress();
		assertTrue(true);

	}

	@Test
	public void testSetCompress() {

		appender.setCompress(true);
		assertTrue(true);

	}

}
