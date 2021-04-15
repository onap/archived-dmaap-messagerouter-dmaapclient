/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.test.clients;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SimpleExamplePublisherWithResponseTest {

    private SimpleExamplePublisherWithResponse pub = null;

    @Before
    public void setUp() throws Exception {
        pub = new SimpleExamplePublisherWithResponse();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMain() {

        try {
            SimpleExamplePublisherWithResponse.main(new String[0]);
        } catch (Exception e) {
            assertTrue(true);
        }
        assertTrue(true);

    }

    @Test(expected = FileNotFoundException.class)
    public void testPublishMessage() throws IOException, InterruptedException {
        pub.publishMessage("/producer", 100);
    }
}
