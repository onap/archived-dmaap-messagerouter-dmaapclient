/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 Nokia
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

public class ValidatorUtilTest {

    @Test
    public void testValidateForDME2WithNullServiceName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.DME2.getValue());
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Servicename is needed");
        }
        
    }
    
    @Test
    public void testValidateForDME2WithNullTopic() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "topic is needed");
        }
        
    }
    
    @Test
    public void testValidateForDME2WithNullUserName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        props.setProperty("topic", "topic");
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "username is needed");
        }
        
    }
    
    @Test
    public void testValidateForDME2WithNullPassword() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.DME2.getValue());
        props.setProperty("ServiceName", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "password is needed");
        }
        
    }
    
    
    
    
    @Test
    public void testValidateForNonDME2WithNullServiceName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.AUTH_KEY.getValue());
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Servicename is needed");
        }
        
    }
    
    @Test
    public void testValidateForNonDME2WithNullTopic() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "topic is needed");
        }
        
    }
    
    @Test
    public void testValidateForNonDME2WithNullContenttype() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "contenttype is needed");
        }
        
    }

    
    @Test
    public void testValidateForNonDME2WithNullUserName() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("contenttype", "contenttype");
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "username is needed");
        }
        
    }
    
    @Test
    public void testValidateForNonDME2WithNullPassword() {
        Properties props = new Properties();
        props.setProperty("TransportType", ProtocolTypeConstants.AUTH_KEY.getValue());
        props.setProperty("host", "ServiceName");
        props.setProperty("topic", "topic");
        props.setProperty("username", "username");
        props.setProperty("contenttype", "contenttype");
        
        try{
            ValidatorUtil.validatePublisher(props);
        } catch(IllegalArgumentException e) {
            assertEquals(e.getMessage(), "password is needed");
        }
        
    }
    
    
    
    
}
