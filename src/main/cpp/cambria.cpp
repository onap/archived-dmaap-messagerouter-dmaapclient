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

/*
 * This is a client library for the AT&T Cambria Event Routing Service.
 */

#include "cambria.h"

#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>

#include <string>
#include <list>
#include <sstream>
#include <iomanip>
#include <algorithm>

// field used in JSON encoding to signal stream name
const char* kPartition = "cambria.partition";

// map from opaque handle to object pointer
#define toOpaque(x) ((CAMBRIA_CLIENT)x)
#define fromOpaque(x) ((cambriaClient*)x)

// trace support
extern void traceOutput ( const char* format, ... );
#ifdef CAMBRIA_TRACING
	#define TRACE traceOutput
#else
	#define TRACE 1 ? (void) 0 : traceOutput
#endif

/*
 *	internal cambria client class
 */
class cambriaClient
{
public:
	cambriaClient ( const std::string& host, int port, const std::string& topic, const std::string& format );
	~cambriaClient ();

	cambriaSendResponse* send ( const char* streamName, const char* message );
	cambriaSendResponse* send ( const char* streamName, const char** message, unsigned int count );

	cambriaGetResponse* get ( int timeoutMs, int limit );

private:

	std::string fHost;
	int fPort;
	std::string fTopic;
	std::string fFormat;

	bool buildMessageBody ( const char* streamName, const char** msgs, unsigned int count, std::string& buffer );

	void write ( int socket, const char* line );
};

cambriaClient::cambriaClient ( const std::string& host, int port, const std::string& topic, const std::string& format ) :
	fHost ( host ),
	fPort ( port ),
	fTopic ( topic ),
	fFormat ( format )
{
}

cambriaClient::~cambriaClient ()
{
}

/*
 *	This isn't quite right -- if the message already has cambria.partition,
 *	it'll wind up with two entries. Also, message MUST start with '{' and
 *	have at least one field.
 */
static char* makeJsonMessage ( const char* streamName, const char* message )
{
	int len = ::strlen ( message );
	if ( streamName )
	{
		len += ::strlen ( kPartition );
		len += ::strlen ( streamName );
		len += 6; 	// quote each and a colon and comma
	}

	char* msg = new char [ len + 1 ];
	::strcpy ( msg, "{" );
	if ( streamName )
	{
		::strcat ( msg, "\"" );
		::strcat ( msg, kPartition );
		::strcat ( msg, "\":\"" );
		::strcat ( msg, streamName );
		::strcat ( msg, "\"," );
	}
	::strcat ( msg, message + 1 );
	return msg;
}

cambriaSendResponse* cambriaClient::send ( const char* streamName, const char* message )
{
	return send ( streamName, &message, 1 );
}

static bool replace ( std::string& str, const std::string& from, const std::string& to )
{
	size_t start_pos = str.find ( from );
	if(start_pos == std::string::npos)
		return false;
	str.replace(start_pos, from.length(), to);
	return true;
}

static void readResponse ( int s, std::string& response )
{
	char buffer [ 4096 ];

	ssize_t n = 0;
	while ( ( n = ::read ( s, buffer, 4095 ) ) > 0 )
	{
		buffer[n] = '\0';
		response += buffer;
	}
}

static int openSocket ( std::string& host, int port, int& ss )
{
	TRACE( "connecting to %s\n", host.c_str() );

	struct hostent *he = ::gethostbyname ( host.c_str() );
	if ( !he )
	{
		TRACE("no host entry\n");
		return CAMBRIA_NO_HOST;
	}

	if ( he->h_addrtype != AF_INET )
	{
		TRACE("not AF_INET\n");
		return CAMBRIA_NO_HOST;
	}

	int s = ::socket ( AF_INET, SOCK_STREAM, 0 );
	if ( s == -1 )
	{
		TRACE("no socket available\n");
		return CAMBRIA_CANT_CONNECT;
	}

	struct sockaddr_in servaddr;
	::memset ( &servaddr, 0, sizeof(servaddr) );

	::memcpy ( &servaddr.sin_addr, he->h_addr_list[0], he->h_length );
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = ::htons ( port );

	if ( ::connect ( s, (struct sockaddr *)&servaddr, sizeof(servaddr) ) )
	{
		TRACE("couldn't connect\n");
		return CAMBRIA_CANT_CONNECT;
	}

	ss = s;
	return 0;
}

