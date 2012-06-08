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

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to be used with the SsoAuthenticationHandler to provide
 * path based authentication.
 * 
 * For example, if a user logs in via a web page and there session id is ABC123,
 * they might be able to edit documents in MS Word without being challenged for
 * a login on a path like /ABC123/mydoc.doc
 * 
 * To use this class, you should use it to wrap your existing resource factory. Eg
 * 
 *  MyResourceFactory rf = new MyResourceFactory();
 *  SsoResourceFactory ssoRf = new SsoResourceFactory(rf);
 * 
 * And then connect ssoRf to the HttpManager like this:
 * 
 *   HttpManager hm = new HttpManager(ssoRf);
 * 
 * URL's which do not relate to SSO will be delegated to the wrapped resource factory.
 * 
 * When a SSO request is received, this resource factory will use the adapted path
 * to locate a resource with the wrapped resource factory.
 *
 * @author brad
 */
public class SsoResourceFactory implements ResourceFactory {

	private static final Logger log = LoggerFactory.getLogger(SsoResourceFactory.class);
	

	private final ResourceFactory resourceFactory;
	
	private final SsoSessionProvider ssoSessionProvider;

	public SsoResourceFactory(ResourceFactory resourceFactory, SsoSessionProvider ssoSessionProvider) {
		this.resourceFactory = resourceFactory;
		this.ssoSessionProvider = ssoSessionProvider;
	}


	@Override
	public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {
		Path p = Path.path(url);
		String firstComp = p.getFirst();
		Object oUserTag = null;
		if( firstComp != null ) {
			oUserTag = ssoSessionProvider.getUserTag(firstComp);
		}
		
		if (oUserTag == null) {
			log.trace("not a SSO path");
			return resourceFactory.getResource(host, url);
		} else {
			log.trace("is an SSO path");
			Path strippedPath = p.getStripFirst();
			HttpManager.request().getAttributes().put("_sso_user", oUserTag);
			return resourceFactory.getResource(host, strippedPath.toString());
		}
	}

	
}
