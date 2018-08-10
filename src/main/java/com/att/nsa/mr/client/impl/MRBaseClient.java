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
package com.att.nsa.mr.client.impl;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.http.CacheUse;
import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.mr.client.MRClient;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

public class MRBaseClient extends HttpClient implements MRClient {


	protected MRBaseClient(Collection<String> hosts) throws MalformedURLException {
		super(ConnectionType.HTTP, hosts, MRConstants.kStdMRServicePort);

		fLog = LoggerFactory.getLogger(this.getClass().getName());
	}

	protected MRBaseClient(Collection<String> hosts, int stdSvcPort) throws MalformedURLException {
		super(ConnectionType.HTTP, hosts, stdSvcPort);

		fLog = LoggerFactory.getLogger(this.getClass().getName());
	}

	protected MRBaseClient(Collection<String> hosts, String clientSignature) throws MalformedURLException {
		super(ConnectionType.HTTP, hosts, MRConstants.kStdMRServicePort, clientSignature, CacheUse.NONE, 1, 1L,
				TimeUnit.MILLISECONDS, 32, 32, 600000);

		fLog = LoggerFactory.getLogger(this.getClass().getName());
	}

	@Override
	public void close() {
	}

	protected Set<String> jsonArrayToSet(JSONArray a) {
		if (a == null)
			return null;

		final TreeSet<String> set = new TreeSet<String>();
		for (int i = 0; i < a.length(); i++) {
			set.add(a.getString(i));
		}
		return set;
	}

	public void logTo(Logger log) {
		fLog = log;
		replaceLogger(log);
	}

	protected Logger getLog() {
		return fLog;
	}

	private Logger fLog;

	public JSONObject post(final String path, final byte[] data, final String contentType, final String username,
			final String password, final String protocalFlag) throws HttpException, JSONException {
		if ((null != username && null != password)) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			String encoding = Base64.encodeAsString(username + ":" + password);

			response = DmaapClientUtil.postResponsewtBasicAuth(target, encoding,data, contentType);

			return getResponseDataInJson(response);
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}
	
	public JSONObject postNoAuth(final String path, final byte[] data, String contentType)
			throws HttpException, JSONException {
		WebTarget target = null;
		Response response = null;
		if (contentType == null) {
			contentType = "text/pain";
		}
		target = DmaapClientUtil.getTarget(path);

		response = DmaapClientUtil.postResponsewtNoAuth(target, data, contentType);

		return getResponseDataInJson(response);
	}

	public String postWithResponse(final String path, final byte[] data, final String contentType,
			final String username, final String password, final String protocolFlag)
			throws HttpException, JSONException {
		String responseData = null;
		if ((null != username && null != password)) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			String encoding = Base64.encodeAsString(username + ":" + password);

			response = DmaapClientUtil.postResponsewtBasicAuth(target, encoding,data, contentType);

			responseData = (String)response.readEntity(String.class);
			return responseData;
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}
	
	public String postNoAuthWithResponse(final String path, final byte[] data, String contentType)
			throws HttpException, JSONException {

		String responseData = null;
		WebTarget target = null;
		Response response = null;
		if (contentType == null) {
			contentType = "text/pain";
		}
		target = DmaapClientUtil.getTarget(path);

		response = DmaapClientUtil.postResponsewtNoAuth(target, data, contentType);
		responseData = (String) response.readEntity(String.class);
		return responseData;
	}

	public JSONObject postAuth(final String path, final byte[] data, final String contentType, final String authKey,
			final String authDate, final String username, final String password, final String protocolFlag)
			throws HttpException, JSONException {
		if ((null != username && null != password)) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			response =DmaapClientUtil.postResponsewtCambriaAuth(target, authKey, authDate, data, contentType);
			return getResponseDataInJson(response);
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}

