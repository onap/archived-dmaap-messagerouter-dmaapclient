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
package org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.dme.client;

import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.aft.dme2.api.util.DME2ExchangeRequestContext;
import com.att.aft.dme2.api.util.DME2ExchangeRequestHandler;

public class PreferredRouteRequestHandler implements DME2ExchangeRequestHandler {
	private Logger fLog = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void handleRequest(DME2ExchangeRequestContext requestData) {

		if (requestData != null) {

			requestData.setPreferredRouteOffer(readRoute("preferredRouteKey"));
		}
	}

	public String readRoute(String routeKey) {

		try {

			MRClientFactory.prop.load(MRClientFactory.routeReader);

		} catch (Exception ex) {
			fLog.error("Request Router Error " + ex.toString());
		}
		return MRClientFactory.prop.getProperty(routeKey);
	}
}
