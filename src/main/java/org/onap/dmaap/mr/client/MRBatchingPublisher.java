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
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.onap.dmaap.mr.client.response.MRPublisherResponse;

/**
 * A MR batching publisher is a publisher with additional functionality
 * for managing delayed sends.
 * 
 * @author author
 *
 */
public interface MRBatchingPublisher extends MRPublisher
{
	/**
	 * Get the number of messages that have not yet been sent.
	 * @return the number of pending messages
	 */
	int getPendingMessageCount ();

	/**
	 * Close this publisher, sending any remaining messages.
	 * @param timeout an amount of time to wait for unsent messages to be sent
	 * @param timeoutUnits the time unit for the timeout arg
	 * @return a list of any unsent messages after the timeout
	 * @throws IOException exception
	 * @throws InterruptedException exception
	 */
	List<message> close ( long timeout, TimeUnit timeoutUnits ) throws IOException, InterruptedException;
	
	MRPublisherResponse sendBatchWithResponse ();
}
