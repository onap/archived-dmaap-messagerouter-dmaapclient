/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *   Modifications Copyright © 2018 IBM.
 *   Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.UUID;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.dmaap.mr.client.impl.MRConsumerImpl;
import org.onap.dmaap.mr.client.impl.MRMetaClient;
import org.onap.dmaap.mr.client.impl.MRSimplerBatchPublisher;
import org.onap.dmaap.mr.tools.ValidatorUtil;

/**
 * A factory for MR clients.<br/>
 * <br/>
 * Use caution selecting a consumer creator factory. If the call doesn't accept
 * a consumer group name, then it creates a consumer that is not restartable.
 * That is, if you stop your process and start it again, your client will NOT
 * receive any missed messages on the topic. If you need to ensure receipt of
 * missed messages, then you must use a consumer that's created with a group
 * name and ID. (If you create multiple consumer processes using the same group,
 * load is split across them. Be sure to use a different ID for each
 * instance.)<br/>
 * <br/>
 * Publishers
 *
 * @author author
 */
public class MRClientFactory {

    private static MultivaluedMap<String, Object> httpHeadersMap;
    public static Map<String, String> DME2HeadersMap;
    public static String routeFilePath;

    public static FileReader routeReader;

    public static FileWriter routeWriter = null;
    public static Properties prop = null;

    /**
     * Instantiates MRClientFactory.
     */
    private MRClientFactory() {
        //prevents instantiation.
    }

    /**
     * Add getter to avoid direct access to static header map.
     *
     * @return
     */
    public static MultivaluedMap<String, Object> getHTTPHeadersMap() {
        return httpHeadersMap;
    }

    /**
     * Add setter to avoid direct access to static header map.
     *
     * @param headers
     */
    public static void setHTTPHeadersMap(MultivaluedMap<String, Object> headers) {
        httpHeadersMap = headers;
    }

    /**
     * Create a consumer instance with the default timeout and no limit on
     * messages returned. This consumer operates as an independent consumer
     * (i.e., not in a group) and is NOT re-startable across sessions.
     *
     * @param hostList A comma separated list of hosts to use to connect to MR. You
     *                 can include port numbers (3904 is the default). For example,
     *                 "hostname:8080,"
     * @param topic    The topic to consume
     * @return a consumer
     */
    public static MRConsumer createConsumer(String hostList, String topic) {
        return createConsumer(MRConsumerImpl.stringToList(hostList), topic);
    }

    /**
     * Create a consumer instance with the default timeout and no limit on
     * messages returned. This consumer operates as an independent consumer
     * (i.e., not in a group) and is NOT re-startable across sessions.
     *
     * @param hostSet The host used in the URL to MR. Entries can be "host:port".
     * @param topic   The topic to consume
     * @return a consumer
     */
    public static MRConsumer createConsumer(Collection<String> hostSet, String topic) {
        return createConsumer(hostSet, topic, null);
    }

    /**
     * Create a consumer instance with server-side filtering, the default
     * timeout, and no limit on messages returned. This consumer operates as an
     * independent consumer (i.e., not in a group) and is NOT re-startable
     * across sessions.
     *
     * @param hostSet The host used in the URL to MR. Entries can be "host:port".
     * @param topic   The topic to consume
     * @param filter  a filter to use on the server side
     * @return a consumer
     */
    public static MRConsumer createConsumer(Collection<String> hostSet, String topic, String filter) {
        return createConsumer(hostSet, topic, UUID.randomUUID().toString(), "0", -1, -1, filter, null, null);
    }

    /**
     * Create a consumer instance with the default timeout, and no limit on
     * messages returned. This consumer can operate in a logical group and is
     * re-startable across sessions when you use the same group and ID on
     * restart.
     *
     * @param hostSet       The host used in the URL to MR. Entries can be "host:port".
     * @param topic         The topic to consume
     * @param consumerGroup The name of the consumer group this consumer is part of
     * @param consumerId    The unique id of this consume in its group
     * @return a consumer
     */
    public static MRConsumer createConsumer(Collection<String> hostSet, final String topic, final String consumerGroup,
                                            final String consumerId) {
        return createConsumer(hostSet, topic, consumerGroup, consumerId, -1, -1);
    }

