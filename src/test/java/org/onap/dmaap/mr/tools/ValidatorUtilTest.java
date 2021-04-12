/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2018 IBM Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.tools;

import org.junit.Test;
import org.onap.dmaap.mr.client.ProtocolType;

import java.util.Properties;

import static org.junit.Assert.*;

public class ValidatorUtilTest {

    @Test
    public void testValidateForDME2WithNullServiceName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.DME2.getValue());
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("ServiceName is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForDME2WithNullTopic() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("topic is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForDME2WithNullUserName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        props.setProperty("topic", "topic");
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("username is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForDME2WithNullPassword() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("password is needed", e.getMessage());
        }

    }


    @Test
    public void testValidateForNonDME2WithNullServiceName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("host is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForNonDME2WithNullTopic() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("topic is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForNonDME2WithNullContenttype() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("contenttype is needed", e.getMessage());
        }

    }


    @Test
    public void testValidateForNonDME2WithNullUserName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("contenttype", "contenttype");
        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("username is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForNonDME2WithNullPassword() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("password is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForNonDME2WithAuthKey() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("authKey is needed", e.getMessage());
        }

    }

    @Test
    public void testValidateForNonDME2WithOutAuthDate() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");


        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("authDate is needed", e.getMessage());
        }
    }

    @Test
    public void testValidateForNonDME2WithAuthDate() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("maxBatchSize is needed", e.getMessage());
        }
    }


    @Test
    public void testValidateForNonDME2WithMaxAgeMs() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");
        props.setProperty("maxBatchSize", "maxBatchSize");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("maxAgeMs is needed", e.getMessage());
        }


    }

    @Test
    public void testValidateForNonDME2WithMessageSentThreadOccurrence() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");
        props.setProperty("maxBatchSize", "maxBatchSize");
        props.setProperty("maxAgeMs", "maxAgeMs");

        try {
            ValidatorUtil.validatePublisher(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("MessageSentThreadOccurrence is needed", e.getMessage());
        }

    }


    @Test
    public void testValidateSubscriberWithoutGroup() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");
        props.setProperty("maxBatchSize", "maxBatchSize");
        props.setProperty("maxAgeMs", "maxAgeMs");

        try {
            ValidatorUtil.validateSubscriber(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("group is needed", e.getMessage());
        }
    }

    @Test
    public void testValidateSubscriberWithoutCustomer() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");
        props.setProperty("maxBatchSize", "maxBatchSize");
        props.setProperty("maxAgeMs", "maxAgeMs");
        props.setProperty("group", "group");

        try {
            ValidatorUtil.validateSubscriber(props);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Consumer (id) is needed", e.getMessage());
        }
    }

    @Test
    public void testValidatePublisher() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolType.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        props.setProperty("password", "password");
        props.setProperty("authKey", "authKey");
        props.setProperty("authDate", "authDate");
        props.setProperty("maxBatchSize", "maxBatchSize");
        props.setProperty("maxAgeMs", "maxAgeMs");
        props.setProperty("MessageSentThreadOccurrence", "10");

        try {
            ValidatorUtil.validatePublisher(props);
        } catch (IllegalArgumentException e) {
            fail();
            return;
        }

        props.remove("MessageSentThreadOccurrence");
        props.setProperty("MessageSentThreadOccurance", "10");
        try {
            ValidatorUtil.validatePublisher(props);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }



}
