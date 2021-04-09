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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.CommandNotReadyException;
import org.onap.dmaap.mr.client.MRClient.MRApiException;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRIdentityManager;
import org.onap.dmaap.mr.client.MRIdentityManager.ApiKey;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest({ MRClientFactory.class })
public class ApiKeyCommandTest {

	@InjectMocks
	private ApiKeyCommand command;
	@Mock
	private MRIdentityManager tm;
	@Mock
	private ApiKey ti;
	@Mock
	private ApiKey key;
	@Mock
	private ApiCredential ac;
	@Mock
	private PrintStream printStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(MRClientFactory.class);
		PowerMockito.when(MRClientFactory.createIdentityManager(Arrays.asList("localhost"), null, null)).thenReturn(tm);
		PowerMockito.when(tm.getApiKey("testtopic")).thenReturn(key);
		PowerMockito.when(tm.createApiKey("testtopic", "1")).thenReturn(ac);

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

		String[] parts1 = { "create", "testtopic", "1" };
		String[] parts2 = { "list", "testtopic", "1" };
		String[] parts3 = { "revoke", "write", "read" };
		List<String[]> parts = Arrays.asList(parts1, parts2, parts3);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			try {
				command.execute(part, new MRCommandContext(), printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(true);

		}
	}

	 @Test
	public void testExecute_error1() throws HttpObjectNotFoundException, HttpException, MRApiException, IOException {
		PowerMockito.when(tm.getApiKey("testtopic")).thenThrow(new IOException("error"));
		String[] parts1 = { "create", "testtopic", "1" };
		String[] parts2 = { "list", "testtopic", "1" };
		String[] parts3 = { "revoke", "write", "read" };
		List<String[]> parts = Arrays.asList(parts1, parts2, parts3);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			try {
				command.execute(part, new MRCommandContext(), printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(true);
		}

	}

	 @Test
	public void testExecute_error2() throws HttpObjectNotFoundException, HttpException, MRApiException, IOException {
		PowerMockito.when(tm.getApiKey("testtopic")).thenThrow(new MRApiException("error"));
		String[] parts1 = { "create", "testtopic", "1" };
		String[] parts2 = { "list", "testtopic", "1" };
		String[] parts3 = { "revoke", "write", "read" };
		List<String[]> parts = Arrays.asList(parts1, parts2, parts3);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			try {
				command.execute(part, new MRCommandContext(),printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(true);

		}
	}

	 @Test
	public void testExecute_error3() throws HttpObjectNotFoundException, HttpException, MRApiException, IOException {
		PowerMockito.when(tm.getApiKey("testtopic")).thenThrow(new HttpException(500, "error"));
		String[] parts1 = { "create", "testtopic", "1" };
		String[] parts2 = { "list", "testtopic", "1" };
		String[] parts3 = { "revoke", "write", "read" };
		List<String[]> parts = Arrays.asList(parts1, parts2, parts3);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			try {
				command.execute(part, new MRCommandContext(), printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(true);
		}

	}

	 @Test
	public void testExecute_error4() throws HttpObjectNotFoundException, HttpException, MRApiException, IOException {
		PowerMockito.when(tm.getApiKey("testtopic")).thenThrow(new HttpObjectNotFoundException("error"));
		String[] parts1 = { "create", "testtopic", "1" };
		String[] parts2 = { "list", "testtopic", "1" };
		String[] parts3 = { "revoke", "write", "read" };
		List<String[]> parts = Arrays.asList(parts1, parts2, parts3);
		for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
			String[] part = (String[]) iterator.next();

			try {
				command.execute(part, new MRCommandContext(), printStream);
			} catch (CommandNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(true);

		}
	}

	 @Test
	public void testDisplayHelp() {

		command.displayHelp(printStream);
		assertTrue(true);

	}

}
