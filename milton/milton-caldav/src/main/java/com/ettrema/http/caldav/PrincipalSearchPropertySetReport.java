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

package com.ettrema.http.caldav;

import com.bradmcevoy.http.Resource;
import com.ettrema.http.report.Report;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
public class PrincipalSearchPropertySetReport implements Report {

	private static final Logger log = LoggerFactory.getLogger(PrincipalSearchPropertySetReport.class);

	@Override
	public String getName() {
		return "principal-search-property-set";
	}

	@Override
	public String process(String host, String path, Resource r, Document doc) {
		System.out.println("XXXXXXXXXXXXXXX NOT IMPLEMENTED XXXXXXXXXXXXXXXXXXXXXXx");
		log.debug("process");
		return "\n<?xml version='1.0' encoding='UTF-8'?>\n"
				+ "<principal-search-property-set xmlns='DAV:'>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<displayname/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>Display Name</description>\n"
				+ "</principal-search-property>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<email-address-set xmlns='http://calendarserver.org/ns/'/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>Email Addresses</description>\n"
				+ "</principal-search-property>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<last-name xmlns='http://calendarserver.org/ns/'/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>Last Name</description>\n"
				+ "</principal-search-property>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<calendar-user-type xmlns='urn:ietf:params:xml:ns:caldav'/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>Calendar User Type</description>\n"
				+ "</principal-search-property>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<first-name xmlns='http://calendarserver.org/ns/'/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>First Name</description>\n"
				+ "</principal-search-property>\n"
				+ "<principal-search-property>\n"
				+ "<prop>\n"
				+ "<calendar-user-address-set xmlns='urn:ietf:params:xml:ns:caldav'/>\n"
				+ "</prop>\n"
				+ "<description xml:lang='en'>Calendar User Address Set</description>\n"
				+ "</principal-search-property>\n"
				+ "</principal-search-property-set>";
	}
}
