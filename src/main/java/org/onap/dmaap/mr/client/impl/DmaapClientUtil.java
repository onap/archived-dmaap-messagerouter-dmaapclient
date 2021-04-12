/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
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

import java.util.Properties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class DmaapClientUtil {

    private DmaapClientUtil() {

    }

    private static final String MR_AUTH_CONSTANT = "X-CambriaAuth";
    private static final String MR_DATE_CONSTANT = "X-CambriaDate";
    private static final String[] httpClientProperties = {ClientProperties.CONNECT_TIMEOUT,
        ClientProperties.READ_TIMEOUT, ClientProperties.PROXY_USERNAME, ClientProperties.PROXY_PASSWORD,
        ClientProperties.PROXY_URI};

    public static ClientConfig getClientConfig(Properties properties) {
        ClientConfig config = new ClientConfig();
        if (properties != null && !properties.isEmpty()) {
            setHttpClientProperties(config, properties);
        }
        return config;
    }

    private static void setHttpClientProperties(ClientConfig config, Properties properties) {
        for (String httpClientProperty : httpClientProperties) {
            if ((properties.getProperty(httpClientProperty) != null)) {
                config.property(httpClientProperty, properties.getProperty(httpClientProperty));
            }
        }
        if ((properties.getProperty(ClientProperties.PROXY_URI) != null)
                && !(properties.getProperty(ClientProperties.PROXY_URI).isEmpty())) {
            config.connectorProvider(new ApacheConnectorProvider());
        } // else the default connectorProvider (HttpConnectorProvider) will be used

    }

    public static WebTarget getTarget(ClientConfig config, final String path, final String username,
                                      final String password) {
        Client client = null;
        if (config != null) {
            client = ClientBuilder.newClient(config);
        } else {
            client = ClientBuilder.newClient();
        }
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(username, password);
        client.register(feature);

        return client.target(path);
    }

    public static WebTarget getTarget(ClientConfig config, final String path) {

        Client client = null;
        if (config != null && config.getProperties().size() > 0) {
            client = ClientBuilder.newClient(config);
        } else {
            client = ClientBuilder.newClient();
        }
        return client.target(path);
    }

    public static Response getResponsewtCambriaAuth(WebTarget target, String username, String password) {
        return target.request().header(MR_AUTH_CONSTANT, username).header(MR_DATE_CONSTANT, password).get();

    }

    public static Response postResponsewtCambriaAuth(WebTarget target, String username, String password, byte[] data,
                                                     String contentType) {
        return target.request().header(MR_AUTH_CONSTANT, username).header(MR_DATE_CONSTANT, password)
                .post(Entity.entity(data, contentType));

    }

    public static Response getResponsewtBasicAuth(WebTarget target, String authHeader) {

        return target.request().header("Authorization", "Basic " + authHeader).get();

    }

    public static Response postResponsewtBasicAuth(WebTarget target, String authHeader, byte[] data,
                                                   String contentType) {

        return target.request().header("Authorization", "Basic " + authHeader).post(Entity.entity(data, contentType));

    }

    public static Response getResponsewtNoAuth(WebTarget target) {

        return target.request().get();

    }

    public static Response postResponsewtNoAuth(WebTarget target, byte[] data, String contentType) {
        return target.request().post(Entity.entity(data, contentType));

    }

}
