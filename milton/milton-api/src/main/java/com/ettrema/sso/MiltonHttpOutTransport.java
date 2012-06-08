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

import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Response;
import java.io.OutputStream;
import java.util.List;
//import org.opensaml.ws.transport.http.HTTPOutTransport;
//import org.opensaml.xml.security.credential.Credential;

/**
 *
 * @author brad
 */
public class MiltonHttpOutTransport { // implements HTTPOutTransport {

	private final Request request;
	
	private final Response response;

	public MiltonHttpOutTransport(Request request, Response response) {
		this.request = request;
		this.response = response;
	}
	

//	
//	@Override
//	public void setVersion(HTTP_VERSION h) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setHeader(String name, String value) {
//		response.setNonStandardHeader(name, value);
//	}
//
//	@Override
//	public void addParameter(String string, String string1) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setStatusCode(int i) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void sendRedirect(String url) {
//		response.sendRedirect(url);
//	}
//
//	@Override
//	public void setAttribute(String string, Object o) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setCharacterEncoding(String charset) {
//		
//	}
//
//	@Override
//	public OutputStream getOutgoingStream() {
//		return response.getOutputStream();
//	}
//
//	@Override
//	public Object getAttribute(String string) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public String getCharacterEncoding() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public Credential getLocalCredential() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public Credential getPeerCredential() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public boolean isAuthenticated() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setAuthenticated(boolean bln) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public boolean isConfidential() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setConfidential(boolean bln) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public boolean isIntegrityProtected() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public void setIntegrityProtected(boolean bln) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public String getHeaderValue(String string) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public String getHTTPMethod() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public int getStatusCode() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public String getParameterValue(String string) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public List<String> getParameterValues(String string) {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public HTTP_VERSION getVersion() {
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//	
}
