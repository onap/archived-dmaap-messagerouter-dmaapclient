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
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;

public class SimpleExampleConsumer {

	static FileWriter routeWriter = null;
	static Properties props = null;
	static FileReader routeReader = null;

	public static void main(String[] args) {
		final Logger LOG = LoggerFactory.getLogger(SimpleExampleConsumer.class);

		long count = 0;
		long nextReport = 5000;

		final long startMs = System.currentTimeMillis();

		try {
			String routeFilePath = "/src/main/resources/dme2/preferredRoute.txt";

			File fo = new File(routeFilePath);
			if (!fo.exists()) {
				routeWriter = new FileWriter(new File(routeFilePath));
			}
			routeReader = new FileReader(new File(routeFilePath));
			props = new Properties();
			final MRConsumer cc = MRClientFactory.createConsumer("/src/main/resources/dme2/consumer.properties");
			int i = 0;
			while (i < 10) {
				Thread.sleep(2);
				i++;
				for (String msg : cc.fetch()) {
					// System.out.println ( "" + (++count) + ": " + msg );
					System.out.println(msg);
				}

				if (count > nextReport) {
					nextReport += 5000;

					final long endMs = System.currentTimeMillis();
					final long elapsedMs = endMs - startMs;
					final double elapsedSec = elapsedMs / 1000.0;
					final double eps = count / elapsedSec;
					System.out.println("Consumed " + count + " in " + elapsedSec + "; " + eps + " eps");
				}
			}
		} catch (Exception x) {
			System.err.println(x.getClass().getName() + ": " + x.getMessage());
			LOG.error("exception: ", x);
		}
	}
}
