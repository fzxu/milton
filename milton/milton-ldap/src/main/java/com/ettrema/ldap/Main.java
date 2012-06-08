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

package com.ettrema.ldap;

import com.bradmcevoy.http.HandlerHelper;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.bradmcevoy.http.webdav.WebDavProtocol;
import com.bradmcevoy.property.BeanPropertySource;
import com.bradmcevoy.property.PropertySource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brad
 */
public class Main {
	public static void main(String[] args) throws Exception {
		System.out.println("Starting milton ldap...");
		MemoryUserSessionFactory factory = new MemoryUserSessionFactory();
		List<PropertySource> propertySources = new ArrayList<PropertySource>();
		BeanPropertySource ps = new BeanPropertySource();
		System.out.println("Using bean property source: " + ps);
		propertySources.add( ps);
		propertySources.add( new WebDavProtocol(new DefaultWebDavResponseHandler(null), new HandlerHelper(null)));
		
		// TODO: add property sources
		
		factory.addUser("userA", "password", "joe", "bloggs", "joeblogss@blogs.com");
		factory.addUser("userB", "password", "joe2", "bloggs2", "joeblogss2@blogs.com");
		factory.addUser("userC", "password", "joe3", "bloggs3", "joeblogss3@blogs.com");
                NullLdapTransactionManager transactionManager = new NullLdapTransactionManager();
		LdapServer ldapServer = new LdapServer(transactionManager, factory, propertySources, 8389, true, "localhost");				
		ldapServer.start();
		System.out.println("Started");
		while(true) {
			Thread.sleep(5000);
			System.out.println("still running...");
		}
	}
			
}