cambriaSendResponse* cambriaClient::send ( const char* streamName, const char** msgs, unsigned int count )
{
	TRACE ( "Sending %d messages.", count );

	cambriaSendResponse* result = new cambriaSendResponse ();
	result->statusCode = 0;
	result->statusMessage = NULL;
	result->responseBody = NULL;

	TRACE( "building message body\n" );

	std::string body;
	if ( !buildMessageBody ( streamName, msgs, count, body ) )
	{
		result->statusCode = CAMBRIA_UNRECOGNIZED_FORMAT;
		return result;
	}

	int s = -1;
	int err = ::openSocket ( fHost, fPort, s );
	if ( err > 0 )
	{
		result->statusCode = err;
		return result;
	}

	// construct path
	std::string path = "/cambriaApiServer/v1/event/";
	path += fTopic;

	// send post prefix
	char line[4096];
	::sprintf ( line,
		"POST %s HTTP/1.0\r\n"
		"Host: %s\r\n"
		"Content-Type: %s\r\n"
		"Content-Length: %d\r\n"
		"\r\n",
		path.c_str(), fHost.c_str(), fFormat.c_str(), body.length() );
	write ( s, line );

	// send the body
	write ( s, body.c_str() );

	TRACE ( "\n" );
	TRACE ( "send complete, reading reply\n" );

	// receive the response
	std::string response;
	readResponse ( s, response );
	::close ( s );

	// parse the header and body: split header and body on first occurrence of \r\n\r\n
	result->statusCode = CAMBRIA_BAD_RESPONSE;

	size_t headerBreak = response.find ( "\r\n\r\n" );
    if ( headerBreak != std::string::npos )
    {
		std::string responseBody = response.substr ( headerBreak + 4 );
		result->responseBody = new char [ responseBody.length() + 1 ];
		::strcpy ( result->responseBody, responseBody.c_str() );

		// all we need from the header for now is the status line
		std::string headerPart = response.substr ( 0, headerBreak + 2 );
		
		size_t newline = headerPart.find ( '\r' );
		if ( newline != std::string::npos )
		{
			std::string statusLine = headerPart.substr ( 0, newline );

			size_t firstSpace = statusLine.find ( ' ' );
			if ( firstSpace != std::string::npos )
			{
				size_t secondSpace = statusLine.find ( ' ', firstSpace + 1 );
				if ( secondSpace != std::string::npos )
				{
					result->statusCode = ::atoi ( statusLine.substr ( firstSpace + 1, secondSpace - firstSpace + 1 ).c_str() );
					std::string statusMessage = statusLine.substr ( secondSpace + 1 );
					result->statusMessage = new char [ statusMessage.length() + 1 ];
					::strcpy ( result->statusMessage, statusMessage.c_str() );
				}
			}
		}
	}
	return result;
}

void cambriaClient::write ( int socket, const char* str )
{
	int len = str ? ::strlen ( str ) : 0;
	::write ( socket, str, len );

	// elaborate tracing nonsense...
	std::string trace ( "> " );
	trace += str;
	while ( replace ( trace, "\r\n", "\\r\\n\n> " ) );

	TRACE ( "%s", trace.c_str() );
}

bool cambriaClient::buildMessageBody ( const char* streamName, const char** msgs, unsigned int count, std::string& buffer )
{
	if ( fFormat == CAMBRIA_NATIVE_FORMAT )
	{
		int snLen = ::strlen ( streamName );
		for ( unsigned int i=0; i<count; i++ )
		{
			const char* msg = msgs[i];

			std::ostringstream s;
			s << snLen << '.' << ::strlen(msg) << '.' << streamName << msg;
			buffer.append ( s.str() );
		}
	}
	else if ( fFormat == CAMBRIA_JSON_FORMAT )
	{
		buffer.append ( "[" );
		for ( unsigned int i=0; i<count; i++ )
		{
			if ( i>0 )
			{
				buffer.append ( "," );
			}
			const char* msg = msgs[i];
			char* jsonMsg = ::makeJsonMessage ( streamName, msg );
			buffer.append ( jsonMsg );
			delete jsonMsg;	// FIXME: allocating memory here just to delete it
		}
		buffer.append ( "]" );
	}
	else
	{
		return false;
	}
	return true;
}

