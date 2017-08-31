#!/bin/bash
#*******************************************************************************
#  ============LICENSE_START=======================================================
#  org.onap.dmaap
#  ================================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  ================================================================================
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#        http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ============LICENSE_END=========================================================
#
#  ECOMP is a trademark and service mark of AT&T Intellectual Property.
#  
#*******************************************************************************

# format://
#	cambriaDel.sh <apiPath> 

if [ $# -gt 2 ]; then
	echo "usage: cambriaDel.sh <apiPath>"
	exit
fi
if [ $# -lt 1 ]; then
	echo "usage: cambriaDel.sh <apiPath>"
	exit
fi

API=$1

# the date needs to be in one of the formats cambria accepts
case "$(uname -s)" in

	Darwin)
		# "EEE MMM dd HH:mm:ss z yyyy"
		DATE=`date`
		;;

	 Linux)
		# "EEE MMM dd HH:mm:ss z yyyy"
		DATE=`date`
	 	;;

	 CYGWIN*|MINGW32*|MSYS*)
		DATE=`date --rfc-2822`
		;;

	*)
		DATE=`date`
		;;
esac


URI="http://$CAMBRIA_SERVER/$API"

if [ -z "$CAMBRIA_APIKEY" ]; then
	echo "no auth"
	curl -i -X GET $AUTHPART $URI
else
	echo "auth in use"
	SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`
	curl -i -X DELETE -H "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" -H "X-CambriaDate: $DATE" $URI
fi