	public String postAuthwithResponse(final String path, final byte[] data, final String contentType,
			final String authKey, final String authDate, final String username, final String password,
			final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		if ((null != username && null != password)) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			response = DmaapClientUtil.postResponsewtCambriaAuth(target, authKey, authDate, data, contentType);
			responseData = (String)response.readEntity(String.class);
			return responseData;

		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/AuthDate parameter(s) cannot be null or empty.");
		}
	}

	public JSONObject get(final String path, final String username, final String password, final String protocolFlag)
			throws HttpException, JSONException {
		if (null != username && null != password) {

			 WebTarget target=null;
			 Response response=null;
			 
			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				target = DmaapClientUtil.getTarget(path);
				response = DmaapClientUtil.getResponsewtCambriaAuth(target, username, password);
			} else {
				target = DmaapClientUtil.getTarget(path, username, password);
				String encoding = Base64.encodeAsString(username + ":" + password);

				response = DmaapClientUtil.getResponsewtBasicAuth(target, encoding);

			}
			return getResponseDataInJson(response);
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	public String getResponse(final String path, final String username, final String password,
			final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		if (null != username && null != password) {
			WebTarget target=null;
			Response response=null;
			if (ProtocolTypeConstants.AUTH_KEY.getValue().equalsIgnoreCase(protocolFlag)) {
				target = DmaapClientUtil.getTarget(path);
				response = DmaapClientUtil.getResponsewtCambriaAuth(target, username, password);
			} else {
				target = DmaapClientUtil.getTarget(path, username, password);
				String encoding = Base64.encodeAsString(username + ":" + password);
				response = DmaapClientUtil.getResponsewtBasicAuth(target, encoding);
			}
			MRClientFactory.HTTPHeadersMap = response.getHeaders();

			String transactionid = response.getHeaderString("transactionid");
			if (transactionid != null && !transactionid.equalsIgnoreCase("")) {
				fLog.info("TransactionId : " + transactionid);
			}

			responseData = (String)response.readEntity(String.class);
			return responseData;
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	public JSONObject getAuth(final String path, final String authKey, final String authDate, final String username,
			final String password, final String protocolFlag) throws HttpException, JSONException {
		if (null != username && null != password) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			response = DmaapClientUtil.getResponsewtCambriaAuth(target, authKey, authDate);

			return getResponseDataInJson(response);
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	public JSONObject getNoAuth(final String path, final String username, final String password,
			final String protocolFlag) throws HttpException, JSONException {
		if (null != username && null != password) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			response = DmaapClientUtil.getResponsewtNoAuth(target);

			return getResponseDataInJson(response);
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	public String getAuthResponse(final String path, final String authKey, final String authDate, final String username,
			final String password, final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		if (null != username && null != password) {
			WebTarget target=null;
			Response response=null;
			target = DmaapClientUtil.getTarget(path, username, password);
			response = DmaapClientUtil.getResponsewtCambriaAuth(target, authKey, authDate);

			MRClientFactory.HTTPHeadersMap = response.getHeaders();

			String transactionid = response.getHeaderString("transactionid");
			if (transactionid != null && !transactionid.equalsIgnoreCase("")) {
				fLog.info("TransactionId : " + transactionid);
			}

			responseData = (String)response.readEntity(String.class);
			return responseData;
		} else {
			throw new HttpException(
					"Authentication Failed: Username/password/AuthKey/Authdate parameter(s) cannot be null or empty.");
		}
	}

	public String getNoAuthResponse(String path, final String username, final String password,
			final String protocolFlag) throws HttpException, JSONException {
		String responseData = null;
		WebTarget target=null;
		Response response=null;
		target = DmaapClientUtil.getTarget(path, username, password);
		response = DmaapClientUtil.getResponsewtNoAuth(target);

		MRClientFactory.HTTPHeadersMap = response.getHeaders();

		String transactionid = response.getHeaderString("transactionid");
		if (transactionid != null && !transactionid.equalsIgnoreCase("")) {
			fLog.info("TransactionId : " + transactionid);
		}

		responseData = (String)response.readEntity(String.class);
		return responseData;

	}


	private JSONObject getResponseDataInJson(Response response) throws JSONException {
		try {
			MRClientFactory.HTTPHeadersMap = response.getHeaders();
			

			// MultivaluedMap<String, Object> headersMap =
			// for(String key : headersMap.keySet()) {
			String transactionid = response.getHeaderString("transactionid");
			if (transactionid != null && !transactionid.equalsIgnoreCase("")) {
				fLog.info("TransactionId : " + transactionid);
			}


			if (response.getStatus() == 403) {
				JSONObject jsonObject = null;
				jsonObject = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(response.getEntity());
				jsonObject.put("result", jsonArray);
				jsonObject.put("status", response.getStatus());
				return jsonObject;
			}
			String responseData = (String)response.readEntity(String.class);

			JSONTokener jsonTokener = new JSONTokener(responseData);
			JSONObject jsonObject = null;
			final char firstChar = jsonTokener.next();
			jsonTokener.back();
			if ('[' == firstChar) {
				JSONArray jsonArray = new JSONArray(jsonTokener);
				jsonObject = new JSONObject();
				jsonObject.put("result", jsonArray);
				jsonObject.put("status", response.getStatus());
			} else {
				jsonObject = new JSONObject(jsonTokener);
				jsonObject.put("status", response.getStatus());
			}

			return jsonObject;
		} catch (JSONException excp) {
			fLog.error("DMAAP - Error reading response data.", excp);
			return null;
		}

	}

	public String getHTTPErrorResponseMessage(String responseString) {

		String response = null;
		int beginIndex = 0;
		int endIndex = 0;
		if (responseString.contains("<body>")) {

			beginIndex = responseString.indexOf("body>") + 5;
			endIndex = responseString.indexOf("</body");
			response = responseString.substring(beginIndex, endIndex);
		}

		return response;

	}

	public String getHTTPErrorResponseCode(String responseString) {

		String response = null;
		int beginIndex = 0;
		int endIndex = 0;
		if (responseString.contains("<title>")) {
			beginIndex = responseString.indexOf("title>") + 6;
			endIndex = responseString.indexOf("</title");
			response = responseString.substring(beginIndex, endIndex);
		}

		return response;
	}

}
