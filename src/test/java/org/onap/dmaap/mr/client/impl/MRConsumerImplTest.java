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
import java.util.LinkedList;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

import org.onap.dmaap.mr.client.impl.MRConstants;
import org.onap.dmaap.mr.client.impl.MRConsumerImpl;

public class MRConsumerImplTest extends TestCase
{
	@Test
	public void testNullFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, -1 );
		assertEquals ("http://localhost:8080/events/" + "topic/cg/cid", url );
	}

	@Test
	public void testFilterWithNoTimeoutOrLimit () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, "filter", null, null );
		final String url = c.createUrlPath ( MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid" ,"http"),-1, -1 );
		assertEquals ("http://localhost:8080/events/" + "topic/cg/cid?filter=filter", url );
	}

	@Test
	public void testTimeoutNoLimitNoFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", 30000, -1, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), 30000, -1 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?timeout=30000", url );
	}

	@Test
	public void testNoTimeoutWithLimitNoFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, 100, null, null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, 100 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?limit=100", url );
	}

	@Test
	public void testWithTimeoutWithLimitWithFilter () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", 1000, 400, "f", null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid" ,"http"), 1000, 400 );
		assertEquals ("http://localhost:8080/events/"  + "topic/cg/cid?timeout=1000&limit=400&filter=f", url );
	}

	@Test
	public void testFilterEncoding () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, "{ \"foo\"=\"bar\"bar\" }", null, null );
		final String url = c.createUrlPath (MRConstants.makeConsumerUrl ( "localhost:8080", "topic", "cg", "cid","http" ), -1, -1 );
		assertEquals ( "http://localhost:8080/events/"  + "topic/cg/cid?filter=%7B+%22foo%22%3D%22bar%22bar%22+%7D", url );
	}
	
	@Test
	public void testFetchWithReturnConsumerResponse () throws IOException
	{
		final LinkedList<String> hosts = new LinkedList<String> ();
		hosts.add ( "localhost:8080" );
		Properties properties = new Properties();
		properties.load(MRSimplerBatchConsumerTest.class.getClassLoader().getResourceAsStream("dme2/consumer.properties"));
		
		final MRConsumerImpl c = new MRConsumerImpl ( hosts, "topic", "cg", "cid", -1, -1, "{ \"foo\"=\"bar\"bar\" }", null, null );
		c.fetchWithReturnConsumerResponse();
	    c.setProtocolFlag("HTTPAAF");
		c.fetchWithReturnConsumerResponse();
		assertTrue(true);
	}
}
