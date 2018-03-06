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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.HttpException;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.apache.http.conn.ssl.*")
@PrepareForTest({ DmaapClientUtil.class })
public class MRBaseClientTest {

	@InjectMocks
	private  MRBaseClient mrBaseClient;
	@Mock
	Collection<String> hosts=new HashSet<>(Arrays.asList("localhost:8080"));
	@Mock
	String clientSignature="topic" + "::" + "cg" + "::" + "cid";
	
	
	@Before
	public void setup() {
		
		PowerMockito.mockStatic(DmaapClientUtil.class );
		

	}

	@Test
	public void testGet() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGet_403() throws JSONException, HttpException {
		ResponseBuilder responseBuilder = Response.status(403);
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGet_basicauth() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.get("/path", "username", "password", "HTTPAAF");
		assertTrue(true);

	}
	
	@Test(expected=HttpException.class)
	public void testGet_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.get("/path", null, null, "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGet_wrongjson() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("[[").build());
		
		mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
		assertTrue(true);
	}
	
	@Test
	public void testGetResponse() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getResponse("/path", "username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGetResponse_aaf() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getResponse("/path", "username", "password", "HTTPAAF");
		assertTrue(true);

	}
	
	@Test(expected=HttpException.class)
	public void testGetResponse_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getResponse("/path", null,null, "HTTPAUTH");

	}
	
	@Test
	public void testAuthResponse() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getAuthResponse("/path", "username", "password", "username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testAuthResponsee_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getAuthResponse("/path", null,null,null,null, "HTTPAUTH");

	}
	
	@Test
	public void testPostAuth() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password", new String("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postAuth("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json","username", "password","username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testPostAuth_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password", new String("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postAuth("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json",null,null,null,null, "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGetNoAuthResponse() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtNoAuth(DmaapClientUtil.getTarget("/path"))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getNoAuthResponse("/path", "username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testPost() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.post("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json","username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testPost_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.post("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json",null, null, "HTTPAUTH");

	}
	
	@Test
	public void testPostAuthwithResponse() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password", new String("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postAuthwithResponse("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json","username", "password", "username", "password","HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testPostAuthwithResponse_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password", new String("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postAuthwithResponse("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json",null,null,null,null,"HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testPostWithResponse() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postWithResponse("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json","username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testPostWithResponse_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget("/path"),  Base64.encodeAsString( "username:password" ))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.postWithResponse("/path",new String("{\"test\":\"test\"}").getBytes(), "application/json",null, null, "HTTPAUTH");

	}
	
	@Test
	public void testGetAuth() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());		
		mrBaseClient.getAuth("/path","username", "password", "username", "password","HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testGetAuth_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget("/path"), "username", "password", new String("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
		
		mrBaseClient.getAuth("/path",null,null,null,null,"HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGetNoAuth() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtNoAuth(DmaapClientUtil.getTarget("/path"))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());		
		mrBaseClient.getNoAuth("/path","username", "password", "HTTPAUTH");
		assertTrue(true);

	}
	
	
	@Test(expected=HttpException.class)
	public void testGetNoAuth_error() throws JSONException, HttpException {
		
		ResponseBuilder responseBuilder = Response.ok();
		PowerMockito.when(DmaapClientUtil.getResponsewtNoAuth(DmaapClientUtil.getTarget("/path"))).thenReturn(responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());		
		mrBaseClient.getNoAuth("/path",null, null, "HTTPAUTH");
		assertTrue(true);

	}
	
	@Test
	public void testGetHTTPErrorResponseMessage(){
		
		assertEquals(mrBaseClient.getHTTPErrorResponseMessage("<body>testtest</body>"), "testtest");

	}
	
	@Test
	public void getGTTPErrorResponseCode(){
		
		assertEquals(mrBaseClient.getHTTPErrorResponseMessage("<body>testtest</body>"), "testtest");

	}
	
	
}
