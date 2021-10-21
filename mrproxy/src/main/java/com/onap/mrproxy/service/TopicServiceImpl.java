/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2021 Orange Intellectual Property. All rights reserved.
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
package com.onap.mrproxy.service;

import com.onap.mrproxy.exception.TopicNotFoundException;
import com.onap.mrproxy.utils.TopicBean;
import com.onap.mrproxy.utils.TopicDetails;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    @Override
    public ResponseEntity getTopicDetails(String topicName) {
        log.info("getTopicDetails Service");
        String doYourStrimziThing = "Returned From Strimzi";
        //TODO Add the Strimzi call, and handle the response.  Call started below in getTopicDetailsStrimzi()


        if(doYourStrimziThing == null) {
            log.error("getTopicDetails Service - Topic Not Found "+topicName);
            throw new TopicNotFoundException(HttpStatus.NOT_FOUND, "No topic found");
        }

        TopicDetails topicDetailsResponse = new TopicDetails("owner", topicName, doYourStrimziThing, false);
        return new ResponseEntity(topicDetailsResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity createTopic(TopicBean topicBean) {
        //TODO Add the Strimzi call, and handle the response.
        log.info("createTopic Service");
        TopicDetails newlyCreatedTopic = new TopicDetails("owner", topicBean.getTopicName(), topicBean.getTopicDescription(), topicBean.isTransactionEnabled());
        return new ResponseEntity(newlyCreatedTopic, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getListOfTopics() {
        log.info("getListOfTopics Service");
        HttpResponse<String> x = getTopicsFromStrimzi();

        return new ResponseEntity(x.body(), HttpStatus.OK);
    }


    private HttpResponse<String> getTopicsFromStrimzi() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/topics"))
            .build();
        try {
            return client.send(request,
                HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
