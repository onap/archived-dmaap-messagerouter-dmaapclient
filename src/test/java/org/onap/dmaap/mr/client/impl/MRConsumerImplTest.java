/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.test.clients.ProtocolTypeConstants;

public class MRConsumerImplTest extends TestCase {
	@Test
	public void testNullFilter() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(-1).setFilter(null).setApiKey_username(null).setApiSecret_password(null)
                .createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				-1, -1);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid", url);
	}

	@Test
	public void testFilterWithNoTimeoutOrLimit() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(-1).setFilter("filter").setApiKey_username(null)
                .setApiSecret_password(null).createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				-1, -1);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid?filter=filter", url);
	}

	@Test
	public void testTimeoutNoLimitNoFilter() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(30000)
                .setLimit(-1).setFilter(null).setApiKey_username(null).setApiSecret_password(null)
                .createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				30000, -1);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid?timeout=30000", url);
	}

	@Test
	public void testNoTimeoutWithLimitNoFilter() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(100).setFilter(null).setApiKey_username(null).setApiSecret_password(null)
                .createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				-1, 100);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid?limit=100", url);
	}

	@Test
	public void testWithTimeoutWithLimitWithFilter() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(1000)
                .setLimit(400).setFilter("f").setApiKey_username(null).setApiSecret_password(null)
                .createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				1000, 400);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid?timeout=1000&limit=400&filter=f", url);
	}

	@Test
	public void testFilterEncoding() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(-1).setFilter("{ \"foo\"=\"bar\"bar\" }").setApiKey_username(null)
                .setApiSecret_password(null).createMRConsumerImpl();
		final String url = c.createUrlPath(MRConstants.makeConsumerUrl("localhost:8080", "topic", "cg", "cid", "http"),
				-1, -1);
		assertEquals("http://localhost:8080/events/" + "topic/cg/cid?filter=%7B+%22foo%22%3D%22bar%22bar%22+%7D", url);
	}

	@Test
	public void testFetchWithReturnConsumerResponse() throws IOException {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
		Properties properties = new Properties();
		properties.load(
				MRSimplerBatchPublisherTest.class.getClassLoader().getResourceAsStream("dme2/consumer.properties"));

		String routeFilePath = "dme2/preferredRoute.txt";

		File file = new File(MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		properties.put("routeFilePath",
				MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		
		File outFile = new File(file.getParent() + "/consumer_tmp.properties");
		properties.store(new FileOutputStream(outFile), "");

		MRClientFactory.prop=properties;

        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(-1).setFilter("{ \"foo\"=\"bar\"bar\" }").setApiKey_username(null)
                .setApiSecret_password(null).createMRConsumerImpl();
		c.setProps(properties);
		assertNotNull(c.fetchWithReturnConsumerResponse());
		c.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());
		assertNotNull(c.fetchWithReturnConsumerResponse());
		c.setProtocolFlag(ProtocolTypeConstants.HTTPNOAUTH.getValue());
		assertNotNull(c.fetchWithReturnConsumerResponse());
		c.setProtocolFlag(ProtocolTypeConstants.AUTH_KEY.getValue());
		assertNotNull(c.fetchWithReturnConsumerResponse());
		assertTrue(true);
	}

	@Test
	public void testFetch() throws Exception {
		final LinkedList<String> hosts = new LinkedList<String>();
		hosts.add("localhost:8080");
		
		
		Properties properties = new Properties();
		properties.load(
				MRSimplerBatchPublisherTest.class.getClassLoader().getResourceAsStream("dme2/consumer.properties"));

		String routeFilePath = "dme2/preferredRoute.txt";

		File file = new File(MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		properties.put("routeFilePath",
				MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		
		File outFile = new File(file.getParent() + "/consumer_tmp.properties");
		properties.store(new FileOutputStream(outFile), "");

		MRClientFactory.prop=properties;
        final MRConsumerImpl c = new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hosts)
                .setTopic("topic").setConsumerGroup("cg").setConsumerId("cid").setTimeoutMs(-1)
                .setLimit(-1).setFilter("{ \"foo\"=\"bar\"bar\" }").setApiKey_username(null)
                .setApiSecret_password(null).createMRConsumerImpl();
		c.setProps(properties);
		try {
			c.fetch();
		} catch (Exception e) {
			assertTrue(true);
		}
		c.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());
		try {
			c.fetch();
		} catch (Exception e) {
			assertTrue(true);
		}
		c.setProtocolFlag(ProtocolTypeConstants.HTTPNOAUTH.getValue());
		try {
			c.fetch();
		} catch (Exception e) {
			assertTrue(true);
		}
		c.setProtocolFlag(ProtocolTypeConstants.AUTH_KEY.getValue());
		try {
			c.fetch();
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
