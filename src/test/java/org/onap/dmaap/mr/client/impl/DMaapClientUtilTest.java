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
package org.onap.dmaap.mr.client.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DMaapClientUtilTest {
    
	Builder builder;
	
	Response response;
	
	WebTarget target;
	
    @Before
    public void setup(){
        Mockito.mock(HttpServletRequest.class);
        builder = Mockito.mock(Invocation.Builder.class);
        response = Mockito.mock(Response.class);
        target = Mockito.mock(WebTarget.class);
        
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
    
    @Test
    public void testGetResponsewtCambriaAuth() {
    	Mockito.when(target.request()).thenReturn(builder);
    	Mockito.when(builder.header("X-CambriaAuth", "testuser")).thenReturn(builder);
    	Mockito.when(builder.header("X-CambriaDate", "testpassword")).thenReturn(builder);
    	Mockito.when(builder.get()).thenReturn(response);
    	
        Response actual = DmaapClientUtil.getResponsewtCambriaAuth(target, "testuser", "testpassword");
        
        assertEquals(response, actual);
    }

    

}
