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
#include <ctime>
#include <string.h>
#include "cambria.h"

const char* kAlarm =
	"<EVENT>"
        "<AGENT_ADDR>12.123.70.213</AGENT_ADDR>"
        "<AGENT_RESOLVED>ptdor306me1.els-an.att.net</AGENT_RESOLVED>"
        "<TIME_RECEIVED>1364716208</TIME_RECEIVED>"
        "  <PROTOCOL_VERSION>V1</PROTOCOL_VERSION>"
        "  <ENTERPRISE_LEN>9</ENTERPRISE_LEN>"
        "  <ENTERPRISE>.1.3.6.1.4.1.9.9.187</ENTERPRISE>"
        "  <GENERIC>6</GENERIC>"
        "  <SPECIFIC>2</SPECIFIC>"
        "  <COMMAND>167</COMMAND>"
        "  <REQUEST_ID>0</REQUEST_ID>"
        "  <ERROR_STATUS>0</ERROR_STATUS>"
        "  <ERROR_INDEX>0</ERROR_INDEX>"
        "  <AGENT_TIME_UP>1554393204</AGENT_TIME_UP>"
        "  <COMMUNITY_LEN>10</COMMUNITY_LEN>"
        "  <COMMUNITY>nidVeskaf0</COMMUNITY>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.2.1.15.3.1.14.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>OCTET_STRING_HEX</VARBIND_TYPE>"
        "      <VARBIND_VALUE>02 02 </VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.2.1.15.3.1.2.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>INTEGER</VARBIND_TYPE>"
        "      <VARBIND_VALUE>1</VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.4.1.9.9.187.1.2.1.1.7.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>OCTET_STRING_ASCII</VARBIND_TYPE>"
        "      <VARBIND_VALUE>peer in wrong AS</VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.4.1.9.9.187.1.2.1.1.8.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>INTEGER</VARBIND_TYPE>"
        "      <VARBIND_VALUE>4</VARBIND_VALUE>"
        "    </VARBIND>"
      "</EVENT>";

int main ( int argc, const char* argv[] )
{
	char** msgs = new char* [ 100 ];
	for ( int i=0; i<100; i++ )
	{
		msgs[i] = new char [ ::strlen ( kAlarm + 1 ) ];
		::strcpy ( msgs[i], kAlarm );
	}

	std::time_t start = std::time ( NULL );
	for ( int i=0; i<5000; i++ )
	{
		::cambriaSimpleSendMultiple ( "localhost", 8080, "topic", "streamName", (const char**)msgs, 100 );
		if ( i % 50 == 0 )
		{
			std::time_t end = std::time ( NULL );
			double seconds = difftime ( end, start );
			::printf ( "%.f seconds for %u posts.\n", seconds, i*100 );
		}
	}
	std::time_t end = std::time ( NULL );
	double seconds = difftime ( end, start );
	::printf ( "%.f seconds for 1,000,000 posts.\n", seconds );

	return 0;
}
