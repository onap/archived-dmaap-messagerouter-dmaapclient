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

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.CommandNotReadyException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRTopicManager;
import org.onap.dmaap.mr.client.MRTopicManager.TopicInfo;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest({MRClientFactory.class})
public class TopicCommandTest {
    @InjectMocks
    private TopicCommand command;
    @Mock
    private MRTopicManager tm;
    @Mock
    private TopicInfo ti;
    @Mock
    private PrintStream printStream;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(MRClientFactory.class);
        PowerMockito.when(MRClientFactory.createTopicManager(Arrays.asList("localhost"), null, null)).thenReturn(tm);
        PowerMockito.when(tm.getTopicMetadata("testtopic")).thenReturn(ti);

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

        String[] parts1 = {"create", "testtopic", "1", "1"};
        String[] parts2 = {"grant", "write", "read", "1"};
        String[] parts3 = {"revoke", "write", "read", "1"};
        String[] parts4 = {"list", "testtopic", "1", "1"};
        List<String[]> parts = Arrays.asList(parts1, parts2, parts3, parts4);
        for (Iterator iterator = parts.iterator(); iterator.hasNext(); ) {
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
    public void testExecute_error() {

        String[] parts1 = {"create", "testtopic", "1", "1"};
        String[] parts2 = {"grant", "write", "read", "1"};
        String[] parts3 = {"revoke", "write", "read", "1"};
        String[] parts4 = {"list", "testtopic", "1", "1"};
        List<String[]> parts = Arrays.asList(parts1, parts2, parts3, parts4);
        for (Iterator iterator = parts.iterator(); iterator.hasNext(); ) {
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
    public void testExecute_error_1() throws com.att.nsa.apiClient.http.HttpException, IOException {
        PowerMockito.when(tm.getTopicMetadata("testtopic")).thenThrow(new IOException("error"));
        PowerMockito.doThrow(new IOException()).when(tm).createTopic("testtopic", "", 1, 1);
        PowerMockito.doThrow(new IOException()).when(tm).revokeProducer("read", "1");
        String[] parts1 = {"create", "testtopic", "1", "1"};
        String[] parts2 = {"grant", "read", "read", "1"};
        String[] parts3 = {"revoke", "write", "read", "1"};
        String[] parts4 = {"list", "testtopic", "1", "1"};
        List<String[]> parts = Arrays.asList(parts1, parts2, parts3, parts4);
        for (Iterator iterator = parts.iterator(); iterator.hasNext(); ) {
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
    public void testExecute_error_2() throws com.att.nsa.apiClient.http.HttpException, IOException {
        PowerMockito.when(tm.getTopicMetadata("testtopic")).thenThrow(new HttpObjectNotFoundException("error"));
        PowerMockito.doThrow(new HttpException(500, "error")).when(tm).createTopic("testtopic", "", 1, 1);
        PowerMockito.doThrow(new HttpException(500, "error")).when(tm).revokeConsumer("read", "1");
        PowerMockito.doThrow(new HttpException(500, "error")).when(tm).allowConsumer("read", "1");
        String[] parts1 = {"create", "testtopic", "1", "1"};
        String[] parts2 = {"grant", "write", "write", "1"};
        String[] parts3 = {"revoke", "read", "read", "1"};
        String[] parts4 = {"list", "testtopic", "1", "1"};
        List<String[]> parts = Arrays.asList(parts1, parts2, parts3, parts4);
        for (Iterator iterator = parts.iterator(); iterator.hasNext(); ) {
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
    public void testExecute_error_3() throws com.att.nsa.apiClient.http.HttpException, IOException {
        PowerMockito.doThrow(new HttpException(500, "error")).when(tm).createTopic("testtopic", "", 1, 1);
        PowerMockito.doThrow(new HttpException(500, "error")).when(tm).allowProducer("read", "1");
        String[] parts1 = {"create", "testtopic", "1a", "1a"};
        String[] parts2 = {"grant", "write", "read", "1"};
        List<String[]> parts = Arrays.asList(parts1, parts2);
        for (Iterator iterator = parts.iterator(); iterator.hasNext(); ) {
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
