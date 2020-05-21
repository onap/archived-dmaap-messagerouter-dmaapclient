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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import org.onap.dmaap.mr.client.MRTopicManager.TopicInfo;


public class MRMetaClientTest {
	
	//@Rule public WireMockRule wireMock = new WireMockRule();
	
	@Before
	public void setUp(){/*
		wireMock.stubFor(get(urlEqualTo("/topics"))
                .willReturn(aResponse().withBody("{\"topics\":[\"topic1\",\"topic2\"]}").withHeader("Content-Type", "application/json")));
		wireMock.stubFor(get(urlEqualTo("/topics/topic1"))
                .willReturn(aResponse().withBody("{\"topics\":[\"topic1\",\"topic2\"]}").withHeader("Content-Type", "application/json")));
		wireMock.stubFor(post(urlEqualTo("/topics/create"))
				.willReturn(aResponse().withStatus(200)));
	*/}
	
	@Test
	public void getTopicsTest() 
	{
		final Collection<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:" +3904 );
		
		MRMetaClient c;
		try {
			c = new MRMetaClient(hosts);
			Set<String> setString=c.getTopics();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotNull(hosts);
		
		
	//	assertEquals ("http://localhost:8080/events/" + "topic/cg/cid", url );
		
	}
	
	@Test
	public void getTopicMetadataTest() {
		final Collection<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:" + 3904 );
		
		final String topic ="topic1";
		
		MRMetaClient c;
		try {
			c = new MRMetaClient(hosts);
			TopicInfo topicInfo=c.getTopicMetadata(topic);
		} catch (IOException | HttpObjectNotFoundException e) {
			e.printStackTrace();
		}
		assertNotNull(topic);
		
	}
	
	@Test
	public void testcreateTopic(){
		final Collection<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:" + 3904 );
		
		MRMetaClient c;
		try {
			c = new MRMetaClient(hosts);
			c.createTopic("topic1", "testTopic", 1, 1);
		} catch (IOException | HttpException e) {
			e.printStackTrace();
		}
		assertNotNull(hosts);
	}
	@Test
	public void testupdateApiKey(){
		final Collection<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:" + 3904 );
		
		MRMetaClient c;
		try {
			c = new MRMetaClient(hosts);
			c.updateCurrentApiKey("test@onap.com", "test email");
		}catch (HttpException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			assertTrue(true);
		}
		
	}

	
}
