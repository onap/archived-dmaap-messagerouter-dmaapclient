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
package org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client;

import java.io.IOException;
import java.util.Set;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;


/**
 * A client for working with topic metadata.
 * @author author
 */
public interface MRTopicManager extends MRClient
{
	/**
	 * Get the topics available in the cluster
	 * @return a set of topic names
	 * @throws IOException 
	 */
	Set<String> getTopics () throws IOException;

	/**
	 * Information about a topic.
	 */
	public interface TopicInfo
	{
		/**
		 * Get the owner of the topic
		 * @return the owner, or null if no entry
		 */
		String getOwner ();

		/**
		 * Get the description for this topic
		 * @return the description, or null if no entry
		 */
		String getDescription ();

		/**
		 * Get the set of allowed producers (as API keys) on this topic
		 * @return the set of allowed producers, null of no ACL exists/enabled
		 */
		Set<String> getAllowedProducers ();

		/**
		 * Get the set of allowed consumers (as API keys) on this topic
		 * @return the set of allowed consumers, null of no ACL exists/enabled
		 */
		Set<String> getAllowedConsumers ();
	}

	/**
	 * Get information about a topic.
	 * @param topic
	 * @return topic information
	 * @throws IOException 
	 * @throws HttpObjectNotFoundException 
	 */
	TopicInfo getTopicMetadata ( String topic ) throws HttpObjectNotFoundException, IOException;

	/**
	 * Create a new topic.
	 * @param topicName
	 * @param topicDescription
	 * @param partitionCount
	 * @param replicationCount
	 * @throws HttpException 
	 * @throws IOException 
	 */
	void createTopic ( String topicName, String topicDescription, int partitionCount, int replicationCount ) throws HttpException, IOException;

	/**
	 * Delete the topic. This call must be authenticated and the API key listed as owner on the topic.
	 * NOTE: The MR (UEB) API server does not support topic deletion at this time (mid 2015)
	 * @param topic
	 * @throws HttpException
	 * @throws IOException 
	 * @deprecated If/when the Kafka system supports topic delete, or the implementation changes, this will be restored.
	 */
	@Deprecated
	void deleteTopic ( String topic ) throws HttpException, IOException;

	/**
	 * Can any client produce events into this topic without authentication?
	 * @param topic
	 * @return true if the topic is open for producing
	 * @throws IOException 
	 * @throws HttpObjectNotFoundException 
	 */
	boolean isOpenForProducing ( String topic ) throws HttpObjectNotFoundException, IOException;

	/**
	 * Get the set of allowed producers. If the topic is open, the result is null.
	 * @param topic
	 * @return a set of allowed producers or null
	 * @throws IOException 
	 * @throws HttpObjectNotFoundException 
	 */
	Set<String> getAllowedProducers ( String topic ) throws HttpObjectNotFoundException, IOException;

	/**
	 * Allow the given API key to produce messages on the given topic. The caller must
	 * own this topic.
	 * @param topic
	 * @param apiKey
	 * @throws HttpException 
	 * @throws HttpObjectNotFoundException 
	 * @throws IOException 
	 */
	void allowProducer ( String topic, String apiKey ) throws HttpObjectNotFoundException, HttpException, IOException;

	/**
	 * Revoke the given API key's authorization to produce messages on the given topic.
	 * The caller must own this topic.
	 * @param topic
	 * @param apiKey
	 * @throws HttpException 
	 * @throws IOException 
	 */
	void revokeProducer ( String topic, String apiKey ) throws HttpException, IOException;
	
	/**
	 * Can any client consume events from this topic without authentication?
	 * @param topic
	 * @return true if the topic is open for consuming
	 * @throws IOException 
	 * @throws HttpObjectNotFoundException 
	 */
	boolean isOpenForConsuming ( String topic ) throws HttpObjectNotFoundException, IOException;

	/**
	 * Get the set of allowed consumers. If the topic is open, the result is null.
	 * @param topic
	 * @return a set of allowed consumers or null
	 * @throws IOException 
	 * @throws HttpObjectNotFoundException 
	 */
	Set<String> getAllowedConsumers ( String topic ) throws HttpObjectNotFoundException, IOException;
	
	/**
	 * Allow the given API key to consume messages on the given topic. The caller must
	 * own this topic.
	 * @param topic
	 * @param apiKey
	 * @throws HttpException 
	 * @throws HttpObjectNotFoundException 
	 * @throws IOException 
	 */
	void allowConsumer ( String topic, String apiKey ) throws HttpObjectNotFoundException, HttpException, IOException;

	/**
	 * Revoke the given API key's authorization to consume messages on the given topic.
	 * The caller must own this topic.
	 * @param topic
	 * @param apiKey
	 * @throws HttpException 
	 * @throws IOException 
	 */
	void revokeConsumer ( String topic, String apiKey ) throws HttpException, IOException;
}

