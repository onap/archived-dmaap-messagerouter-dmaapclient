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
package com.onap.mrproxy.controller;
import static org.mockito.Mockito.when;
import com.onap.mrproxy.service.TopicService;
import com.onap.mrproxy.utils.TopicBean;
import com.onap.mrproxy.utils.TopicDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TopicControllerTest {

    @Mock
    TopicService topicService;

    @InjectMocks
    TopicController topicController;

    @Test
    void testGetTopicDetailsSuccess(){
        String topicName = "topicName";
        when(topicService.getTopicDetails(ArgumentMatchers.anyString())).thenReturn(testTopicDetails(topicName));

        ResponseEntity<TopicDetails> topicDetailsResponse = topicController.getTopicDetails(topicName);

        Assertions.assertNotNull(topicDetailsResponse);
        Assertions.assertEquals("description", topicDetailsResponse.getBody().getDescription());
        Assertions.assertEquals("owner", topicDetailsResponse.getBody().getOwner());
        Assertions.assertEquals(topicName, topicDetailsResponse.getBody().getTopicname());
        Assertions.assertEquals(HttpStatus.OK, topicDetailsResponse.getStatusCode());
        Assertions.assertFalse(topicDetailsResponse.getBody().isTxenabled());
    }

    @Test
    void testCreateTopicSuccess(){
        String topicName = "topicName";
        TopicBean topicBeanRequest = new TopicBean(topicName, "description", 2, 1, false);
        when(topicService.createTopic(ArgumentMatchers.any())).thenReturn(testTopicDetails(topicName));

        ResponseEntity<TopicDetails> topicDetailsResponse = topicController.createTopic(topicBeanRequest);

        Assertions.assertNotNull(topicDetailsResponse);
        Assertions.assertEquals("description", topicDetailsResponse.getBody().getDescription());
        Assertions.assertEquals("owner", topicDetailsResponse.getBody().getOwner());
        Assertions.assertEquals(topicName, topicDetailsResponse.getBody().getTopicname());
        Assertions.assertEquals(HttpStatus.OK, topicDetailsResponse.getStatusCode());
        Assertions.assertFalse(topicDetailsResponse.getBody().isTxenabled());

    }

    private ResponseEntity<TopicDetails> testTopicDetails(String topicName){
        TopicDetails mockTopicDetails = new TopicDetails("owner", topicName, "description", false);
        return new ResponseEntity<TopicDetails>(mockTopicDetails, HttpStatus.OK);
    }
}
