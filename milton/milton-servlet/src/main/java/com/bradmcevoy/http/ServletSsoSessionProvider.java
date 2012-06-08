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

package com.bradmcevoy.http;

import com.ettrema.sso.SsoSessionProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * This SsoSessionProvider works by keeping a track of active sessions, and looking
 * up sessions by the session id, which forms the leading path of a SSO path.
 * 
 * Eg:
 * 
 * /ABC123/MyDocuments/adoc.doc
 * 
 * Note that to be secure this should be used over SSL
 *
 * @author brad
 */
public class ServletSsoSessionProvider implements SsoSessionProvider, HttpSessionListener {

	/**
	 * Note, one shared map across all instances of ServletSsoSessionProvider!
	 * 
	 */
	private static final Map<String,HttpSession> mapOfSessions = new ConcurrentHashMap<String, HttpSession>();
	
	private String userSessionVariableName = "user";
	
	
	
	
	public Object getUserTag(String firstComp) {
		HttpSession sess = mapOfSessions.get(firstComp);
		if( sess == null ) {
			return null;
		} else {
			Object oUser = sess.getAttribute(userSessionVariableName);
			return oUser;
		}
	}

	public void sessionCreated(HttpSessionEvent hse) {
		String id = hse.getSession().getId();
		mapOfSessions.put(id, hse.getSession());
	}

	public void sessionDestroyed(HttpSessionEvent hse) {
		String id = hse.getSession().getId();
		mapOfSessions.remove(id);
	}

	public String getUserSessionVariableName() {
		return userSessionVariableName;
	}

	public void setUserSessionVariableName(String userSessionVariableName) {
		this.userSessionVariableName = userSessionVariableName;
	}
		
	
}
