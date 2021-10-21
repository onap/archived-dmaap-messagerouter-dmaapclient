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

import com.onap.mrproxy.utils.TopicBean;
import com.onap.mrproxy.utils.TopicDetails;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;

public interface TopicService {

    ResponseEntity getTopicDetails(String topicName);

    ResponseEntity createTopic(TopicBean topicBean);

    ResponseEntity getListOfTopics();
}
