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
package org.onap.dmaap.mr.test.clients;

import org.json.JSONObject;
import org.onap.dmaap.mr.client.MRBatchingPublisher;
import org.onap.dmaap.mr.client.MRClientBuilders.PublisherBuilder;
import org.onap.dmaap.mr.client.MRPublisher.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SamplePublisher {
    public static void main(String[] args) throws IOException, InterruptedException {
        final Logger logger = LoggerFactory.getLogger(SampleConsumer.class);
        // read the hosts(s) from the command line
        final String hosts = (args.length > 0 ? args[0] : "localhost:8181");

        // read the topic name from the command line

        final String topic = (args.length > 1 ? args[1] : "org.onap.dmaap.mr.testingTopic");

        // set up some batch limits and the compression flag
        final int maxBatchSize = 100;
        final int maxAgeMs = 250;
        final boolean withGzip = false;

        // create our publisher

        final MRBatchingPublisher pub = new PublisherBuilder().
                usingHosts(hosts).
                onTopic(topic).limitBatch(maxBatchSize, maxAgeMs).
                authenticatedBy("CG0TXc2Aa3v8LfBk", "pj2rhxJWKP23pgy8ahMnjH88").
                build();
        // publish some messages
        final JSONObject msg1 = new JSONObject();
        msg1.put("name", "tttttttttttttttt");
        msg1.put("greeting", "ooooooooooooooooo");
        pub.send("MyPartitionKey", msg1.toString());

        final JSONObject msg2 = new JSONObject();
        msg2.put("now", System.currentTimeMillis());
        pub.send("MyOtherPartitionKey", msg2.toString());

        // ...

        // close the publisher to make sure everything's sent before exiting. The batching
        // publisher interface allows the app to get the set of unsent messages. It could
        // write them to disk, for example, to try to send them later.
        final List<Message> stuck = pub.close(20, TimeUnit.SECONDS);
        if (!stuck.isEmpty()) {
            logger.warn(stuck.size() + " messages unsent");
        } else {
            logger.info("Clean exit; all messages sent.");
        }
    }
}
