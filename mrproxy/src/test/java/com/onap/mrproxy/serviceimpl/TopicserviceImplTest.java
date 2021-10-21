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
package com.onap.mrproxy.serviceimpl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.onap.mrproxy.controller.TopicController;
import com.onap.mrproxy.service.TopicService;
import com.onap.mrproxy.service.TopicServiceImpl;
import com.onap.mrproxy.utils.TopicDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TopicserviceImplTest {

    @InjectMocks
    TopicServiceImpl topicService;

    @Test
    void testGetTopicDetails(){
        Assertions.assertTrue(true);
        //TODO DO proper unit test. This is just a placeholder
    }

    private TopicDetails testTopicDetails(String topicName){
        return new TopicDetails("owner", topicName, "description", false);
    }
}
