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
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRConsumer;

public class MRSimplerBatchConsumerTest {
	
	File outFile;
	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		properties.load(MRSimplerBatchConsumerTest.class.getClassLoader().getResourceAsStream("dme2/consumer.properties"));
		
		String routeFilePath="dme2/preferredRoute.txt";
		
		File file = new File(MRSimplerBatchConsumerTest.class.getClassLoader().getResource(routeFilePath).getFile());
		properties.put("DME2preferredRouterFilePath", MRSimplerBatchConsumerTest.class.getClassLoader().getResource(routeFilePath).getFile());
		
		outFile = new File(file.getParent() + "/consumer_tmp.properties");
		properties.store(new FileOutputStream(outFile), "");
	}

	@Test
	public void testSend() throws IOException, InterruptedException {
				
		final MRConsumer cc = MRClientFactory.createConsumer(outFile.getPath());	
		
		try {
			for(String msg : cc.fetch()){
				System.out.println(msg);
			}
		} catch (Exception e) {
			System.err.println ( e.getClass().getName () + ": " + e.getMessage () );
		}		
		
	}
	

}
