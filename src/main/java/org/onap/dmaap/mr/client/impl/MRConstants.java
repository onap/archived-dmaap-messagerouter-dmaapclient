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

import org.apache.http.HttpHost;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class MRConstants {

    private MRConstants() {

    }

    private static final String PROTOCOL = "http";
    public static final String CONTEXT = "/";
    public static final String BASE_PATH = "events/";
    public static final int STD_MR_SERVICE_PORT = 8080;

    public static String escape(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String makeUrl(String rawTopic) {
        final String cleanTopic = escape(rawTopic);

        return new StringBuilder().
                append(MRConstants.CONTEXT).
                append(MRConstants.BASE_PATH).
                append(cleanTopic).toString();
    }

    public static String makeUrl(final String host, final String rawTopic) {
        final String cleanTopic = escape(rawTopic);
        final StringBuilder url = new StringBuilder();

        if (!host.startsWith("http") && !host.startsWith("https")) {
            url.append(PROTOCOL).append("://");
        }
        url.append(host);
        url.append(MRConstants.CONTEXT);
        url.append(MRConstants.BASE_PATH);
        url.append(cleanTopic);
        return url.toString();
    }

    public static String makeUrl(final String host, final String rawTopic, final String transferProtocol, final String partition) {
        final String cleanTopic = escape(rawTopic);

        final StringBuilder url = new StringBuilder();

        if (transferProtocol != null && !transferProtocol.isEmpty()) {
            url.append(transferProtocol).append("://");
        } else {
            url.append(PROTOCOL).append("://");
        }
        url.append(host);
        url.append(MRConstants.CONTEXT);
        url.append(MRConstants.BASE_PATH);
        url.append(cleanTopic);
        if (partition!= null && !partition.isEmpty())
            url.append("?partitionKey=").append(partition);
        return url.toString();
    }

    public static String makeConsumerUrl(String topic, String rawConsumerGroup, String rawConsumerId) {
        final String cleanConsumerGroup = escape(rawConsumerGroup);
        final String cleanConsumerId = escape(rawConsumerId);
        return MRConstants.CONTEXT + MRConstants.BASE_PATH + topic + "/" + cleanConsumerGroup + "/" + cleanConsumerId;
    }

    /**
     * Create a list of HttpHosts from an input list of strings. Input strings have
     * host[:port] as format. If the port section is not provided, the default port is used.
     *
     * @param hosts
     * @return a list of hosts
     */
    public static List<HttpHost> createHostsList(Collection<String> hosts) {
        final ArrayList<HttpHost> convertedHosts = new ArrayList<>();
        for (String host : hosts) {
            if (host.length() == 0) continue;
            convertedHosts.add(hostForString(host));
        }
        return convertedHosts;
    }

    /**
     * Return an HttpHost from an input string. Input string has
     * host[:port] as format. If the port section is not provided, the default port is used.
     *
     * @param host
     * @return a list of hosts
     */
    public static HttpHost hostForString(String host) {
        if (host.length() < 1) throw new IllegalArgumentException("An empty host entry is invalid.");

        String hostPart = host;
        int port = STD_MR_SERVICE_PORT;

        final int colon = host.indexOf(':');
        if (colon == 0) throw new IllegalArgumentException("Host entry '" + host + "' is invalid.");
        if (colon > 0) {
            hostPart = host.substring(0, colon).trim();

            final String portPart = host.substring(colon + 1).trim();
            if (portPart.length() > 0) {
                try {
                    port = Integer.parseInt(portPart);
                } catch (NumberFormatException x) {
                    throw new IllegalArgumentException("Host entry '" + host + "' is invalid.", x);
                }
            }
            // else: use default port on "foo:"
        }

        return new HttpHost(hostPart, port);
    }

    public static String makeConsumerUrl(String host, String fTopic, String fGroup, String fId, final String transferprotocol) {
        final String cleanConsumerGroup = escape(fGroup);
        final String cleanConsumerId = escape(fId);

        StringBuilder url = new StringBuilder();

        if (transferprotocol != null && !transferprotocol.equals("")) {
            url.append(transferprotocol).append("://");
        } else {
            url.append(PROTOCOL).append("://");
        }

        url.append(host)
                .append(CONTEXT)
                .append(BASE_PATH)
                .append(fTopic)
                .append("/").append(cleanConsumerGroup)
                .append("/").append(cleanConsumerId);

        return url.toString();
    }
}
