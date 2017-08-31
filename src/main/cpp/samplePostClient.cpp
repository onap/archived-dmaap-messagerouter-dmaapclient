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

#include <stdio.h>
#include "cambria.h"

void handleResponse ( const CAMBRIA_CLIENT cc, const cambriaSendResponse* response )
{
	if ( response )
	{
		::printf ( "\t%d %s\n", response->statusCode, ( response->statusMessage ? response->statusMessage : "" ) );
		::printf ( "\t%s\n", response->responseBody ? response->responseBody : "" );

		// destroy the response (or it'll leak)
		::cambriaDestroySendResponse ( cc, response );
	}
	else
	{
		::fprintf ( stderr, "No response object.\n" );
	}
}

int main ( int argc, const char* argv[] )
{
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	// you can send single message in one call...
	::printf ( "Sending single message...\n" );
	int sent = ::cambriaSimpleSend ( "localhost", 8080, "topic", "streamName",
		"{ \"field\":\"this is a JSON formatted alarm\" }" );
	::printf ( "\t%d sent\n\n", sent );

	// you can also send multiple messages in one call with cambriaSimpleSendMultiple.
	// the message argument becomes an array of strings, and you pass an array
	// count too.
	const char* msgs[] =
	{
		"{\"format\":\"json\"}",
		"<format>xml</format>",
		"or whatever. they're just strings."
	};
	sent = ::cambriaSimpleSendMultiple ( "localhost", 8080, "topic", "streamName", msgs, 3 );
	::printf ( "\t%d sent\n\n", sent );

	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	// you can also create a client instance to keep around and make multiple
	// send requests to. Chunked sending isn't supported right now, so each
	// call to cambriaSendMessage results in a full socket open / post / close
	// cycle, but hopefully we can improve this with chunking so that subsequent
	// sends just push the message into the socket.

	// create a client
	const CAMBRIA_CLIENT cc = ::cambriaCreateClient ( "localhost", 8080, "topic", CAMBRIA_NATIVE_FORMAT );
	if ( !cc )
	{
		::printf ( "Couldn't create client.\n" );
		return 1;
	}

	////////////////////////////////////////////////////////////////////////////
	// send a single message
	::printf ( "Sending single message...\n" );
	const cambriaSendResponse* response = ::cambriaSendMessage ( cc, "streamName", "{\"foo\":\"bar\"}" );
	handleResponse ( cc, response );

	////////////////////////////////////////////////////////////////////////////
	// send a few messages at once
	const char* msgs2[] =
	{
		"{\"foo\":\"bar\"}",
		"{\"bar\":\"baz\"}",
		"{\"zoo\":\"zee\"}",
		"{\"foo\":\"bar\"}",
		"{\"foo\":\"bar\"}",
		"{\"foo\":\"bar\"}",
	};
	unsigned int count = sizeof(msgs2)/sizeof(const char*);

	::printf ( "Sending %d messages...\n", count );
	response = ::cambriaSendMessages ( cc, "streamName", msgs2, count );
	handleResponse ( cc, response );

	////////////////////////////////////////////////////////////////////////////
	// destroy the client (or it'll leak)
	::cambriaDestroyClient ( cc );

	return 0;
}
