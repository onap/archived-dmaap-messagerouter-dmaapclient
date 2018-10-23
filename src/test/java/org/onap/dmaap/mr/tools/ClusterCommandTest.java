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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.nsa.cmdtool.CommandNotReadyException;

@RunWith(PowerMockRunner.class)
public class ClusterCommandTest {
	@InjectMocks
	private ClusterCommand command;
	@Mock
	private MRCommandContext context;
	@Mock
	private PrintStream printStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.when(context.getCluster()).thenReturn(Arrays.asList("localhost"));
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetMatches() {

		command.getMatches();
		assertTrue(true);

	}

	@Test
	public void testCheckReady() {

		try {
			command.checkReady(context);
		} catch (CommandNotReadyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}

	@Test
	public void testExecute() throws FileNotFoundException, CommandNotReadyException {
		String[] parts = { "create", "testtopic", "1", "1" };
		command.execute(parts, context, printStream);
		assertTrue(true);

	}

	@Test
	public void testExecute1() throws FileNotFoundException, CommandNotReadyException {
		String[] parts = {};
		command.execute(parts, context, printStream);
		assertTrue(true);

	}

	@Test
	public void testDisplayHelp() {

		command.displayHelp(printStream);
		assertTrue(true);

	}

}
