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
package com.att.nsa.mr.test.support;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.response.MRPublisherResponse;

/**
 * A helper for unit testing systems that use a MRPublisher. When setting
 * up your test, inject an instance into MRClientFactory to have it return
 * the mock client.
 * 
 * @author author
 *
 */
public class MRBatchingPublisherMock implements MRBatchingPublisher
{
	public class Entry
	{
		public Entry ( String partition, String msg )
		{
			fPartition = partition;
			fMessage = msg;
		}

		@Override
		public String toString ()
		{
			return fMessage;
		}
		
		public final String fPartition;
		public final String fMessage;
	}

	public MRBatchingPublisherMock ()
	{
		fCaptures = new LinkedList<> ();
	}

	public interface Listener
	{
		void onMessage ( Entry e );
	}
	public void addListener ( Listener listener )
	{
		fListeners.add ( listener );
	}
	
	public List<Entry> getCaptures ()
	{
		return getCaptures ( new MessageFilter () { @Override public boolean match ( String msg ) { return true; } } );
	}

	public interface MessageFilter
	{
		boolean match ( String msg );
	}

	public List<Entry> getCaptures ( MessageFilter filter )
	{
		final LinkedList<Entry> result = new LinkedList<> ();
		for ( Entry capture : fCaptures )
		{
			if ( filter.match ( capture.fMessage ) )
			{
				result.add ( capture );
			}
		}
		return result;
	}

	public int received ()
	{
		return fCaptures.size();
	}

	public void reset ()
	{
		fCaptures.clear ();
	}

	@Override
	public int send ( String partition, String msg )
	{
		final Entry e = new Entry ( partition, msg ); 

		fCaptures.add ( e );
		for ( Listener l : fListeners )
		{
			l.onMessage ( e );
		}
		return 1;
	}

	@Override
	public int send ( message msg )
	{
		return send ( msg.fPartition, msg.fMsg );
	}
	@Override
	public int send ( String msg )
	{
		return 1;
		
	}

	@Override
	public int send ( Collection<message> msgs )
	{
		int sum = 0;
		for ( message m : msgs )
		{
			sum += send ( m );
		}
		return sum;
	}

	@Override
	public int getPendingMessageCount ()
	{
		return 0;
	}

	@Override
	public List<message> close ( long timeout, TimeUnit timeoutUnits )
	{
		return new LinkedList<> ();
	}

	@Override
	public void close ()
	{
	}

	@Override
	public void setApiCredentials ( String apiKey, String apiSecret )
	{
	}

	@Override
	public void clearApiCredentials ()
	{
	}

	@Override
	public void logTo ( Logger log )
	{
	}

	private final LinkedList<Entry> fCaptures;
	private LinkedList<Listener> fListeners = new LinkedList<> ();
	@Override
	public MRPublisherResponse sendBatchWithResponse() {
		// TODO Auto-generated method stub
		return null;
	}
}