    /**
     * Create a consumer instance with the default timeout, and no limit on
     * messages returned. This consumer can operate in a logical group and is
     * re-startable across sessions when you use the same group and ID on
     * restart.
     *
     * @param hostSet       The host used in the URL to MR. Entries can be "host:port".
     * @param topic         The topic to consume
     * @param consumerGroup The name of the consumer group this consumer is part of
     * @param consumerId    The unique id of this consume in its group
     * @param timeoutMs     The amount of time in milliseconds that the server should keep
     *                      the connection open while waiting for message traffic. Use -1
     *                      for default timeout.
     * @param limit         A limit on the number of messages returned in a single call.
     *                      Use -1 for no limit.
     * @return a consumer
     */
    public static MRConsumer createConsumer(Collection<String> hostSet, final String topic, final String consumerGroup,
                                            final String consumerId, int timeoutMs, int limit) {
        return createConsumer(hostSet, topic, consumerGroup, consumerId, timeoutMs, limit, null, null, null);
    }

    /**
     * Create a consumer instance with the default timeout, and no limit on
     * messages returned. This consumer can operate in a logical group and is
     * re-startable across sessions when you use the same group and ID on
     * restart. This consumer also uses server-side filtering.
     *
     * @param hostList      A comma separated list of hosts to use to connect to MR. You
     *                      can include port numbers (3904 is the default)"
     * @param topic         The topic to consume
     * @param consumerGroup The name of the consumer group this consumer is part of
     * @param consumerId    The unique id of this consume in its group
     * @param timeoutMs     The amount of time in milliseconds that the server should keep
     *                      the connection open while waiting for message traffic. Use -1
     *                      for default timeout.
     * @param limit         A limit on the number of messages returned in a single call.
     *                      Use -1 for no limit.
     * @param filter        A Highland Park filter expression using only built-in filter
     *                      components. Use null for "no filter".
     * @return a consumer
     */
    public static MRConsumer createConsumer(String hostList, final String topic, final String consumerGroup,
                                            final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret) {
        return createConsumer(MRConsumerImpl.stringToList(hostList), topic, consumerGroup, consumerId, timeoutMs, limit,
                filter, apiKey, apiSecret);
    }

