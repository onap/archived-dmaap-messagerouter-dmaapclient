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

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import org.onap.dmaap.mr.client.MRBatchingPublisher;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRConsumer;
import org.onap.dmaap.mr.client.MRPublisher.message;

public class MessageCommand implements Command<MRCommandContext>
{
        final Logger logger = LoggerFactory.getLogger(ApiKeyCommand.class);
	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"(post) (\\S*) (\\S*) (.*)",
			"(read) (\\S*) (\\S*) (\\S*)",
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
		if ( parts[0].equalsIgnoreCase ( "read" ))
		{
			final MRConsumer cc = MRClientFactory.createConsumer ( context.getCluster (), parts[1], parts[2], parts[3],
				-1, -1, null, context.getApiKey(), context.getApiPwd() );
			context.applyTracer ( cc );
			try
			{
				for ( String msg : cc.fetch () )
				{
					out.println ( msg );
				}
			}
			catch ( Exception e )
			{
				out.println ( "Problem fetching messages: " + e.getMessage() );
                            logger.error("Problem fetching messages: ", e);
			}
			finally
			{
				cc.close ();
			}
		}
		else
		{
			final MRBatchingPublisher pub=ToolsUtil.createBatchPublisher(context, parts[1]);
			try
			{
				pub.send ( parts[2], parts[3] );
			}
			catch ( IOException e )
			{
				out.println ( "Problem sending message: " + e.getMessage() );
                            logger.error("Problem sending message: ", e);
			}
			finally
			{
				List<message> left = null;
				try
				{
					left = pub.close ( 500, TimeUnit.MILLISECONDS );
				}
				catch ( IOException e )
				{
					out.println ( "Problem sending message: " + e.getMessage() );
                                    logger.error("Problem sending message: ", e);
				}
				catch ( InterruptedException e )
				{
					out.println ( "Problem sending message: " + e.getMessage() );
                                    logger.error("Problem sending message: ", e);
                                    Thread.currentThread().interrupt();
				}
				if ( left != null && left.isEmpty() )
				{
					out.println ( left.size() + " messages not sent." );
				}
			}
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "post <topicName> <partition> <message>" );
		out.println ( "read <topicName> <consumerGroup> <consumerId>" );
	}

}
