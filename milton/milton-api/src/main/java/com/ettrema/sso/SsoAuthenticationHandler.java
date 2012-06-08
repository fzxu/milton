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

package com.ettrema.sso;

import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;

/**
 * This is a post resource-resolution authentication handler. 
 * 
 * It assumes that the SsoResourceFactory has populated the _sso_user
 * request attribute if appropriate
 *
 * @author brad
 */
public class SsoAuthenticationHandler implements AuthenticationHandler {


	
	public boolean supports(Resource r, Request request) {
		boolean b = request.getAttributes().get("_sso_user") != null;		
		return b;
	}

	public Object authenticate(Resource resource, Request request) {
		return request.getAttributes().get("_sso_user");
	}

	public String getChallenge(Resource resource, Request request) {
		return null;
	}

	public boolean isCompatible(Resource resource) {
		return true;
	}	
}
