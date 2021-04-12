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

package org.onap.dmaap.mr.dme.client;


import org.json.JSONObject;
import org.onap.dmaap.mr.client.MRBatchingPublisher;
import org.onap.dmaap.mr.client.MRClientFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Map;

/**
 * An example of how to use the Java publisher.
 *
 * @author author
 */
public class SimpleExamplePublisher {
    static String content = null;
    static String messageSize = null;
    static String transport = null;
    static String messageCount = null;

    public void publishMessage(String producerFilePath) throws IOException, InterruptedException {

        // create our publisher
        // publish some messages

        StringBuilder sb = new StringBuilder();
        final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher(producerFilePath);

        if (content.equalsIgnoreCase("text/plain")) {
            for (int i = 0; i < Integer.parseInt(messageCount); i++) {
                for (int j = 0; j < Integer.parseInt(messageSize); j++) {
                    sb.append("T");
                }

                pub.send(sb.toString());
            }
        } else if (content.equalsIgnoreCase("application/cambria")) {
            for (int i = 0; i < Integer.parseInt(messageCount); i++) {
                for (int j = 0; j < Integer.parseInt(messageSize); j++) {
                    sb.append("C");
                }
                pub.send("Key", sb.toString());
            }
        } else if (content.equalsIgnoreCase("application/json")) {
            for (int i = 0; i < Integer.parseInt(messageCount); i++) {

                final JSONObject msg12 = new JSONObject();
                msg12.put("Name", "DMaaP Reference Client to Test jason Message");

                pub.send(msg12.toString());

            }
        }

        // close the publisher to make sure everything's sent before exiting.
        // The batching
        // publisher interface allows the app to get the set of unsent messages.
        // It could
        // write them to disk, for example, to try to send them later.
    /*  final List<message> stuck = pub.close(20, TimeUnit.SECONDS);
        if (stuck.size() > 0) {
            System.err.println(stuck.size() + " messages unsent");
        } else {
            System.out.println("Clean exit; all messages sent.");
        }*/

        if (transport.equalsIgnoreCase("HTTP")) {
            MultivaluedMap<String, Object> headersMap = MRClientFactory.getHTTPHeadersMap();
            for (String key : headersMap.keySet()) {
                System.out.println("Header Key " + key);
                System.out.println("Header Value " + headersMap.get(key));
            }
        } else {
            Map<String, String> dme2headersMap = MRClientFactory.DME2HeadersMap;
            for (String key : dme2headersMap.keySet()) {
                System.out.println("Header Key " + key);
                System.out.println("Header Value " + dme2headersMap.get(key));
            }
        }

    }

    public static void main(String[] args) throws InterruptedException, Exception {

        String producerFilePath = args[0];
        content = args[1];
        messageSize = args[2];
        transport = args[3];
        messageCount = args[4];


        SimpleExamplePublisher publisher = new SimpleExamplePublisher();

        publisher.publishMessage("D:\\SG\\producer.properties");
    }

}
