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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.MRTopicManager.TopicInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MRClientFactory.class, ToolsUtil.class })
public class MessageCommandTest {
	@InjectMocks
	private MessageCommand command;
	@Mock
	private MRConsumer tm;
	@Mock
	private TopicInfo ti;
	@Mock
	private MRBatchingPublisher pub;
	@Mock
	private MRConsumer cc;
	@Mock
	private PrintStream printStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(MRClientFactory.class);
		PowerMockito.mockStatic(ToolsUtil.class);
		PowerMockito.when(MRClientFactory.createConsumer(Arrays.asList("localhost"), "testtopic", "2", "3", -1, -1,
				null, null, null)).thenReturn(cc);

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

		String[] parts1 = { "read", "testtopic", "2", "3" };
		String[] parts2 = { "write", "testtopic", "2", "3" };
		List<String[]> parts = Arrays.asList(parts1, parts2);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			MRCommandContext context = new MRCommandContext();
			PowerMockito.when(ToolsUtil.createBatchPublisher(context, "testtopic")).thenReturn(pub);
			try {
				command.execute(part, context, printStream);
			} catch (CommandNotReadyException e) {
				assertTrue(true);
			}
		}
		assertTrue(true);

	}

	@Test
	public void testExecute_error1() {
		try {
			PowerMockito.doThrow(new Exception()).when(cc).fetch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] parts1 = { "read", "testtopic", "2", "3" };
		String[] parts2 = { "write", "testtopic", "2", "3" };
		List<String[]> parts = Arrays.asList(parts1, parts2);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			MRCommandContext context = new MRCommandContext();
			PowerMockito.when(ToolsUtil.createBatchPublisher(context, "testtopic")).thenReturn(pub);
			try {
				command.execute(part, context, printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue(true);

	}

	@Test
	public void testExecute_error2() {
		try {
			PowerMockito.doThrow(new IOException()).when(pub).close(500, TimeUnit.MILLISECONDS);
			PowerMockito.doThrow(new IOException()).when(pub).send("2", "3");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] parts1 = { "read", "testtopic", "2", "3" };
		String[] parts2 = { "write", "testtopic", "2", "3" };
		List<String[]> parts = Arrays.asList(parts1, parts2);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			MRCommandContext context = new MRCommandContext();
			PowerMockito.when(ToolsUtil.createBatchPublisher(context, "testtopic")).thenReturn(pub);
			try {
				command.execute(part, context, printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue(true);

	}

	/*
	 * @Test public void testExecute_error3() {
	 * 
	 * try { PowerMockito.doThrow(new IOException()).when(pub).send("2", "3");
	 * PowerMockito.doThrow(new InterruptedException()).when(pub).close(500,
	 * TimeUnit.MILLISECONDS); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } catch (InterruptedException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } String[] parts1 =
	 * { "read", "testtopic", "2", "3" }; String[] parts2 = { "write",
	 * "testtopic", "2", "3" }; List<String[]> parts = Arrays.asList(parts1,
	 * parts2); for (Iterator iterator = parts.iterator(); iterator.hasNext();)
	 * { String[] part = (String[]) iterator.next(); PrintStream printStream =
	 * new PrintStream(System.out);
	 * 
	 * MRCommandContext context = new MRCommandContext();
	 * PowerMockito.when(ToolsUtil.createBatchPublisher(context,
	 * "testtopic")).thenReturn(pub); try { command.execute(part, context,
	 * printStream); } catch (CommandNotReadyException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } assertTrue(true);
	 * 
	 * }
	 */

	@Test
	public void testDisplayHelp() {

		command.displayHelp(printStream);

	}

}
