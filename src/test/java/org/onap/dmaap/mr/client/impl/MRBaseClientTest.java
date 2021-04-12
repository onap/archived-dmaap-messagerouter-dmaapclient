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
package org.onap.dmaap.mr.client.impl;

import org.apache.http.HttpException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.apache.http.conn.ssl.*", "jdk.internal.reflect.*"})
@PrepareForTest({DmaapClientUtil.class})
public class MRBaseClientTest {

    // @InjectMocks
    private MRBaseClient mrBaseClient;
    private Collection<String> hosts = new HashSet<>(Arrays.asList("localhost:8080"));
    private String clientSignature = "topic" + "::" + "cg" + "::" + "cid";
    private ClientConfig config = null;

    @Before
    public void setup() throws MalformedURLException {
        mrBaseClient = new MRBaseClient(hosts, clientSignature);
        PowerMockito.mockStatic(DmaapClientUtil.class);
    }

    @Test
    public void testGet() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(
                DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username", "password"))
                .thenReturn(response);

        JSONObject result = mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
        assertEquals(200, result.getInt("status"));
        assertEquals("test", result.getString("test"));
        verify(response, atLeast(1)).getStatus();
        verify(response).readEntity(String.class);
        verify(response).getHeaders();
    }

    @Test
    public void testGet_403() throws JSONException, HttpException {
        ResponseBuilder responseBuilder = Response.status(403);
        Mockito
                .when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());
        JSONObject result = mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
        assertEquals(403, result.getInt("status"));
    }

    @Test
    public void testGet_basicauth() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                Base64.encodeAsString("username:password"))).thenReturn(response);

        JSONObject result = mrBaseClient.get("/path", "username", "password", "HTTPAAF");
        assertEquals(200, result.getInt("status"));
        verify(response, atLeast(1)).getStatus();
        verify(response).readEntity(String.class);
        verify(response).getHeaders();

    }

    @Test(expected = HttpException.class)
    public void testGet_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito.when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                "password"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.get("/path", null, null, "HTTPAUTH");
    }

    @Test
    public void testGet_wrongjson() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("[[");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(
                DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username", "password"))
                .thenReturn(response);

        mrBaseClient.get("/path", "username", "password", "HTTPAUTH");
        verify(response, atLeast(1)).getStatus();
        verify(response).readEntity(String.class);
        verify(response).getHeaders();
    }

    @Test
    public void testGetResponse() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(
                DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username", "password"))
                .thenReturn(response);

        mrBaseClient.getResponse("/path", "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test
    public void testGetResponse_aaf() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                Base64.encodeAsString("username:password"))).thenReturn(response);

        mrBaseClient.getResponse("/path", "username", "password", "HTTPAAF");
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testGetResponse_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.getResponse("/path", null, null, "HTTPAUTH");
    }

    @Test
    public void testAuthResponse() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(
                DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username", "password"))
                .thenReturn(response);

        mrBaseClient.getAuthResponse("/path", "username", "password", "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testAuthResponsee_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.getAuthResponse("/path", null, null, null, null, "HTTPAUTH");

    }

    @Test
    public void testPostAuth() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito
                .when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password", ("{\"test\":\"test\"}").getBytes(), "application/json"))
                .thenReturn(response);

        mrBaseClient.postAuth(new PostAuthDataObject().setPath("/path")
                .setData(("{\"test\":\"test\"}").getBytes())
                .setContentType("application/json")
                .setAuthKey("username")
                .setAuthDate("password")
                .setUsername("username")
                .setPassword("password")
                .setProtocolFlag("HTTPAUTH"));
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testPostAuth_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password", ("{\"test\":\"test\"}").getBytes(), "application/json"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.postAuth(new PostAuthDataObject().setPath("/path")
                .setData(("{\"test\":\"test\"}").getBytes())
                .setContentType("application/json")
                .setAuthKey(null)
                .setAuthDate(null)
                .setUsername(null)
                .setPassword(null)
                .setProtocolFlag("HTTPAUTH"));
    }

    @Test
    public void testGetNoAuthResponse() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.getResponsewtNoAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"))).thenReturn(response);

        mrBaseClient.getNoAuthResponse("/path", "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test
    public void testPost() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.postResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                Base64.encodeAsString("username:password"), ("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(response);

        mrBaseClient.post("/path", ("{\"test\":\"test\"}").getBytes(), "application/json", "username",
                "password", "HTTPAUTH");
        verify(response, atLeast(1)).getStatus();
        verify(response).readEntity(String.class);
        verify(response).getHeaders();

    }

    @Test(expected = HttpException.class)
    public void testPost_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                        Base64.encodeAsString("username:password")))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.post("/path", ("{\"test\":\"test\"}").getBytes(), "application/json", null, null,
                "HTTPAUTH");

    }

    @Test
    public void testPostAuthwithResponse() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito
                .when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password", ("{\"test\":\"test\"}").getBytes(), "application/json"))
                .thenReturn(response);

        mrBaseClient.postAuthwithResponse("/path", ("{\"test\":\"test\"}").getBytes(), "application/json",
                "username", "password", "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testPostAuthwithResponse_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password", ("{\"test\":\"test\"}").getBytes(), "application/json"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.postAuthwithResponse("/path", ("{\"test\":\"test\"}").getBytes(), "application/json",
                null, null, null, null, "HTTPAUTH");

    }

    @Test
    public void testPostWithResponse() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.postResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                Base64.encodeAsString("username:password"), ("{\"test\":\"test\"}").getBytes(), "application/json")).thenReturn(response);

        mrBaseClient.postWithResponse("/path", ("{\"test\":\"test\"}").getBytes(), "application/json",
                "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testPostWithResponse_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.getResponsewtBasicAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"),
                        Base64.encodeAsString("username:password")))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.postWithResponse("/path", ("{\"test\":\"test\"}").getBytes(), "application/json", null,
                null, "HTTPAUTH");

    }

    @Test
    public void testGetAuth() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(
                DmaapClientUtil.getResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username", "password"))
                .thenReturn(response);
        mrBaseClient.getAuth("/path", "username", "password", "username", "password", "HTTPAUTH");
        assertTrue(true);

    }

    @Test(expected = HttpException.class)
    public void testGetAuth_error() throws JSONException, HttpException {

        ResponseBuilder responseBuilder = Response.ok();
        Mockito
                .when(DmaapClientUtil.postResponsewtCambriaAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"), "username",
                        "password", ("{\"test\":\"test\"}").getBytes(), "application/json"))
                .thenReturn(
                        responseBuilder.header("transactionid", "transactionid").entity("{\"test\":\"test\"}").build());

        mrBaseClient.getAuth("/path", null, null, null, null, "HTTPAUTH");

    }

    @Test
    public void testGetNoAuth() throws JSONException, HttpException {

        Response response = Mockito.mock(Response.class);
        MultivaluedMap<String, Object> map = new StringKeyIgnoreCaseMultivaluedMap<>();
        map.add("transactionid", "transactionid");

        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.readEntity(String.class)).thenReturn("{\"test\":\"test\"}");
        Mockito.when(response.getHeaders()).thenReturn(map);

        Mockito.when(DmaapClientUtil.getResponsewtNoAuth(DmaapClientUtil.getTarget(getClientConfig(), "/path"))).thenReturn(response);
        mrBaseClient.getNoAuth("/path");
        assertTrue(true);

    }


    @Test
    public void testGetHTTPErrorResponseMessage() {
        assertEquals("testtest", mrBaseClient.getHTTPErrorResponseMessage("<body>testtest</body>"));

    }

    @Test
    public void getGTTPErrorResponseCode() {
        assertEquals("500", mrBaseClient.getHTTPErrorResponseCode("<title>500</title>"));
    }


    private ClientConfig getClientConfig() {
        if (config == null) {
            config = DmaapClientUtil.getClientConfig(null);
        }
        return config;

    }

}
