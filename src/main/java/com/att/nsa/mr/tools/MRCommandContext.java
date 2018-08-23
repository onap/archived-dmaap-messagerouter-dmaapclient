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

import java.util.Collection;
import java.util.LinkedList;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cmdtool.CommandContext;
import com.att.nsa.mr.client.MRClient;

public class MRCommandContext implements CommandContext
{
	public MRCommandContext ()
	{
		fApiKey = null;
		fApiPwd = null;

		fCluster = new LinkedList<String> ();
		fCluster.add ( "localhost" );
	}

	@Override
	public void requestShutdown ()
	{
		fShutdown = true;
	}

	@Override
	public boolean shouldContinue ()
	{
		return !fShutdown;
	}

	public void setAuth ( String key, String pwd ) { fApiKey = key; fApiPwd = pwd; }
	public void clearAuth () { setAuth(null,null); }
	
	public boolean checkClusterReady ()
	{
		return ( fCluster.isEmpty());
	}

	public Collection<String> getCluster ()
	{
		return new LinkedList<String> ( fCluster );
	}

	public void clearCluster ()
	{
		fCluster.clear ();
	}

	public void addClusterHost ( String host )
	{
		fCluster.add ( host );
	}

	public String getApiKey () { return fApiKey; }
	public String getApiPwd () { return fApiPwd; }

	public void useTracer ( HttpTracer t )
	{
		fTracer = t;
	}
	public void noTracer () { fTracer = null; }

	public void applyTracer ( MRClient cc )
	{
		if ( cc instanceof HttpClient && fTracer != null )
		{
			((HttpClient)cc).installTracer ( fTracer );
		}
	}

	private boolean fShutdown;
	private String fApiKey;
	private String fApiPwd;
	private final LinkedList<String> fCluster;
	private HttpTracer fTracer = null;
}
