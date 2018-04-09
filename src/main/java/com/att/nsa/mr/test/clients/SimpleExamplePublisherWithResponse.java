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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.response.MRPublisherResponse;
	/**
	 *An example of how to use the Java publisher. 
	 * @author author
	 *
	 */
	public class SimpleExamplePublisherWithResponse
	{
		static FileWriter routeWriter= null;
		static Properties props=null;	
		static FileReader routeReader=null;
		
		public static void main(String []args) throws InterruptedException, Exception{
			
			String routeFilePath="src/main/resources/dme2/preferredRoute.txt";
			String msgCount = args[0];
			SimpleExamplePublisherWithResponse publisher = new SimpleExamplePublisherWithResponse();
			File fo= new File(routeFilePath);
			if(!fo.exists()){
					routeWriter=new FileWriter(new File (routeFilePath));
			}	
			routeReader= new FileReader(new File (routeFilePath));
			props= new Properties();
			int i=0;
			while (i< Integer.valueOf(msgCount))
			{
				publisher.publishMessage("src/main/resources/dme2/producer.properties",Integer.valueOf(msgCount));
				i++;
			}
		}
		
		public void publishMessage ( String producerFilePath , int count ) throws IOException, InterruptedException
		{
			// create our publisher
			final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher (producerFilePath,true);	
			// publish some messages
			final JSONObject msg1 = new JSONObject ();

			msg1.put ( "Partition:1", "Message:"+count);
			msg1.put ( "greeting", "Hello  .." );
			
			
			pub.send ( "1", msg1.toString());
			pub.send ( "1", msg1.toString());
			
			MRPublisherResponse res= pub.sendBatchWithResponse();
			
			System.out.println("Pub response->"+res.toString());
		}
		
		
	}
