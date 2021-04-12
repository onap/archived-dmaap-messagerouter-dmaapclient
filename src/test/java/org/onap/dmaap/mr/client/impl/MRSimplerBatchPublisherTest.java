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

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.onap.dmaap.mr.client.MRClientFactory;
import org.onap.dmaap.mr.client.MRPublisher.Message;
import org.onap.dmaap.mr.client.ProtocolTypeConstants;
import org.onap.dmaap.mr.client.response.MRPublisherResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MRSimplerBatchPublisherTest {

    File outFile;

    public void setUp(String contentType) throws Exception {
        Properties properties = new Properties();
        properties.load(
                MRSimplerBatchPublisherTest.class.getClassLoader().getResourceAsStream("dme2/producer.properties"));

        String routeFilePath = "dme2/preferredRoute.txt";

        File file = new File(MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
        properties.put("DME2preferredRouterFilePath",
                MRSimplerBatchPublisherTest.class.getClassLoader().getResource(routeFilePath).getFile());
        if (contentType != null) {
            properties.put("contenttype", contentType);
        }
        outFile = new File(file.getParent() + "/producer_tmp.properties");
        properties.store(new FileOutputStream(outFile), "");
    }

    @Test
    public void testSend() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponse() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void testSendBatchWithResponseConText() throws Exception {

        setUp("text/plain");

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponseContCambria() throws Exception {

        setUp("application/cambria-zip");

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponseProtKey() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());
        pub.setProtocolFlag(ProtocolTypeConstants.AUTH_KEY.getValue());
        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponseProtAaf() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());
        pub.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());
        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponseProtNoAuth() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath());
        pub.setProtocolFlag(ProtocolTypeConstants.HTTPNOAUTH.getValue());
        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());

        final List<Message> stuck = pub.close(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, stuck.size());

    }

    @Test
    public void testSendBatchWithResponsecontypeText() throws Exception {

        setUp("text/plain");

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", "payload");
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void testSendBatchWithResponsecontypeCambria() throws Exception {

        setUp("application/cambria-zip");

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", "payload");
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void testSendBatchWithResponsePrAuthKey() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);
        pub.setProtocolFlag(ProtocolTypeConstants.AUTH_KEY.getValue());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void testSendBatchWithResponsePrAaf() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);
        pub.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void testSendBatchWithResponsePrNoauth() throws Exception {

        setUp(null);

        final MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);
        pub.setProtocolFlag(ProtocolTypeConstants.HTTPNOAUTH.getValue());

        // publish some messages
        final JSONObject msg1 = new JSONObject();
        pub.send("MyPartitionKey", msg1.toString());
        MRPublisherResponse pubResponse = new MRPublisherResponse();
        pub.setPubResponse(pubResponse);

        MRPublisherResponse mrPublisherResponse = pub.sendBatchWithResponse();
        Assert.assertEquals(1, mrPublisherResponse.getPendingMsgs());

    }

    @Test
    public void createPublisherResponse() throws Exception {
        setUp(null);
        MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        MRPublisherResponse response = pub.createMRPublisherResponse("{\"message\": \"published the message\", \"status\": \"200\"}", new MRPublisherResponse());
        assertEquals("200", response.getResponseCode());

    }

    @Test
    public void createPublisherResponseSucc() throws Exception {
        setUp(null);
        MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        MRPublisherResponse response = pub.createMRPublisherResponse("{\"fakemessage\": \"published the message\", \"fakestatus\": \"200\"}", new MRPublisherResponse());
        assertEquals("200", response.getResponseCode());

    }

    @Test
    public void createPublisherResponseError() throws Exception {
        setUp(null);
        MRSimplerBatchPublisher pub = (MRSimplerBatchPublisher) MRClientFactory
                .createBatchingPublisher(outFile.getPath(), true);

        MRPublisherResponse response = pub.createMRPublisherResponse("", new MRPublisherResponse());
        assertEquals("400", response.getResponseCode());

    }

}
