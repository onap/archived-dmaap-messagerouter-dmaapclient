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
package org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRClientFactory;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.MRConsumer;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.client.response.MRConsumerResponse;
import org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.test.clients.ProtocolTypeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;

public class MRConsumerImpl extends MRBaseClient implements MRConsumer
{
	
	private static final String SUCCESS_MESSAGE = "Success";
	
	
	private Logger fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	public static List<String> stringToList ( String str )
	{
		final LinkedList<String> set = new LinkedList<String> ();
		if ( str != null )
		{
			final String[] parts = str.trim ().split ( "," );
			for ( String part : parts )
			{
				final String trimmed = part.trim();
				if ( trimmed.length () > 0 )
				{
					set.add ( trimmed );
				}
			}
		}
		return set;
	}
	
	public MRConsumerImpl ( Collection<String> hostPart, final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit, String filter, String apiKey_username, String apiSecret_password ) throws MalformedURLException
		{
			this( hostPart, topic, consumerGroup, consumerId, timeoutMs, limit, filter, apiKey_username, apiSecret_password, false );
		}
	
	public MRConsumerImpl ( Collection<String> hostPart, final String topic, final String consumerGroup,
		final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret, boolean allowSelfSignedCerts ) throws MalformedURLException
	{
		super ( hostPart, topic + "::" + consumerGroup + "::" + consumerId );

		fTopic = topic;
		fGroup = consumerGroup;
		fId = consumerId;
		fTimeoutMs = timeoutMs;
		fLimit = limit;
		fFilter = filter;

		//setApiCredentials ( apiKey, apiSecret );
	}

	@Override
	public Iterable<String> fetch () throws IOException,Exception
	{
		// fetch with the timeout and limit set in constructor
		return fetch ( fTimeoutMs, fLimit );
	}

	@Override
	public Iterable<String> fetch ( int timeoutMs, int limit ) throws IOException,Exception
	{
		final LinkedList<String> msgs = new LinkedList<String> ();

// FIXME: the timeout on the socket needs to be at least as long as the long poll
//		// sanity check for long poll timeout vs. socket read timeout
//		final int maxReasonableTimeoutMs = CambriaSingletonHttpClient.sfSoTimeoutMs * 9/10;
//		if ( timeoutMs > maxReasonableTimeoutMs )
//		{
//			log.warn ( "Long poll time (" + timeoutMs + ") is too high w.r.t. socket read timeout (" +
//				CambriaSingletonHttpClient.sfSoTimeoutMs + "). Reducing long poll timeout to " + maxReasonableTimeoutMs + "." );
//			timeoutMs = maxReasonableTimeoutMs;
//		}

	//	final String urlPath = createUrlPath ( timeoutMs, limit );

		//getLog().info ( "UEB GET " + urlPath );
		try
		{
			if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(protocolFlag)) {
				DMEConfigure(timeoutMs, limit);
			try 
			{
				//getLog().info ( "Receiving msgs from: " + url+subContextPath );
				String reply = sender.sendAndWait(timeoutMs+10000L);				
			//	System.out.println("Message received = "+reply);
				final JSONObject o =getResponseDataInJson(reply);
				//msgs.add(reply);
				if ( o != null )
				{
					final JSONArray a = o.getJSONArray ( "result" );
				//	final int b = o.getInt("status" );
					//if ( a != null && a.length()>0 )
					if ( a != null)
					{
						for ( int i=0; i<a.length (); i++ )
						{
							//msgs.add("DMAAP response status: "+Integer.toString(b));
							if (a.get(i) instanceof String)
								msgs.add ( a.getString(i) );
							else
						    	msgs.add ( a.getJSONObject(i).toString() );
							
							
						}
					}
//					else if(a != null && a.length()<1){
//						msgs.add ("[]");		
//						}
				}
			}	
			catch ( JSONException e )
				{
					// unexpected response
					reportProblemWithResponse ();
				}
				catch ( HttpException e )
				{
					throw new IOException ( e );
				}	
			}
			
			if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(protocolFlag)) {
				final String urlPath = createUrlPath (MRConstants.makeConsumerUrl ( host, fTopic, fGroup, fId,props.getProperty("Protocol")), timeoutMs, limit );
			
				
				try
				{
					final JSONObject o = get ( urlPath, username, password, protocolFlag );

					if ( o != null )
					{
						final JSONArray a = o.getJSONArray ( "result" );
						final int b = o.getInt("status" );
						//if ( a != null && a.length()>0 )
						if ( a != null)
						{
							for ( int i=0; i<a.length (); i++ )
							{
								msgs.add("DMAAP response status: "+Integer.toString(b));
								if (a.get(i) instanceof String)
									msgs.add ( a.getString(i) );
								else
									msgs.add ( a.getJSONObject(i).toString() );
								
							}
						}
//						else if(a != null && a.length()<1)
//							{
//								msgs.add ("[]");		
//							}
					}
				}
				catch ( JSONException e )
				{
					// unexpected response
					reportProblemWithResponse ();
				}
				catch ( HttpException e )
				{
					throw new IOException ( e );
				}
			} 
			
			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				final String urlPath = createUrlPath (MRConstants.makeConsumerUrl ( host, fTopic, fGroup, fId ,props.getProperty("Protocol")), timeoutMs, limit );
				