// read the next string into value, and return the end pos, or 0 on error
static int readNextJsonString ( const std::string& body, int startPos, std::string& value )
{
	value = "";

	if ( startPos >= body.length () )
	{
		return 0;
	}

	// skip a comma
	int current = startPos;
	if ( body[current] == ',' ) current++;

	if ( current >= body.length() || body[current] != '"' )
	{
		return 0;
	}
	current++;

	// walk the string for the closing quote (FIXME: unicode support)
	bool esc = false;
	int hex = 0;
	while ( ( body[current] != '"' || esc ) && current < body.length() )
	{
		if ( hex > 0 )
		{
			hex--;
			if ( hex == 0 )
			{
				// presumably read a unicode escape. this code isn't
				// equipped for multibyte or unicode, so just skip it
				value += '?';
			}
		}
		else if ( esc )
		{
			esc = false;
			switch ( body[current] )
			{
				case '"':
				case '\\':
				case '/':
					value += body[current];
					break;

				case 'b': value += '\b'; break;
				case 'f': value += '\f'; break;
				case 'n': value += '\n'; break;
				case 'r': value += '\r'; break;
				case 't': value += '\t'; break;

				case 'u': hex=4; break;
			}
		}
		else
		{
			esc = body[current] == '\\';
			if ( !esc ) value += body[current];
		}
		current++;
	}

	return current + 1;
}

static void readGetBody ( std::string& body, cambriaGetResponse& response )
{
	TRACE("response %s\n", body.c_str() );	
	
	if ( body.length() < 2 || body[0] != '[' || body[body.length()-1] != ']' )
	{
		response.statusCode = CAMBRIA_BAD_RESPONSE;
	}

	std::list<char*> msgs;
	std::string val;
	int current = 1;
	while ( ( current = readNextJsonString ( body, current, val ) ) > 0 )
	{
		char* msg = new char [ val.length() + 1 ];
		::strcpy ( msg, val.c_str() );
		msgs.push_back ( msg );
	}
	
	// now build a response
	response.messageCount = msgs.size();
	response.messageSet = new char* [ msgs.size() ];
	int index = 0;
	for ( std::list<char*>::iterator it = msgs.begin(); it != msgs.end(); it++ )
	{
		response.messageSet [ index++ ] = *it;
	}
}

cambriaGetResponse* cambriaClient::get ( int timeoutMs, int limit )
{
	cambriaGetResponse* result = new cambriaGetResponse ();
	result->statusCode = 0;
	result->statusMessage = NULL;
	result->messageCount = 0;
	result->messageSet = new char* [ 1 ];

	int s = -1;
	int err = ::openSocket ( fHost, fPort, s );
	if ( err > 0 )
	{
		result->statusCode = err;
		return result;
	}
	
	// construct path
	std::string path = "/cambriaApiServer/v1/event/";
	path += fTopic;

	bool haveAdds = false;
	std::ostringstream adds;
	if ( timeoutMs > -1 )
	{
		adds << "timeout=" << timeoutMs;
		haveAdds = true;
	}
	if ( limit > -1 )
	{
		if ( haveAdds )
		{
			adds << "&";
		}
		adds << "limit=" << limit;
		haveAdds = true;
	}
	if ( haveAdds )
	{
		path += "?";
		path += adds.str();
	}

	// send post prefix
	char line[4096];
	::sprintf ( line,
		"GET %s HTTP/1.0\r\n"
		"Host: %s\r\n"
		"\r\n",
		path.c_str(), fHost.c_str() );
	write ( s, line );

	TRACE ( "\n" );
	TRACE ( "request sent; reading reply\n" );

	// receive the response (FIXME: would be nice to stream rather than load it all)
	std::string response;
	readResponse ( s, response );
	::close ( s );

	// parse the header and body: split header and body on first occurrence of \r\n\r\n
	result->statusCode = CAMBRIA_BAD_RESPONSE;

	size_t headerBreak = response.find ( "\r\n\r\n" );
    if ( headerBreak != std::string::npos )
    {
		// get the header line
		std::string headerPart = response.substr ( 0, headerBreak + 2 );

		size_t newline = headerPart.find ( '\r' );
		if ( newline != std::string::npos )
		{
			std::string statusLine = headerPart.substr ( 0, newline );

			size_t firstSpace = statusLine.find ( ' ' );
			if ( firstSpace != std::string::npos )
			{
				size_t secondSpace = statusLine.find ( ' ', firstSpace + 1 );
				if ( secondSpace != std::string::npos )
				{
					result->statusCode = ::atoi ( statusLine.substr ( firstSpace + 1, secondSpace - firstSpace + 1 ).c_str() );
					std::string statusMessage = statusLine.substr ( secondSpace + 1 );
					result->statusMessage = new char [ statusMessage.length() + 1 ];
					::strcpy ( result->statusMessage, statusMessage.c_str() );
				}
			}
		}

		if ( result->statusCode < 300 )
		{
			std::string responseBody = response.substr ( headerBreak + 4 );
			readGetBody ( responseBody, *result );
		}
	}
	return result;
}


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

