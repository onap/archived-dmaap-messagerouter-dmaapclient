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

package com.att.nsa.mr.dme.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;

import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;

import java.util.List;

public class SimpleExampleConsumer {

	private static final Logger logger = LoggerFactory.getLogger(SimpleExampleConsumer.class);

	private SimpleExampleConsumer() {
	}

	public static void main(String[] args) {

		long count = 0;
		long nextReport = 5000;
		String key;

		final long startMs = System.currentTimeMillis();

		try {

			final MRConsumer cc = MRClientFactory.createConsumer("D:\\SG\\consumer.properties");
			while (true) {
				for (String msg : cc.fetch()) {
					logger.debug("Message Received: " + msg);
				}
				// Header for DME2 Call.
				MultivaluedMap<String, Object> headersMap = MRClientFactory.HTTPHeadersMap;
				for (MultivaluedMap.Entry<String, List<Object>> entry : headersMap.entrySet()) {
					key = entry.getKey();
					logger.debug("Header Key " + key);
					logger.debug("Header Value " + headersMap.get(key));
				}
				// Header for HTTP Call.

				Map<String, String> dme2headersMap = MRClientFactory.DME2HeadersMap;
				for (Map.Entry<String, String> entry : dme2headersMap.entrySet()) {
					key = entry.getKey();
					logger.debug("Header Key " + key);
					logger.debug("Header Value " + dme2headersMap.get(key));
				}

				if (count > nextReport) {
					nextReport += 5000;

					final long endMs = System.currentTimeMillis();
					final long elapsedMs = endMs - startMs;
					final double elapsedSec = elapsedMs / 1000.0;
					final double eps = count / elapsedSec;
				}
			}
		} catch (Exception x) {
			logger.error(x.toString());
		}
	}
}
