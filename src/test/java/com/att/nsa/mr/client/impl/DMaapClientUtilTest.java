/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2018 IBM Intellectual Property. All rights reserved.
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
package com.att.nsa.mr.client.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.WebTarget;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DMaapClientUtilTest {
    
    @Before
    public void setup(){
        Mockito.mock(HttpServletRequest.class);
    }
    
    @Test
    public void testGetTarget() throws IOException{
        WebTarget actual = DmaapClientUtil.getTarget("testpath");
        
        assertEquals("testpath", actual.getUri().getPath());
    }
    
    @Test
    public void testGetTargetWithParams() throws IOException{
        WebTarget actual = DmaapClientUtil.getTarget("testpath", "testuser", "testpassword");
        
        assertEquals("testpath", actual.getUri().getPath());
    }

    

}