CAMBRIA_CLIENT cambriaCreateClient ( const char* host, int port, const char* topic, const char* format )
{
	cambriaClient* cc = new cambriaClient ( host, port, topic, format );
	return toOpaque(cc);
}

void cambriaDestroyClient ( CAMBRIA_CLIENT client )
{
	delete fromOpaque ( client );
}

cambriaSendResponse* cambriaSendMessage ( CAMBRIA_CLIENT client, const char* streamName, const char* message )
{
	cambriaClient* c = fromOpaque ( client );
	return c->send ( streamName, message );
}

cambriaSendResponse* cambriaSendMessages ( CAMBRIA_CLIENT client, const char* streamName, const char** messages, unsigned int count )
{
	cambriaClient* c = fromOpaque ( client );
	return c->send ( streamName, messages, count );
}

cambriaGetResponse* cambriaGetMessages ( CAMBRIA_CLIENT client, unsigned long timeoutMs, unsigned int limit )
{
	cambriaClient* c = fromOpaque ( client );
	return c->get ( timeoutMs, limit );
}

void cambriaDestroySendResponse ( CAMBRIA_CLIENT client, const cambriaSendResponse* response )
{
	if ( response )
	{
		delete response->statusMessage;
		delete response->responseBody;
		delete response;
	}
}

void cambriaDestroyGetResponse ( CAMBRIA_CLIENT client, const cambriaGetResponse* response )
{
	if ( response )
	{
		delete response->statusMessage;
		for ( int i=0; i<response->messageCount; i++ )
		{
			delete response->messageSet[i];
		}
		delete response;
	}
}

int cambriaSimpleSend ( const char* host, int port, const char* topic, const char* streamName, const char* msg )
{
	return cambriaSimpleSendMultiple ( host, port, topic, streamName, &msg, 1 );
}

int cambriaSimpleSendMultiple ( const char* host, int port, const char* topic, const char* streamName, const char** messages, unsigned int msgCount )
{
	int count = 0;

	const CAMBRIA_CLIENT cc = ::cambriaCreateClient ( host, port, topic, CAMBRIA_NATIVE_FORMAT );
	if ( cc )
	{
		const cambriaSendResponse* response = ::cambriaSendMessages ( cc, streamName, messages, msgCount );
		if ( response && response->statusCode < 300 )
		{
			count = msgCount;
		}
		::cambriaDestroySendResponse ( cc, response );
		::cambriaDestroyClient ( cc );
	}

	return count;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

const unsigned int kMaxTraceBuffer = 2048;

static void writeTraceString ( const char* msg )
{
	::fprintf ( stdout, "%s", msg );
	::fflush ( stdout );	// because we want output before core dumping :-)
}

void traceOutput ( const char* format, ... )
{
	char buffer [ kMaxTraceBuffer ];
	::memset ( buffer, '\0', kMaxTraceBuffer * sizeof ( char ) );

	va_list list;
	va_start ( list, format );
	::vsprintf ( buffer, format, list );
	writeTraceString ( buffer );
	va_end ( list );
}
