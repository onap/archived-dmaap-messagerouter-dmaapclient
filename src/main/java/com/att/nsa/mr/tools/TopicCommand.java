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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRTopicManager;
import com.att.nsa.mr.client.MRTopicManager.TopicInfo;

public class TopicCommand implements Command<MRCommandContext>
{
        final Logger logger = LoggerFactory.getLogger(ApiKeyCommand.class);
	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"topic (list)",
			"topic (list) (\\S*)",
			"topic (create) (\\S*) (\\S*) (\\S*)",
			"topic (grant|revoke) (read|write) (\\S*) (\\S*)",
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
		final MRTopicManager tm = MRClientFactory.createTopicManager ( context.getCluster(), context.getApiKey(), context.getApiPwd() );
		context.applyTracer ( tm );

		try
		{
			if ( parts[0].equals ( "list" ) )
			{
				try
				{
					if ( parts.length == 1 )
					{
						for ( String topic : tm.getTopics () )
						{
							out.println ( topic );
						}
					}
					else
					{
						final TopicInfo ti = tm.getTopicMetadata ( parts[1] );

						final String owner = ti.getOwner ();
						out.println ( "      owner: " + ( owner == null ? "<none>" : owner ) );

						final String desc = ti.getDescription ();
						out.println ( "description: " + ( desc == null ? "<none>" : desc ) );

						final Set<String> prods = ti.getAllowedProducers ();
						if ( prods != null )
						{
							out.println ( "  write ACL: " );
							for ( String key : prods )
							{
								out.println ( "\t" + key );
							}
						}
						else
						{
							out.println ( "  write ACL: <not active>" );
						}

						final Set<String> cons = ti.getAllowedConsumers ();
						if ( cons != null )
						{
							out.println ( "   read ACL: " );
							for ( String key : cons )
							{
								out.println ( "\t" + key );
							}
						}
						else
						{
							out.println ( "   read ACL: <not active>" );
						}
					}
				}
				catch ( IOException x )
				{
					out.println ( "Problem with request: " + x.getMessage () );
                                    logger.error("IOException: ", e);
				}
				catch ( HttpObjectNotFoundException e )
				{
					out.println ( "Not found: " + e.getMessage () );
                                    logger.error("HttpObjectNotFoundException: ", e);
				}
			}
			else if ( parts[0].equals ( "create" ) )
			{
				try
				{
					final int partitions = Integer.parseInt ( parts[2] );
					final int replicas = Integer.parseInt ( parts[3] );
					
					tm.createTopic ( parts[1], "", partitions, replicas );
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("HttpException: ", e);
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("IOException: ", e);
				}
				catch ( NumberFormatException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("NumberFormatException: ", e);
				}
			}
			else if ( parts[0].equals ( "grant" ) )
			{
				try
				{
					if ( parts[1].equals ( "write" ) ) 
					{
						tm.allowProducer ( parts[2], parts[3] );
					}
					else if ( parts[1].equals ( "read" ) )
					{
						tm.allowConsumer ( parts[2], parts[3] );
					}
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("HttpException: ", e);
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("IOException: ", e);
				}
			}
			else if ( parts[0].equals ( "revoke" ) )
			{
				try
				{
					if ( parts[1].equals ( "write" ) ) 
					{
						tm.revokeProducer ( parts[2], parts[3] );
					}
					else if ( parts[1].equals ( "read" ) )
					{
						tm.revokeConsumer ( parts[2], parts[3] );
					}
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("HttpException: ", e);
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
                                    logger.error("IOException: ", e);
				}
			}
		}
		finally
		{
			tm.close ();
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "topic list" );
		out.println ( "topic list <topicName>" );
		out.println ( "topic create <topicName> <partitions> <replicas>" );
		out.println ( "topic grant write|read <topicName> <apiKey>" );
		out.println ( "topic revoke write|read <topicName> <apiKey>" );
	}

}
