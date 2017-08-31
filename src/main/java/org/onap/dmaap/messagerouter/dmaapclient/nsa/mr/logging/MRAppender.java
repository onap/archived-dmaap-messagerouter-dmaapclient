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
/**
 * 
 */
package org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.logging;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRClientFactory;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRPublisher;

/**
 * @author author
 *
 */
public class MRAppender extends AppenderSkeleton {

	private MRPublisher fPublisher;

	//Provided through log4j configuration
	private String topic;
	private String partition;
	private String hosts;
	private int maxBatchSize = 1;
	private int maxAgeMs = 1000;
	private boolean compress = false;

	/**
	 * 
	 */
	public MRAppender() {
		super();
	}

	/**
	 * @param isActive
	 */
	public MRAppender(boolean isActive) {
		super(isActive);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
		if (!this.closed) {
			this.closed = true;
			fPublisher.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event) {
		final String message;
		
		if (this.layout == null) {
			message = event.getRenderedMessage();
		} else {
			message = this.layout.format(event);
		}
		
		try {
			fPublisher.send(partition, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void activateOptions() {
		if (hosts != null && topic != null && partition != null) {
			fPublisher = MRClientFactory.createBatchingPublisher(hosts.split(","), topic, maxBatchSize, maxAgeMs, compress);
		} else {
			LogLog.error("The Hosts, Topic, and Partition parameter are required to create a MR Log4J Appender");
		}
	}
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public int getMaxAgeMs() {
		return maxAgeMs;
	}

	public void setMaxAgeMs(int maxAgeMs) {
		this.maxAgeMs = maxAgeMs;
	}	
	
	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

}
