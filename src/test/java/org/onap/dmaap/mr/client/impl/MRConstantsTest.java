/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *
 *******************************************************************************/

package org.onap.dmaap.mr.client.impl;

import junit.framework.TestCase;
import org.apache.http.HttpHost;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MRConstantsTest extends TestCase {
    @Test
    public void testPlainHost() throws IOException {
        final String rawTopic = "bar";
        final String result = MRConstants.makeUrl(rawTopic);
        assertEquals("/events/" + "bar", result);
    }

    @Test
    public void testHostWithProtocol() throws IOException {
        final String rawTopic = "bar";
        final String result = MRConstants.makeUrl(rawTopic);
        assertEquals("/events/" + "bar", result);
    }

    @Test
    public void testHostWithProtocolAndPort() throws IOException {
        final String rawTopic = "bar";
        final String result = MRConstants.makeUrl(rawTopic);
        assertEquals("/events/" + "bar", result);
    }

    @Test
    public void testHostWithPort() throws IOException {
        final String rawTopic = "bar";
        final String result = MRConstants.makeUrl(rawTopic);
        assertEquals("/events/" + "bar", result);
    }

    @Test
    public void testHostWithPortAndEscapedTopic() throws IOException {
        final String rawTopic = "bar?bell";
        final String result = MRConstants.makeUrl(rawTopic);
        assertEquals("/events/" + "bar%3Fbell", result);
    }

    @Test
    public void testConsumerPlainHost() throws IOException {
        final String rawTopic = "bar";
        final String rawGroup = "group";
        final String rawId = "id";
        final String result = MRConstants.makeConsumerUrl(rawTopic, rawGroup, rawId);
        assertEquals("/events/" + "bar/group/id", result);
    }

    @Test
    public void testCreateHostList() {
        final ArrayList<String> in = new ArrayList<String>();
        in.add("foo");
        in.add("bar");
        in.add("baz:80");

        final Collection<HttpHost> hosts = MRConstants.createHostsList(in);
        assertEquals(3, hosts.size());

        final Iterator<HttpHost> it = hosts.iterator();
        final HttpHost first = it.next();
        assertEquals(MRConstants.STD_MR_SERVICE_PORT, first.getPort());
        assertEquals("foo", first.getHostName());

        final HttpHost second = it.next();
        assertEquals(MRConstants.STD_MR_SERVICE_PORT, second.getPort());
        assertEquals("bar", second.getHostName());

        final HttpHost third = it.next();
        assertEquals(80, third.getPort());
        assertEquals("baz", third.getHostName());
    }

    private static final String[][] hostTests =
            {
                    {"host", "host", "" + MRConstants.STD_MR_SERVICE_PORT},
                    {":oops", null, "-1"},
                    {"host:1.3", null, "-1"},
                    {"host:13", "host", "13"},
                    {"host:", "host", "" + MRConstants.STD_MR_SERVICE_PORT},
            };

    @Test
    public void testHostParse() {
        for (String[] test : hostTests) {
            final String hostIn = test[0];
            final String hostOut = test[1];
            final int portOut = Integer.parseInt(test[2]);

            try {
                final HttpHost hh = MRConstants.hostForString(hostIn);
                assertEquals(hostOut, hh.getHostName());
                assertEquals(portOut, hh.getPort());
            } catch (IllegalArgumentException x) {
                assertEquals(-1, portOut);
            }
        }
    }
}
