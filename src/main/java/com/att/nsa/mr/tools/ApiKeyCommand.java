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

import java.io.IOException;
import java.io.PrintStream;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRIdentityManager;
import com.att.nsa.mr.client.MRClient.MRApiException;
import com.att.nsa.mr.client.MRIdentityManager.ApiKey;

public class ApiKeyCommand implements Command<MRCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"key (create|update) (\\S*) (\\S*)",
			"key (list) (\\S*)",
			"key (revoke)",
		};
	}

	@Override
	public void checkReady ( MRCommandContext context ) throws CommandNotReadyException
	{
		if ( !context.checkClusterReady () )
		{
			throw new CommandNotReadyException ( "Use 'cluster' to specify a cluster to use." );
		}
	}

	@Override
	public void execute ( String[] parts, MRCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		final MRIdentityManager tm = MRClientFactory.createIdentityManager ( context.getCluster(), context.getApiKey(), context.getApiPwd() );
		context.applyTracer ( tm );

		try
		{
			if ( parts[0].equals ( "list" ) )
			{
				final ApiKey key = tm.getApiKey ( parts[1] );
				if ( key != null )
				{
					out.println ( "email: " + key.getEmail () );
					out.println ( "description: " + key.getDescription () );
				}
				else
				{
					out.println ( "No key returned" );
				}
			}
			else if ( parts[0].equals ( "create" ) )
			{
				final ApiCredential ac = tm.createApiKey ( parts[1], parts[2] );
				if ( ac != null )
				{
					out.println ( "   key: " + ac.getApiKey () );
					out.println ( "secret: " + ac.getApiSecret () );
				}
				else
				{
					out.println ( "No credential returned?" );
				}
			}
			else if ( parts[0].equals ( "update" ) )
			{
				tm.updateCurrentApiKey ( parts[1], parts[2] );
				out.println ( "Updated" );
			}
			else if ( parts[0].equals ( "revoke" ) )
			{
				tm.deleteCurrentApiKey ();
				out.println ( "Updated" );
			}
		}
		catch ( HttpObjectNotFoundException e )
		{
			out.println ( "Object not found: " + e.getMessage () );
		}
		catch ( HttpException e )
		{
			out.println ( "HTTP exception: " + e.getMessage () );
		}
		catch ( MRApiException e )
		{
			out.println ( "API exception: " + e.getMessage () );
		}
		catch ( IOException e )
		{
			out.println ( "IO exception: " + e.getMessage () );
		}
		finally
		{
			tm.close ();
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "key create <email> <description>" );
		out.println ( "key update <email> <description>" );
		out.println ( "key list <key>" );
		out.println ( "key revoke" );
	}
}
