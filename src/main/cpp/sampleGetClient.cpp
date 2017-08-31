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

int main ( int argc, const char* argv[] )
{
	const CAMBRIA_CLIENT cc = ::cambriaCreateClient ( "localhost", 8080, "topic", CAMBRIA_NATIVE_FORMAT );
	if ( !cc )
	{
		::printf ( "Couldn't create client.\n" );
		return 1;
	}

	int count = 0;
	while ( 1 )
	{
		cambriaGetResponse* response = ::cambriaGetMessages ( cc, 5000, 1024*1024 );
		if ( response && response->statusCode < 300 )
		{
			for ( int i=0; i<response->messageCount; i++ )
			{
				const char* msg = response->messageSet [ i ];
				::printf ( "%d: %s\n", count++,	 msg );
			}
			::cambriaDestroyGetResponse ( cc, response );
		}
		else if ( response )
		{
			::fprintf ( stderr, "%d %s", response->statusCode, response->statusMessage );
		}
		else
		{
			::fprintf ( stderr, "No response object.\n" );
		}
	}

	::cambriaDestroyClient ( cc );

	return 0;
}
