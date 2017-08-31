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

package org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.test.clients;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRBatchingPublisher;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRClientFactory;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRPublisher.message;

/**
 * An example of how to use the Java publisher. 
 * @author author
 */
public class SimpleExamplePublisher
{
	static FileWriter routeWriter= null;
	static Properties props=null;	
	static FileReader routeReader=null;
	public void publishMessage ( String producerFilePath  ) throws IOException, InterruptedException, Exception
	{
				
		// create our publisher
		final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher (producerFilePath);	
		// publish some messages
		final JSONObject msg1 = new JSONObject ();
		msg1.put ( "Name", "Sprint" );
		//msg1.put ( "greeting", "Hello  .." );
		pub.send ( "First cambria messge" );
		pub.send ( "MyPartitionKey", msg1.toString () );

		final JSONObject msg2 = new JSONObject ();
		//msg2.put ( "mrclient1", System.currentTimeMillis () );
		
        
		// ...

		// close the publisher to make sure everything's sent before exiting. The batching
		// publisher interface allows the app to get the set of unsent messages. It could
		// write them to disk, for example, to try to send them later.
		final List<message> stuck = pub.close ( 20, TimeUnit.SECONDS );
		if ( stuck.size () > 0 )
		{
			System.err.println ( stuck.size() + " messages unsent" );
		}
		else
		{
			System.out.println ( "Clean exit; all messages sent." );
		}
	}
	
	public static void main(String []args) throws InterruptedException, Exception{

		String routeFilePath="/src/main/resources/dme2/preferredRoute.txt";

		SimpleExamplePublisher publisher = new SimpleExamplePublisher();

		
		File fo= new File(routeFilePath);
		if(!fo.exists()){
				routeWriter=new FileWriter(new File (routeFilePath));
		}	
		routeReader= new FileReader(new File (routeFilePath));
		props= new Properties();
		publisher.publishMessage("/src/main/resources/dme2/producer.properties");
		}
	
}

