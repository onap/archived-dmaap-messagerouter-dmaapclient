/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package com.att.nsa.mr.dme.client;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.mr.client.HostSelector;
import com.att.nsa.mr.client.MRPublisher.message;
import com.att.nsa.mr.test.support.MRBatchingPublisherMock.Listener;

public class PreferredRouteReplyHandlerTest {
	private PreferredRouteReplyHandler handler = null;

	@Before
	public void setUp() throws Exception {
		handler = new PreferredRouteReplyHandler();

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testHandleReply() {

//		handler.handleReply(null);
//		assertTrue(true);

	}
	
	@Test
	public void testHandleFault() {
/*
		handler.handleFault(null);
		assertTrue(true);
*/
	}
	
	@Test
	public void testHandleEndpointFault() {

/*		handler.handleEndpointFault(null);
		assertTrue(true);*/

	}
	
	@Test
	public void testRouteWriter() {
/*
		handler.routeWriter("routeKey", "routeValue");
		assertTrue(true);
*/
	}
	
	
	

}
