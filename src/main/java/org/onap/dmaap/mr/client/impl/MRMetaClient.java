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

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.dmaap.mr.client.MRIdentityManager;
import org.onap.dmaap.mr.client.MRTopicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class MRMetaClient extends MRBaseClient implements MRTopicManager, MRIdentityManager {
    private static final String BASE_URI_TOPIC = "/topics";
    private static final String BASE_URI_APIKEY = "/apiKeys";

    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_EMAIL = "email";

    private static final Logger logger = LoggerFactory.getLogger(MRMetaClient.class);

    public MRMetaClient(Collection<String> baseUrls) throws MalformedURLException {
        super(baseUrls);
    }

    @Override
    public Set<String> getTopics() throws IOException {
        final TreeSet<String> set = new TreeSet<>();
        try {
            final JSONObject topicSet = get(BASE_URI_TOPIC);
            final JSONArray a = topicSet.getJSONArray("topics");
            for (int i = 0; i < a.length(); i++) {
                set.add(a.getString(i));
            }
        } catch (HttpObjectNotFoundException e) {
            getLog().warn("No /topics endpoint on service.");
            logger.error("HttpObjectNotFoundException: ", e);
        } catch (JSONException e) {
            getLog().warn("Bad /topics result from service.");
            logger.error("JSONException: ", e);
        } catch (HttpException e) {
            throw new IOException(e);
        }
        return set;
    }

    @Override
    public TopicInfo getTopicMetadata(String topic) throws HttpObjectNotFoundException, IOException {
        try {
            final JSONObject topicData = get(BASE_URI_TOPIC + "/" + MRConstants.escape(topic));
            return new TopicInfo() {
                @Override
                public String getOwner() {
                    return topicData.optString("owner", null);
                }

                @Override
                public String getDescription() {
                    return topicData.optString(PARAM_DESCRIPTION, null);
                }

                @Override
                public Set<String> getAllowedProducers() {
                    final JSONObject acl = topicData.optJSONObject("writerAcl");
                    if (acl != null && acl.optBoolean("enabled", true)) {
                        return jsonArrayToSet(acl.optJSONArray("users"));
                    }
                    return null;
                }

                @Override
                public Set<String> getAllowedConsumers() {
                    final JSONObject acl = topicData.optJSONObject("readerAcl");
                    if (acl != null && acl.optBoolean("enabled", true)) {
                        return jsonArrayToSet(acl.optJSONArray("users"));
                    }
                    return null;
                }
            };
        } catch (JSONException e) {
            throw new IOException(e);
        } catch (HttpException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void createTopic(String topicName, String topicDescription, int partitionCount, int replicationCount) throws HttpException, IOException {
        final JSONObject o = new JSONObject();
        o.put("topicName", topicName);
        o.put("topicDescription", topicDescription);
        o.put("partitionCount", partitionCount);
        o.put("replicationCount", replicationCount);
        post(BASE_URI_TOPIC + "/create", o, false);
    }

    @Override
    public void deleteTopic(String topic) throws HttpException, IOException {
        delete(BASE_URI_TOPIC + "/" + MRConstants.escape(topic));
    }

    @Override
    public boolean isOpenForProducing(String topic) throws HttpObjectNotFoundException, IOException {
        return null == getAllowedProducers(topic);
    }

    @Override
    public Set<String> getAllowedProducers(String topic) throws HttpObjectNotFoundException, IOException {
        return getTopicMetadata(topic).getAllowedProducers();
    }

    @Override
    public void allowProducer(String topic, String apiKey) throws HttpObjectNotFoundException, HttpException, IOException {
        put(BASE_URI_TOPIC + "/" + MRConstants.escape(topic) + "/producers/" + MRConstants.escape(apiKey), new JSONObject());
    }

    @Override
    public void revokeProducer(String topic, String apiKey) throws HttpException, IOException {
        delete(BASE_URI_TOPIC + "/" + MRConstants.escape(topic) + "/producers/" + MRConstants.escape(apiKey));
    }

    @Override
    public boolean isOpenForConsuming(String topic) throws HttpObjectNotFoundException, IOException {
        return null == getAllowedConsumers(topic);
    }

    @Override
    public Set<String> getAllowedConsumers(String topic) throws HttpObjectNotFoundException, IOException {
        return getTopicMetadata(topic).getAllowedConsumers();
    }

    @Override
    public void allowConsumer(String topic, String apiKey) throws HttpObjectNotFoundException, HttpException, IOException {
        put(BASE_URI_TOPIC + "/" + MRConstants.escape(topic) + "/consumers/" + MRConstants.escape(apiKey), new JSONObject());
    }

    @Override
    public void revokeConsumer(String topic, String apiKey) throws HttpException, IOException {
        delete(BASE_URI_TOPIC + "/" + MRConstants.escape(topic) + "/consumers/" + MRConstants.escape(apiKey));
    }

    @Override
    public ApiCredential createApiKey(String email, String description) throws HttpException, MRApiException, IOException {
        try {
            final JSONObject o = new JSONObject();
            o.put(PARAM_EMAIL, email);
            o.put(PARAM_DESCRIPTION, description);
            final JSONObject reply = post(BASE_URI_APIKEY + "/create", o, true);
            return new ApiCredential(reply.getString("key"), reply.getString("secret"));
        } catch (JSONException e) {
            // the response doesn't meet our expectation
            throw new MRApiException("The API key response is incomplete.", e);
        }
    }

    @Override
    public ApiKey getApiKey(String apiKey) throws HttpObjectNotFoundException, HttpException, IOException {
        final JSONObject keyEntry = get(BASE_URI_APIKEY + "/" + MRConstants.escape(apiKey));
        if (keyEntry == null) {
            return null;
        }

        return new ApiKey() {
            @Override
            public String getEmail() {
                final JSONObject aux = keyEntry.optJSONObject("aux");
                if (aux != null) {
                    return aux.optString(PARAM_EMAIL);
                }
                return null;
            }

            @Override
            public String getDescription() {
                final JSONObject aux = keyEntry.optJSONObject("aux");
                if (aux != null) {
                    return aux.optString(PARAM_DESCRIPTION);
                }
                return null;
            }
        };
    }

    @Override
    public void updateCurrentApiKey(String email, String description) throws HttpObjectNotFoundException, HttpException, IOException {
        final JSONObject o = new JSONObject();
        if (email != null) o.put(PARAM_EMAIL, email);
        if (description != null) o.put(PARAM_DESCRIPTION, description);
        patch(BASE_URI_APIKEY + "/" + MRConstants.escape(getCurrentApiKey()), o);
    }

    @Override
    public void deleteCurrentApiKey() throws HttpException, IOException {
        delete(BASE_URI_APIKEY + "/" + MRConstants.escape(getCurrentApiKey()));
    }
}
