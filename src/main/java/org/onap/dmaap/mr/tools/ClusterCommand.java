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
package org.onap.dmaap.mr.tools;

import java.io.PrintStream;

import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import org.onap.dmaap.mr.client.impl.MRConsumerImpl;

public class ClusterCommand implements Command<MRCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"cluster",
			"cluster (\\S*)?",
		};
	}

	@Override
	public void checkReady ( MRCommandContext context ) throws CommandNotReadyException
	{
	}

	@Override
	public void execute ( String[] parts, MRCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		if ( parts.length == 0 )
		{
			for ( String host : context.getCluster () )
			{
				out.println ( host );
			}
		}
		else
		{
			context.clearCluster ();
			for ( String part : parts )
			{
				String[] hosts = part.trim().split ( "\\s+" );
				for ( String host : hosts )
				{
					for ( String splitHost : MRConsumerImpl.stringToList(host) )
					{
						context.addClusterHost ( splitHost );
					}
				}
			}
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "cluster host1 host2 ..." );
	}

}
