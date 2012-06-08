/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package com.bradmcevoy.http.http11;

import com.bradmcevoy.http.HandlerHelper;
import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

/**
 *
 * @author brad
 */
public class GetHandlerTest extends TestCase {
	
	GetHandler getHandler;
	Http11ResponseHandler responseHandler;
	HandlerHelper handlerHelper;
	
	public GetHandlerTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		responseHandler = createMock(Http11ResponseHandler.class);
		handlerHelper = createMock(HandlerHelper.class);
		getHandler = new GetHandler(responseHandler, handlerHelper);
	}

	public void testProcess() throws Exception {
	}

	public void testProcessResource() throws Exception {
	}

	public void testProcessExistingResource() throws Exception {
	}


	public void testGetMethods() {
	}

	public void testIsCompatible() {
	}
}
