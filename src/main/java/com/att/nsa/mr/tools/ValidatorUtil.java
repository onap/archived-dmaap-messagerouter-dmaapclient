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
package com.att.nsa.mr.tools;

import java.util.Properties;

import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

public class ValidatorUtil {

	public  static void validatePublisher(Properties props) {
		String transportType = props.getProperty("TransportType");
		if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(transportType)) {
			validateForDME2(props);
		} else {
			 validateForNonDME2(props);
		}
		String maxBatchSize  = props.getProperty("maxBatchSize");
		if (maxBatchSize == null || maxBatchSize.isEmpty()) {
			throw new IllegalArgumentException ( "maxBatchSize is needed" );
		}
		String maxAgeMs  = props.getProperty("maxAgeMs");
		if (maxAgeMs == null || maxAgeMs.isEmpty()) {
			throw new IllegalArgumentException ( "maxAgeMs is needed" );
		}
		String messageSentThreadOccurance  = props.getProperty("MessageSentThreadOccurance");
		if (messageSentThreadOccurance == null || messageSentThreadOccurance.isEmpty()) {
			throw new IllegalArgumentException ( "MessageSentThreadOccurance is needed" );
		}
		
	}

	public  static void validateSubscriber(Properties props) {
		String transportType = props.getProperty("TransportType");
		if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(transportType)) {
			validateForDME2(props);
		} else {
			 validateForNonDME2(props);
		}
		String group  = props.getProperty("group");
		if (group == null || group.isEmpty()) {
			throw new IllegalArgumentException ( "group is needed" );
		}
		String id  = props.getProperty("id");
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException ( "Consumer (Id)  is needed" );
		}
	}
	
	private  static void validateForDME2(Properties props) {
		String serviceName  = props.getProperty("ServiceName");
		if (serviceName == null || serviceName.isEmpty()) {
			throw new IllegalArgumentException ( "Servicename is needed" );
		}
		String topic  = props.getProperty("topic");
		if (topic == null || topic.isEmpty()) {
			throw new IllegalArgumentException ( "topic is needed" );
		}
		String username  = props.getProperty("username");
		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException ( "username is needed" );
		}
		String password  = props.getProperty("password");
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException ( "password is needed" );
		}
		String dME2preferredRouterFilePath  = props.getProperty("DME2preferredRouterFilePath");
		if (dME2preferredRouterFilePath == null || dME2preferredRouterFilePath.isEmpty()) {
			throw new IllegalArgumentException ( "DME2preferredRouterFilePath is needed" );
		}
		String partner  = props.getProperty("Partner");
		String routeOffer  = props.getProperty("routeOffer");
		if ((partner == null || partner.isEmpty()) && (routeOffer == null || routeOffer.isEmpty())) {
			throw new IllegalArgumentException ( "Partner or  routeOffer is needed" );
		}
		String protocol  = props.getProperty("Protocol");
		if (protocol == null || protocol.isEmpty()) {
			throw new IllegalArgumentException ( "Protocol is needed" );
		}
		String methodType  = props.getProperty("MethodType");
		if (methodType == null || methodType.isEmpty()) {
			throw new IllegalArgumentException ( "MethodType is needed" );
		}
		String contenttype  = props.getProperty("contenttype");
		if (contenttype == null || contenttype.isEmpty()) {
			throw new IllegalArgumentException ( "contenttype is needed" );
		}
		String latitude  = props.getProperty("Latitude");
		if (latitude == null || latitude.isEmpty()) {
			throw new IllegalArgumentException ( "Latitude is needed" );
		}
		String longitude  = props.getProperty("Longitude");
		if (longitude == null || longitude.isEmpty()) {
			throw new IllegalArgumentException ( "Longitude is needed" );
		}
		String aftEnv  = props.getProperty("AFT_ENVIRONMENT");
		if (aftEnv == null || aftEnv.isEmpty()) {
			throw new IllegalArgumentException ( "AFT_ENVIRONMENT is needed" );
		}
		String version  = props.getProperty("Version");
		if (version == null || version.isEmpty()) {
			throw new IllegalArgumentException ( "Version is needed" );
		}
		String environment  = props.getProperty("Environment");
		if (environment == null || environment.isEmpty()) {
			throw new IllegalArgumentException ( "Environment is needed" );
		}
		String subContextPath  = props.getProperty("SubContextPath");
		if (subContextPath == null || subContextPath.isEmpty()) {
			throw new IllegalArgumentException ( "SubContextPath is needed" );
		}
		String sessionstickinessrequired  = props.getProperty("sessionstickinessrequired");
		if (sessionstickinessrequired == null || sessionstickinessrequired.isEmpty()) {
			throw new IllegalArgumentException ( "sessionstickinessrequired  is needed" );
		}
	}
	
	private  static void validateForNonDME2(Properties props) {
		String transportType = props.getProperty("TransportType");
		String host  = props.getProperty("host");
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException ( "Servicename is needed" );
		}
		String topic  = props.getProperty("topic");
		if (topic == null || topic.isEmpty()) {
			throw new IllegalArgumentException ( "topic is needed" );
		}
		String contenttype  = props.getProperty("contenttype");
		if (contenttype == null || contenttype.isEmpty()) {
			throw new IllegalArgumentException ( "contenttype is needed" );
		}
		if (!ProtocolTypeConstants.HTTPNOAUTH.getValue().equalsIgnoreCase(transportType)){
		String username  = props.getProperty("username");
		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException ( "username is needed" );
		}
		String password  = props.getProperty("password");
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException ( "password is needed" );
		}
		}
		if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(transportType)) {
			String authKey  = props.getProperty("authKey");
			if (authKey == null || authKey.isEmpty()) {
				throw new IllegalArgumentException ( "authKey is needed" );
			}
			String authDate  = props.getProperty("authDate");
			if (authDate == null || authDate.isEmpty()) {
				throw new IllegalArgumentException ( "password is needed" );
			}
			
		}
	}	
	
}
