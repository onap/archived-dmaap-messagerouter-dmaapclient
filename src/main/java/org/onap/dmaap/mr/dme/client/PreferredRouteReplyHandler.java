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
package org.onap.dmaap.mr.dme.client;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.aft.dme2.api.util.DME2ExchangeFaultContext;
import com.att.aft.dme2.api.util.DME2ExchangeReplyHandler;
import com.att.aft.dme2.api.util.DME2ExchangeResponseContext;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.impl.MRSimplerBatchPublisher;

public class PreferredRouteReplyHandler implements DME2ExchangeReplyHandler {
	private Logger fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	@Override public void handleReply(DME2ExchangeResponseContext responseData) {
		
		if(responseData != null) {
			MRClientFactory.DME2HeadersMap=responseData.getResponseHeaders();
			if (responseData.getResponseHeaders().get("transactionId")!=null)
				
			fLog.info("Transaction_Id : " + responseData.getResponseHeaders().get("transactionId"));
			
			if(responseData.getRouteOffer() != null ){
				routeWriter("preferredRouteKey",responseData.getRouteOffer());
				
			}
		}
}
	
	@Override public void handleFault(DME2ExchangeFaultContext responseData) {
		// TODO Auto-generated method stub
 //StaticCache.getInstance().setHandleFaultInvoked(true);
 }
	@Override public void handleEndpointFault(DME2ExchangeFaultContext responseData) {
		// TODO Auto-generated method stub
		 
	}
	public void routeWriter(String routeKey, String routeValue){

		try(FileWriter	routeWriter=new FileWriter(new File (MRSimplerBatchPublisher.routerFilePath))){
			routeWriter.write(routeKey+"="+routeValue);
			routeWriter.close();
		
		}catch(Exception ex){
			fLog.error("Reply Router Error " + ex);
		}
		
	}
}