    /**
     * Create a consumer instance with the default timeout, and no limit on
     * messages returned. This consumer can operate in a logical group and is
     * re-startable across sessions when you use the same group and ID on
     * restart. This consumer also uses server-side filtering.
     *
     * @param hostSet       The host used in the URL to MR. Entries can be "host:port".
     * @param topic         The topic to consume
     * @param consumerGroup The name of the consumer group this consumer is part of
     * @param consumerId    The unique id of this consume in its group
     * @param timeoutMs     The amount of time in milliseconds that the server should keep
     *                      the connection open while waiting for message traffic. Use -1
     *                      for default timeout.
     * @param limit         A limit on the number of messages returned in a single call.
     *                      Use -1 for no limit.
     * @param filter        A Highland Park filter expression using only built-in filter
     *                      components. Use null for "no filter".
     * @return a consumer
     */
    public static MRConsumer createConsumer(Collection<String> hostSet, final String topic, final String consumerGroup,
                                            final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret) {
        if (MRClientBuilders.sfConsumerMock != null) {
            return MRClientBuilders.sfConsumerMock;
        }
        try {
            return new MRConsumerImpl.MRConsumerImplBuilder().setHostPart(hostSet).setTopic(topic)
                    .setConsumerGroup(consumerGroup).setConsumerId(consumerId)
                    .setTimeoutMs(timeoutMs).setLimit(limit).setFilter(filter)
                    .setApiKey_username(apiKey).setApiSecret_password(apiSecret)
                    .createMRConsumerImpl();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //*************************************************************************
    //*************************************************************************
    //*************************************************************************

    /**
     * Create a publisher that sends each message (or group of messages)
     * immediately. Most applications should favor higher latency for much
     * higher message throughput and the "simple publisher" is not a good
     * choice.
     *
     * @param hostlist The host used in the URL to MR. Can be "host:port", can be
     *                 multiple comma-separated entries.
     * @param topic    The topic on which to publish messages.
     * @return a publisher
     */
    public static MRBatchingPublisher createSimplePublisher(String hostlist, String topic) {
        return createBatchingPublisher(hostlist, topic, 1, 1);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown. Message payloads are
     * not compressed.
     *
     * @param hostlist     The host used in the URL to MR. Can be "host:port", can be
     *                     multiple comma-separated entries.
     * @param topic        The topic on which to publish messages.
     * @param maxBatchSize The largest set of messages to batch
     * @param maxAgeMs     The maximum age of a message waiting in a batch
     * @return a publisher
     */
    public static MRBatchingPublisher createBatchingPublisher(String hostlist, String topic, int maxBatchSize,
                                                              long maxAgeMs) {
        return createBatchingPublisher(hostlist, topic, maxBatchSize, maxAgeMs, false);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown.
     *
     * @param hostlist     The host used in the URL to MR. Can be "host:port", can be
     *                     multiple comma-separated entries.
     * @param topic        The topic on which to publish messages.
     * @param maxBatchSize The largest set of messages to batch
     * @param maxAgeMs     The maximum age of a message waiting in a batch
     * @param compress     use gzip compression
     * @return a publisher
     */
    public static MRBatchingPublisher createBatchingPublisher(String hostlist, String topic, int maxBatchSize,
                                                              long maxAgeMs, boolean compress) {
        return createBatchingPublisher(MRConsumerImpl.stringToList(hostlist), topic, maxBatchSize, maxAgeMs, compress);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown.
     *
     * @param hostSet      A set of hosts to be used in the URL to MR. Can be
     *                     "host:port". Use multiple entries to enable failover.
     * @param topic        The topic on which to publish messages.
     * @param maxBatchSize The largest set of messages to batch
     * @param maxAgeMs     The maximum age of a message waiting in a batch
     * @param compress     use gzip compression
     * @return a publisher
     */
    public static MRBatchingPublisher createBatchingPublisher(String[] hostSet, String topic, int maxBatchSize,
                                                              long maxAgeMs, boolean compress) {
        final TreeSet<String> hosts = new TreeSet<>();
        Collections.addAll(hosts, hostSet);
        return createBatchingPublisher(hosts, topic, maxBatchSize, maxAgeMs, compress);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown.
     *
     * @param hostSet      A set of hosts to be used in the URL to MR. Can be
     *                     "host:port". Use multiple entries to enable failover.
     * @param topic        The topic on which to publish messages.
     * @param maxBatchSize The largest set of messages to batch
     * @param maxAgeMs     The maximum age of a message waiting in a batch
     * @param compress     use gzip compression
     * @return a publisher
     */
    public static MRBatchingPublisher createBatchingPublisher(Collection<String> hostSet, String topic,
                                                              int maxBatchSize, long maxAgeMs, boolean compress) {
        return new MRSimplerBatchPublisher.Builder().againstUrls(hostSet).onTopic(topic).batchTo(maxBatchSize, maxAgeMs)
                .compress(compress).build();
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown.
     *
     * @param host         A host to be used in the URL to MR. Can be "host:port". Use
     *                     multiple entries to enable failover.
     * @param topic        The topic on which to publish messages.
     * @param username     username
     * @param password     password
     * @param maxBatchSize The largest set of messages to batch
     * @param maxAgeMs     The maximum age of a message waiting in a batch
     * @param compress     use gzip compression
     * @param protocolFlag http auth or ueb auth or dme2 method
     * @return MRBatchingPublisher obj
     */
    public static MRBatchingPublisher createBatchingPublisher(String host, String topic, final String username,
                                                              final String password, int maxBatchSize, long maxAgeMs, boolean compress, String protocolFlag) {
        MRSimplerBatchPublisher pub = new MRSimplerBatchPublisher.Builder()
                .againstUrls(MRConsumerImpl.stringToList(host)).onTopic(topic).batchTo(maxBatchSize, maxAgeMs)
                .compress(compress).build();

        pub.setHost(host);
        pub.setUsername(username);
        pub.setPassword(password);
        pub.setProtocolFlag(protocolFlag);
        return pub;
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown
     *
     * @param props props set all properties for publishing message
     * @return MRBatchingPublisher obj
     * @throws FileNotFoundException exc
     * @throws IOException           ioex
     */
    public static MRBatchingPublisher createBatchingPublisher(Properties props, boolean withResponse)
            throws FileNotFoundException, IOException {
        return createInternalBatchingPublisher(props, withResponse);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown
     *
     * @param props props set all properties for publishing message
     * @return MRBatchingPublisher obj
     * @throws FileNotFoundException exc
     * @throws IOException           ioex
     */
    public static MRBatchingPublisher createBatchingPublisher(Properties props)
            throws FileNotFoundException, IOException {
        return createInternalBatchingPublisher(props, false);
    }

    /**
     * Create a publisher that batches messages. Be sure to close the publisher
     * to send the last batch and ensure a clean shutdown
     *
     * @param producerFilePath set all properties for publishing message
     * @return MRBatchingPublisher obj
     * @throws FileNotFoundException exc
     * @throws IOException           ioex
     */
    public static MRBatchingPublisher createBatchingPublisher(final String producerFilePath)
            throws FileNotFoundException, IOException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(producerFilePath)) {
            props.load(input);
        }
        return createBatchingPublisher(props);
    }

    /**
     * Create a publisher that will contain send methods that return response
     * object to user.
     *
     * @param producerFilePath set all properties for publishing message
     * @return MRBatchingPublisher obj
     * @throws FileNotFoundException exc
     * @throws IOException           ioex
     */
    public static MRBatchingPublisher createBatchingPublisher(final String producerFilePath, boolean withResponse)
            throws FileNotFoundException, IOException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(producerFilePath)) {
            props.load(input);
        }
        return createBatchingPublisher(props, withResponse);
    }

    protected static MRBatchingPublisher createInternalBatchingPublisher(Properties props, boolean withResponse)
            throws FileNotFoundException, IOException {
        assert props != null;
        MRSimplerBatchPublisher pub;

        String messageSentThreadOccurrence = props.getProperty(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE);
        if (messageSentThreadOccurrence == null || messageSentThreadOccurrence.isEmpty()) {
            messageSentThreadOccurrence = props.getProperty(DmaapClientConst.MESSAGE_SENT_THREAD_OCCURRENCE_OLD);
        }

        if (withResponse) {
            pub = new MRSimplerBatchPublisher.Builder()
                    .againstUrlsOrServiceName(MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.HOST)), MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.SERVICE_NAME)), props.getProperty(DmaapClientConst.TRANSPORT_TYPE))
                    .onTopic(props.getProperty(DmaapClientConst.TOPIC))
                    .batchTo(Integer.parseInt(props.getProperty(DmaapClientConst.MAX_BATCH_SIZE)),
                            Integer.parseInt(props.getProperty(DmaapClientConst.MAX_AGE_MS)))
                    .compress(Boolean.parseBoolean(props.getProperty(DmaapClientConst.COMPRESS)))
                    .httpThreadTime(Integer.parseInt(messageSentThreadOccurrence))
                    .withResponse(withResponse).build();
        } else {
            pub = new MRSimplerBatchPublisher.Builder()
                    .againstUrlsOrServiceName(MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.HOST)), MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.SERVICE_NAME)), props.getProperty(DmaapClientConst.TRANSPORT_TYPE))
                    .onTopic(props.getProperty(DmaapClientConst.TOPIC))
                    .batchTo(Integer.parseInt(props.getProperty(DmaapClientConst.MAX_BATCH_SIZE)),
                            Integer.parseInt(props.getProperty(DmaapClientConst.MAX_AGE_MS)))
                    .compress(Boolean.parseBoolean(props.getProperty(DmaapClientConst.COMPRESS)))
                    .httpThreadTime(Integer.parseInt(messageSentThreadOccurrence)).build();
        }
        pub.setHost(props.getProperty(DmaapClientConst.HOST));
        if (props.getProperty(DmaapClientConst.TRANSPORT_TYPE).equalsIgnoreCase(ProtocolType.AUTH_KEY.getValue())) {

            pub.setAuthKey(props.getProperty(DmaapClientConst.AUTH_KEY));
            pub.setAuthDate(props.getProperty(DmaapClientConst.AUTH_DATE));
            pub.setUsername(props.getProperty(DmaapClientConst.USERNAME));
            pub.setPassword(props.getProperty(DmaapClientConst.PASSWORD));
        } else {
            pub.setUsername(props.getProperty(DmaapClientConst.USERNAME));
            pub.setPassword(props.getProperty(DmaapClientConst.PASSWORD));
        }
        pub.setProtocolFlag(props.getProperty(DmaapClientConst.TRANSPORT_TYPE));
        pub.setProps(props);
        prop = new Properties();
        if (props.getProperty(DmaapClientConst.TRANSPORT_TYPE).equalsIgnoreCase(ProtocolType.DME2.getValue())) {
            routeFilePath = props.getProperty(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH);
            routeReader = new FileReader(new File(routeFilePath));
            File fo = new File(routeFilePath);
            if (!fo.exists()) {
                routeWriter = new FileWriter(new File(routeFilePath));
            }
        }
        return pub;
    }

    /**
     * Create an identity manager client to work with API keys.
     *
     * @param hostSet   A set of hosts to be used in the URL to MR. Can be
     *                  "host:port". Use multiple entries to enable failover.
     * @param apiKey    Your API key
     * @param apiSecret Your API secret
     * @return an identity manager
     */
    public static MRIdentityManager createIdentityManager(Collection<String> hostSet, String apiKey, String apiSecret) {
        MRIdentityManager cim;
        try {
            cim = new MRMetaClient(hostSet);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        cim.setApiCredentials(apiKey, apiSecret);
        return cim;
    }

    /**
     * Create a topic manager for working with topics.
     *
     * @param hostSet   A set of hosts to be used in the URL to MR. Can be
     *                  "host:port". Use multiple entries to enable failover.
     * @param apiKey    Your API key
     * @param apiSecret Your API secret
     * @return a topic manager
     */
    public static MRTopicManager createTopicManager(Collection<String> hostSet, String apiKey, String apiSecret) {
        MRMetaClient tmi;
        try {
            tmi = new MRMetaClient(hostSet);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        tmi.setApiCredentials(apiKey, apiSecret);
        return tmi;
    }

    /**
     * Inject a consumer. Used to support unit tests.
     *
     * @param cc
     */
    public static void $testInject(MRConsumer cc) {
        MRClientBuilders.sfConsumerMock = cc;
    }

    public static MRConsumer createConsumer(String host, String topic, String username, String password, String group,
                                            String id, int timeout, int limit, String protocalFlag, String consumerFilePath) {

        MRConsumerImpl sub;
        try {
            sub = new MRConsumerImpl.MRConsumerImplBuilder()
                    .setHostPart(MRConsumerImpl.stringToList(host)).setTopic(topic)
                    .setConsumerGroup(group).setConsumerId(id).setTimeoutMs(timeout).setLimit(limit)
                    .setFilter(null).setApiKey_username(null).setApiSecret_password(null)
                    .createMRConsumerImpl();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        sub.setUsername(username);
        sub.setPassword(password);
        sub.setHost(host);
        sub.setProtocolFlag(protocalFlag);
        sub.setConsumerFilePath(consumerFilePath);
        return sub;

    }

    public static MRConsumer createConsumer(String host, String topic, String username, String password, String group,
                                            String id, String protocalFlag, String consumerFilePath, int timeout, int limit) {

        MRConsumerImpl sub;
        try {
            sub = new MRConsumerImpl.MRConsumerImplBuilder()
                    .setHostPart(MRConsumerImpl.stringToList(host)).setTopic(topic)
                    .setConsumerGroup(group).setConsumerId(id).setTimeoutMs(timeout).setLimit(limit)
                    .setFilter(null).setApiKey_username(null).setApiSecret_password(null)
                    .createMRConsumerImpl();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        sub.setUsername(username);
        sub.setPassword(password);
        sub.setHost(host);
        sub.setProtocolFlag(protocalFlag);
        sub.setConsumerFilePath(consumerFilePath);
        return sub;

    }

    public static MRConsumer createConsumer(String consumerFilePath) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(consumerFilePath)) {
            props.load(input);
        }
        return createConsumer(props);
    }

    public static MRConsumer createConsumer(Properties props) throws FileNotFoundException, IOException {
        int timeout;
        ValidatorUtil.validateSubscriber(props);
        if (props.getProperty(DmaapClientConst.TIMEOUT) != null) {
            timeout = Integer.parseInt(props.getProperty(DmaapClientConst.TIMEOUT));
        } else {
            timeout = -1;
        }
        int limit;
        if (props.getProperty(DmaapClientConst.LIMIT) != null) {
            limit = Integer.parseInt(props.getProperty(DmaapClientConst.LIMIT));
        } else {
            limit = -1;
        }
        String group;
        if (props.getProperty(DmaapClientConst.GROUP) == null) {
            group = UUID.randomUUID().toString();
        } else {
            group = props.getProperty(DmaapClientConst.GROUP);
        }
        MRConsumerImpl sub = null;
        if (props.getProperty(DmaapClientConst.TRANSPORT_TYPE).equalsIgnoreCase(ProtocolType.AUTH_KEY.getValue())) {
            sub = new MRConsumerImpl.MRConsumerImplBuilder()
                    .setHostPart(MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.HOST)))
                    .setTopic(props.getProperty(DmaapClientConst.TOPIC)).setConsumerGroup(group)
                    .setConsumerId(props.getProperty(DmaapClientConst.ID)).setTimeoutMs(timeout).setLimit(limit)
                    .setFilter(props.getProperty(DmaapClientConst.FILTER))
                    .setApiKey_username(props.getProperty(DmaapClientConst.AUTH_KEY))
                    .setApiSecret_password(props.getProperty(DmaapClientConst.AUTH_DATE)).createMRConsumerImpl();
            sub.setAuthKey(props.getProperty(DmaapClientConst.AUTH_KEY));
            sub.setAuthDate(props.getProperty(DmaapClientConst.AUTH_DATE));
            sub.setUsername(props.getProperty(DmaapClientConst.USERNAME));
            sub.setPassword(props.getProperty(DmaapClientConst.PASSWORD));
        } else {
            sub = new MRConsumerImpl.MRConsumerImplBuilder()
                    .setHostPart(MRConsumerImpl.stringToList(props.getProperty(DmaapClientConst.HOST)))
                    .setTopic(props.getProperty(DmaapClientConst.TOPIC)).setConsumerGroup(group)
                    .setConsumerId(props.getProperty(DmaapClientConst.ID)).setTimeoutMs(timeout).setLimit(limit)
                    .setFilter(props.getProperty(DmaapClientConst.FILTER))
                    .setApiKey_username(props.getProperty(DmaapClientConst.USERNAME))
                    .setApiSecret_password(props.getProperty(DmaapClientConst.PASSWORD)).createMRConsumerImpl();
            sub.setUsername(props.getProperty(DmaapClientConst.USERNAME));
            sub.setPassword(props.getProperty(DmaapClientConst.PASSWORD));
        }

        sub.setProps(props);
        sub.setHost(props.getProperty(DmaapClientConst.HOST));
        sub.setProtocolFlag(props.getProperty(DmaapClientConst.TRANSPORT_TYPE));
        sub.setfFilter(props.getProperty(DmaapClientConst.FILTER));
        if (props.getProperty(DmaapClientConst.TRANSPORT_TYPE).equalsIgnoreCase(ProtocolType.DME2.getValue())) {
            MRConsumerImpl.setRouterFilePath(props.getProperty(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH));
            routeFilePath = props.getProperty(DmaapClientConst.DME2PREFERRED_ROUTER_FILE_PATH);
            routeReader = new FileReader(new File(routeFilePath));
            prop = new Properties();
            File fo = new File(routeFilePath);
            if (!fo.exists()) {
                routeWriter = new FileWriter(new File(routeFilePath));
            }
        }

        return sub;
    }
}
