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

package com.att.nsa.mr.test.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRPublisher.message;

/**
 * A simple publisher that reads from std in, sending each line as a message. 
 * @author author
 */
public class ConsolePublisher
{

	private static final Logger logger = LoggerFactory.getLogger(ConsolePublisher.class);
    private ConsolePublisher() {
    }
	public static void main ( String[] args ) throws IOException //throws IOException, InterruptedException
	{
		// read the hosts(s) from the command line
		final String hosts = args.length > 0 ? args[0] : "aaa.it.att.com,bbb.it.att.com,ccc.it.att.com";

		// read the topic name from the command line
		final String topic = args.length > 1 ? args[1] : "TEST-TOPIC";

		// read the topic name from the command line
		final String partition = args.length > 2 ? args[2] : UUID.randomUUID ().toString ();

		// set up some batch limits and the compression flag
		final int maxBatchSize = 100;
		final long maxAgeMs = 250;
		final boolean withGzip = false;

		// create our publisher
		final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher ( hosts, topic, maxBatchSize, maxAgeMs, withGzip );

		final BufferedReader cin = new BufferedReader ( new InputStreamReader ( System.in ) );
		try
		{
			String line = null;
			while ( ( line = cin.readLine () ) != null )
			{
				pub.send ( partition, line );
			}
		}
		finally
		{
			List<message> leftovers = null;
			try
			{
				leftovers = pub.close ( 10, TimeUnit.SECONDS );
			}
			catch ( InterruptedException e )
			{
                            logger.error( "Send on close interrupted." );
                            Thread.currentThread().interrupt();
			}
			for ( message m : leftovers )
			{
                            logger.error( "Unsent message: " + m.fMsg );
			}
		}
	}
}
