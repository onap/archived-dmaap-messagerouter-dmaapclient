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
package org.onap.dmaap.mr.client;

import java.io.IOException;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;

/**
 * A client for manipulating API keys.
 * @author author
 *
 */
public interface MRIdentityManager extends MRClient
{
	/**
	 * An API Key record
	 */
	public interface ApiKey
	{
		/**
		 * Get the email address associated with the API key
		 * @return the email address on the API key or null
		 */
		String getEmail ();

		/**
		 * Get the description associated with the API key
		 * @return the description on the API key or null
		 */
		String getDescription ();
	}

	/**
	 * Create a new API key on the UEB cluster. The returned credential instance
	 * contains the new API key and API secret. This is the only time the secret
	 * is available to the client -- there's no API for retrieving it later -- so
	 * your application must store it securely. 
	 * 
	 * @param email
	 * @param description
	 * @return a new credential
	 * @throws HttpException 
	 * @throws MRApiException 
	 * @throws IOException 
	 */
	ApiCredential createApiKey ( String email, String description ) throws HttpException, MRApiException, IOException;
	
	/**
	 * Get basic info about a known API key
	 * @param apiKey
	 * @return the API key's info or null if it doesn't exist
	 * @throws HttpObjectNotFoundException, HttpException, MRApiException 
	 * @throws IOException 
	 */
	ApiKey getApiKey ( String apiKey ) throws HttpObjectNotFoundException, HttpException, MRApiException, IOException;

	/**
	 * Update the record for the API key used to authenticate this request. The UEB
	 * API requires that you authenticate with the same key you're updating, so the
	 * API key being changed is the one used for setApiCredentials.
	 * 
	 * @param email use null to keep the current value
	 * @param description use null to keep the current value
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws HttpObjectNotFoundException 
	 */
	void updateCurrentApiKey ( String email, String description ) throws HttpObjectNotFoundException, HttpException, IOException;

	/**
	 * Delete the *current* API key. After this call returns, the API key
	 * used to authenticate will no longer be valid.
	 * 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	void deleteCurrentApiKey () throws HttpException, IOException;
}
