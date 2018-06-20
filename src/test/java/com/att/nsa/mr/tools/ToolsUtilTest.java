/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 Nokia
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

/**
 * @author marcin.migdal@nokia.com
 */
package com.att.nsa.mr.tools;

import com.att.nsa.mr.client.MRBatchingPublisher;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ToolsUtilTest {

    private final List<String> clusters = new ArrayList<>();
    private final String cluster1 = "Cluster1", cluster2 = "Cluster2";
    private final String apiKey = "apiKey";
    private final String apiPassword = "apiPassword";
    private final String topicName = "topicName";

    @Before
    public void setUp() {
        clusters.add(cluster1);
        clusters.add(cluster2);
    }

    @Test
    public void createBatchPublisher() {
        MRCommandContext mrCommandContext = Mockito.mock(MRCommandContext.class);
        Mockito.when(mrCommandContext.getCluster()).thenReturn(clusters);
        Mockito.when(mrCommandContext.getApiKey()).thenReturn(apiKey);
        Mockito.when(mrCommandContext.getApiPwd()).thenReturn(apiPassword);

        MRBatchingPublisher mrBatchingPublisher = ToolsUtil.createBatchPublisher(mrCommandContext, topicName);

        Assert.assertNotNull(mrBatchingPublisher);
    }
}