			try 
			{
				final JSONObject o = getAuth(urlPath, authKey, authDate, username, password, protocolFlag );
				if ( o != null )
				{
					final JSONArray a = o.getJSONArray ( "result" );
					final int b = o.getInt("status" );
					//if ( a != null && a.length()>0)
					if ( a != null)
					{
						for ( int i=0; i<a.length (); i++ )
						{
							msgs.add("DMAAP response status: "+Integer.toString(b));
							if (a.get(i) instanceof String)
								msgs.add ( a.getString(i) );
							else
						    	msgs.add ( a.getJSONObject(i).toString() );
							
						}
					}
//					else if(a != null && a.length()<1){
//						msgs.add ("[]");		
//						}
				}
			}
			catch ( JSONException e )
			{
				// unexpected response
				reportProblemWithResponse ();
			}
			catch ( HttpException e )
			{
				throw new IOException ( e );
			}
				
			}
			
		} catch ( JSONException e ) {
			// unexpected response
			reportProblemWithResponse ();
		} catch (HttpException e) {
			throw new IOException(e);
		} catch (Exception e ) {
			throw e;
		}


		return msgs;
	}

	private JSONObject getResponseDataInJson(String response) {
	try {
		
		
		//fLog.info("DMAAP response status: " + response.getStatus());

		//	final String responseData = response.readEntity(String.class);
		JSONTokener jsonTokener = new JSONTokener(response);
		JSONObject jsonObject = null;
		final char firstChar = jsonTokener.next();
    	jsonTokener.back();
		if ('[' == firstChar) {
			JSONArray jsonArray = new JSONArray(jsonTokener);
			jsonObject = new JSONObject();
			jsonObject.put("result", jsonArray);
		} else {
			jsonObject = new JSONObject(jsonTokener);
		}

		return jsonObject;
	} catch (JSONException excp) {
	//	fLog.error("DMAAP - Error reading response data.", excp);
		return null;
	}
	
	
	
}
	
	private JSONObject getResponseDataInJsonWithResponseReturned(String response) {
			JSONTokener jsonTokener = new JSONTokener(response);
			JSONObject jsonObject = null;
			final char firstChar = jsonTokener.next();
	    	jsonTokener.back();
	    	if(null != response && response.length()==0){
	    		return null;
	    	}
	    	
			if ('[' == firstChar) {
				JSONArray jsonArray = new JSONArray(jsonTokener);
				jsonObject = new JSONObject();
				jsonObject.put("result", jsonArray);
			} else if('{' == firstChar){
				return null;
			} else if('<' == firstChar){
				return null;
			}else{
				jsonObject = new JSONObject(jsonTokener);
			}

			return jsonObject;
		
	}
	
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final int fTimeoutMs;
	private final int fLimit;
	private String fFilter;
	private String username;
	private String password;
	private String host;
	 private  String latitude;
		private  String longitude;
		private  String version;
		private  String serviceName;
		private  String env;
		private  String partner;
		private String routeOffer;
		private  String subContextPath;
		private  String protocol;
		private  String methodType;
		private  String url;
		private  String dmeuser;
		private  String dmepassword;
		private  String contenttype;
	    private DME2Client sender;
		public String protocolFlag = ProtocolTypeConstants.DME2.getValue();
		public String consumerFilePath;
		private String authKey;
		private String authDate;
        private Properties props;
    	private HashMap<String, String> DMETimeOuts;
    	private String handlers;
    	public static String routerFilePath;
    	public static String getRouterFilePath() {
    		return routerFilePath;
    	}

    	public static void setRouterFilePath(String routerFilePath) {
    		MRSimplerBatchPublisher.routerFilePath = routerFilePath;
    	}
		public String getConsumerFilePath() {
			return consumerFilePath;
		}

		public void setConsumerFilePath(String consumerFilePath) {
			this.consumerFilePath = consumerFilePath;
		}

		public String getProtocolFlag() {
			return protocolFlag;
		}

		public void setProtocolFlag(String protocolFlag) {
			this.protocolFlag = protocolFlag;
		}
		
		private void DMEConfigure(int timeoutMs, int limit)throws IOException,DME2Exception, URISyntaxException{ 
			latitude = props.getProperty("Latitude");
			longitude = props.getProperty("Longitude");
			version = props.getProperty("Version");
			serviceName = props.getProperty("ServiceName");
			env = props.getProperty("Environment");
			partner = props.getProperty("Partner");
			routeOffer = props.getProperty("routeOffer");
			
			subContextPath=props.getProperty("SubContextPath")+fTopic+"/"+fGroup+"/"+fId;
		//	subContextPath=createUrlPath (subContextPath, timeoutMs, limit);
			//if (timeoutMs != -1) subContextPath=createUrlPath (subContextPath, timeoutMs);
			
			protocol = props.getProperty("Protocol"); 
			methodType = props.getProperty("MethodType");
			dmeuser = props.getProperty("username");
			dmepassword = props.getProperty("password");
			contenttype = props.getProperty("contenttype");
			handlers = props.getProperty("sessionstickinessrequired");
			//url =protocol+"://DME2SEARCH/"+ "service="+serviceName+"/"+"version="+version+"/"+"envContext="+env+"/"+"partner="+partner;
		//	url = protocol + "://"+serviceName+"?version="+version+"&envContext="+env+"&routeOffer="+partner;
		
			/**
			 * Changes to DME2Client url to use Partner for auto failover between data centers
			 * When Partner value is not provided use the routeOffer value for auto failover within a cluster 
			 */
			
			String preferredRouteKey = readRoute("preferredRouteKey");
						
			if (partner != null && !partner.isEmpty() && preferredRouteKey != null && !preferredRouteKey.isEmpty()) 
			{ 
				url = protocol + "://"+serviceName+"?version="+version+"&envContext="+env+"&partner="+partner+"&routeoffer="+preferredRouteKey; 
			}else  if (partner != null && !partner.isEmpty()) 
			{ 
				url = protocol + "://"+serviceName+"?version="+version+"&envContext="+env+"&partner="+partner; 
			}
			else if (routeOffer!=null && !routeOffer.isEmpty()) 
			{ 
				url = protocol + "://"+serviceName+"?version="+version+"&envContext="+env+"&routeoffer="+routeOffer;
			}
			
			//fLog.info("url :"+url);
						
			if(timeoutMs != -1 )url=url+"&timeout="+timeoutMs;
			if(limit != -1 )url=url+"&limit="+limit;

			DMETimeOuts = new HashMap<String, String>();
			DMETimeOuts.put("AFT_DME2_EP_READ_TIMEOUT_MS", props.getProperty("AFT_DME2_EP_READ_TIMEOUT_MS"));
			DMETimeOuts.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", props.getProperty("AFT_DME2_ROUNDTRIP_TIMEOUT_MS"));
			DMETimeOuts.put("AFT_DME2_EP_CONN_TIMEOUT", props.getProperty("AFT_DME2_EP_CONN_TIMEOUT"));
			DMETimeOuts.put("Content-Type", contenttype);
			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT",props.getProperty("AFT_ENVIRONMENT"));
		//	System.setProperty("DME2.DEBUG", "true");
			
			//SSL changes
			System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS",
					"SSLv3,TLSv1,TLSv1.1");
			System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
			System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
			//SSL changes
            
			sender = new DME2Client(new URI(url), timeoutMs+10000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);	
			if(dmeuser != null && dmepassword != null){
			sender.setCredentials(dmeuser, dmepassword);
			//System.out.println(dmepassword);
			}
			sender.setHeaders(DMETimeOuts);
			sender.setPayload("");               
			
			if(handlers.equalsIgnoreCase("yes")){
				sender.addHeader("AFT_DME2_EXCHANGE_REQUEST_HANDLERS", props.getProperty("AFT_DME2_EXCHANGE_REQUEST_HANDLERS"));
				sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", props.getProperty("AFT_DME2_EXCHANGE_REPLY_HANDLERS"));
				sender.addHeader("AFT_DME2_REQ_TRACE_ON", props.getProperty("AFT_DME2_REQ_TRACE_ON"));
				}else{
					sender.addHeader("AFT_DME2_EXCHANGE_REPLY_HANDLERS", "org.onap.dmaap.messagerouter.dmaapclient.nsa.mr.dme.client.HeaderReplyHandler");
				}
		/*	HeaderReplyHandler headerhandler= new HeaderReplyHandler(); 
			sender.setReplyHandler(headerhandler);*/
