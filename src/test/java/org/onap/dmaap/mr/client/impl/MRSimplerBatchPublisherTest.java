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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRPublisher.message;
import org.onap.dmaap.mr.client.response.MRPublisherResponse;

public class MRSimplerBatchPublisherTest {
	
	File outFile;
	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		properties.load(MRSimplerBatchPublisherTest.class.getClassLoader().getResourceAsStream("dme2/producer.properties"));
		
		String routeFilePath="dme2/preferredRoute.txt";
		
		File file = new File(MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		properties.put("DME2preferredRouterFilePath", MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
		
		outFile = new File(file.getParent() + "/producer_tmp.properties");
		properties.store(new FileOutputStream(outFile), "");
	}

	@Test
	public void testSend() throws IOException, InterruptedException {
				
		final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher)MRClientFactory.createBatchingPublisher(outFile.getPath());	
		
		//publish some messages
		final JSONObject msg1 = new JSONObject ();
		pub.send ( "MyPartitionKey", msg1.toString () );

		final List<message> stuck = pub.close ( 1, TimeUnit.SECONDS );
		if ( stuck.size () > 0 ) {
			System.out.println( stuck.size() + " messages unsent" );
		}
		else
		{
			System.out.println ( "Clean exit; all messages sent." );
		}
		
		
	}

	@Test
	public void testSendBatchWithResponse() throws IOException, InterruptedException {
				
		final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher)MRClientFactory.createBatchingPublisher(outFile.getPath(), true);	
		
		//publish some messages
		final JSONObject msg1 = new JSONObject ();
		pub.send ( "MyPartitionKey", msg1.toString () );
		MRPublisherResponse pubResponse = new MRPublisherResponse();
		pub.setPubResponse(pubResponse);
		
		MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
		Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());
		
	}

}
