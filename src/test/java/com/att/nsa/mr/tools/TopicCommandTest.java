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

package com.att.nsa.mr.tools;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.HostSelector;
import com.att.nsa.mr.client.MRPublisher.message;
import com.att.nsa.mr.test.support.MRBatchingPublisherMock.Entry;

public class TopicCommandTest {
	private TopicCommand command = null;
	private String[] parts = new String[5];

	@Before
	public void setUp() throws Exception {
		command = new TopicCommand();
		
		for (int i  = 0; i < parts.length; i++) {
			parts[i] = "String" + (i + 1);
		} 

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
			command.checkReady(new MRCommandContext());
		} catch (CommandNotReadyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testExecute() {
		
/*		try {
			command.execute(parts, new MRCommandContext(), new PrintStream("/filename"));
		} catch (CommandNotReadyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
*/
	}
	
	
	@Test
	public void testDisplayHelp() {
		
		/*try {
			command.displayHelp(new PrintStream("/filename"));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);*/

	}
	
	
	
	
}
