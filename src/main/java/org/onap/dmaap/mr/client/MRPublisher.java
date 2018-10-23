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
import java.util.Collection;

/**
 * A MR publishing interface.
 *
 */
public interface MRPublisher extends MRClient
{
	/**
	 * A simple message container 
	 */
	public static class message
	{
		public message ( String partition, String msg )
		{
			fPartition = partition == null ? "" : partition;
			fMsg = msg;
			if ( fMsg == null )
			{
				throw new IllegalArgumentException ( "Can't send a null message." );
			}
		}

		public message ( message msg )
		{
			this ( msg.fPartition, msg.fMsg );
		}

		public final String fPartition;
		public final String fMsg;
	}
	
	/**
	 * Send the given message without partition. partition will be placed at HTTP request level.
	 * @param msg message to sent
	 * @return the number of pending messages
	 * @throws IOException exception
	 */
	int send ( String msg ) throws IOException;
	/**
	 * Send the given message using the given partition.
	 * @param partition partition
	 * @param msg message
	 * @return the number of pending messages
	 * @throws IOException exception
	 */
	int send ( String partition, String msg ) throws IOException;

	/**
	 * Send the given message using its partition.
	 * @param msg mesg
	 * @return the number of pending messages
	 * @throws IOException exp
	 */
	int send ( message msg ) throws IOException;

	/**
	 * Send the given messages using their partitions.
	 * @param msgs msg
	 * @return the number of pending messages
	 * @throws IOException exp
	 */
	int send ( Collection<message> msgs ) throws IOException;

	/**
	 * Close this publisher. It's an error to call send() after close()
	 */
	void close ();
}