//			} catch (DME2Exception x) {
//				getLog().warn(x.getMessage(), x);
//				System.out.println("XXXXXXXXXXXX"+x);
//			} catch (URISyntaxException x) {
//				System.out.println(x);
//				getLog().warn(x.getMessage(), x);
//			} catch (Exception x) {
//				System.out.println("XXXXXXXXXXXX"+x);
//				getLog().warn(x.getMessage(), x);
//			}
		}
	public Properties getProps() {
			return props;
		}

		public void setProps(Properties props) {
			this.props = props;
		}

	protected String createUrlPath (String url, int timeoutMs , int limit ) throws IOException
	{
		final StringBuffer contexturl= new StringBuffer(url);
	//	final StringBuffer url = new StringBuffer ( CambriaConstants.makeConsumerUrl ( host, fTopic, fGroup, fId ) );
		final StringBuffer adds = new StringBuffer ();
		if ( timeoutMs > -1 ) adds.append ( "timeout=" ).append ( timeoutMs ); 
		if ( limit > -1 )
		{
			if ( adds.length () > 0 )
			{
				adds.append ( "&" );
			}
			adds.append ( "limit=" ).append ( limit );
		}
		if ( fFilter != null && fFilter.length () > 0 )
		{
			try {
				if ( adds.length () > 0 )
				{
					adds.append ( "&" );
				}
				adds.append("filter=").append(URLEncoder.encode(fFilter, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getMessage() + "....say whaaaat?!");
			}
		}
		if ( adds.length () > 0 )
		{
			contexturl.append ( "?" ).append ( adds.toString () );
		}
		
		//sender.setSubContext(url.toString());
		return contexturl.toString ();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getAuthDate() {
		return authDate;
	}

	public void setAuthDate(String authDate) {
		this.authDate = authDate;
	}

	public String getfFilter() {
		return fFilter;
	}

	public void setfFilter(String fFilter) {
		this.fFilter = fFilter;
	}
	
	private String readRoute(String routeKey) {

		try {
			
			MRClientFactory.prop.load(new FileReader(new File (MRClientFactory.routeFilePath)));

		} catch (Exception ex) {
			fLog.error("Reply Router Error " + ex.toString() );
		}
		String routeOffer = MRClientFactory.prop.getProperty(routeKey);		
		return routeOffer;
	}
	
	@Override
	public MRConsumerResponse fetchWithReturnConsumerResponse() {

		// fetch with the timeout and limit set in constructor
		return fetchWithReturnConsumerResponse(fTimeoutMs, fLimit);
	}

	@Override
	public MRConsumerResponse fetchWithReturnConsumerResponse(int timeoutMs,
			int limit) {
		final LinkedList<String> msgs = new LinkedList<String>();
		MRConsumerResponse mrConsumerResponse = new MRConsumerResponse();
		try {
			if (ProtocolTypeConstants.DME2.getValue().equalsIgnoreCase(
					protocolFlag)) {
				DMEConfigure(timeoutMs, limit);

				String reply = sender.sendAndWait(timeoutMs + 10000L);

				final JSONObject o = getResponseDataInJsonWithResponseReturned(reply);

				if (o != null) {
					final JSONArray a = o.getJSONArray("result");

					if (a != null) {
						for (int i = 0; i < a.length(); i++) {							
							if (a.get(i) instanceof String)
								msgs.add(a.getString(i));
							else
								msgs.add(a.getJSONObject(i).toString());

						}
					}

				}
				createMRConsumerResponse(reply, mrConsumerResponse);
			}

			if (ProtocolTypeConstants.AAF_AUTH.getValue().equalsIgnoreCase(
					protocolFlag)) {
				final String urlPath = createUrlPath(
						MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId,
								props.getProperty("Protocol")), timeoutMs,
						limit);

				String response = getResponse(urlPath, username, password,
						protocolFlag);

				final JSONObject o = getResponseDataInJsonWithResponseReturned(response);

				if (o != null) {
					final JSONArray a = o.getJSONArray("result");

					if (a != null) {
						for (int i = 0; i < a.length(); i++) {							
							if (a.get(i) instanceof String)
								msgs.add(a.getString(i));
							else
								msgs.add(a.getJSONObject(i).toString());

						}
					}

				}
				createMRConsumerResponse(response, mrConsumerResponse);
			}

			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(
					protocolFlag)) {
				final String urlPath = createUrlPath(
						MRConstants.makeConsumerUrl(host, fTopic, fGroup, fId,
								props.getProperty("Protocol")), timeoutMs,
						limit);

				String response  = getAuthResponse(urlPath, authKey, authDate,
						username, password, protocolFlag);
				final JSONObject o = getResponseDataInJsonWithResponseReturned(response);			
				if (o != null) {
					final JSONArray a = o.getJSONArray("result");

					if (a != null) {
						for (int i = 0; i < a.length(); i++) {
							if (a.get(i) instanceof String)
								msgs.add(a.getString(i));
							else
								msgs.add(a.getJSONObject(i).toString());

						}
					}

				}
				createMRConsumerResponse(response, mrConsumerResponse);
			}
			
			
			
		} catch (JSONException e) {	
			mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
			mrConsumerResponse.setResponseMessage(e.getMessage());
		} catch (HttpException e) {			
			mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
			mrConsumerResponse.setResponseMessage(e.getMessage());
		}catch(DME2Exception e){			
			mrConsumerResponse.setResponseCode(e.getErrorCode());
			mrConsumerResponse.setResponseMessage(e.getErrorMessage());
		}catch (Exception e) {			
			mrConsumerResponse.setResponseMessage(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
			mrConsumerResponse.setResponseMessage(e.getMessage());
		}
		mrConsumerResponse.setActualMessages(msgs);
		return mrConsumerResponse;
	}

	private void createMRConsumerResponse(String reply, MRConsumerResponse mrConsumerResponse) {
		
		if(reply.startsWith("{")){
			JSONObject jObject = new JSONObject(reply);
			String message = jObject.getString("message");
			int status = jObject.getInt("status");
		
			mrConsumerResponse.setResponseCode(Integer.toString(status));
			
			if(null != message){
				mrConsumerResponse.setResponseMessage(message);	
			}	
		}else if (reply.startsWith("<")){
			mrConsumerResponse.setResponseCode(getHTTPErrorResponseCode(reply));
			mrConsumerResponse.setResponseMessage(getHTTPErrorResponseMessage(reply));			
		}else{
			mrConsumerResponse.setResponseCode(String.valueOf(HttpStatus.SC_OK));
			mrConsumerResponse.setResponseMessage(SUCCESS_MESSAGE);	
		}
		
	}

 
}
