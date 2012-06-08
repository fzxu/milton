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

import com.bradmcevoy.http.http11.DefaultHttp11ResponseHandler;

import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Disables locking, as required for MS office support
 *
 */
public class MsOfficeResponseHandler extends AbstractWrappingResponseHandler {

    private static final Logger log = LoggerFactory.getLogger( DefaultHttp11ResponseHandler.class );

    public MsOfficeResponseHandler( WebDavResponseHandler wrapped ) {
        super( wrapped );
    }

    public MsOfficeResponseHandler( AuthenticationService authenticationService ) {
        super( new DefaultWebDavResponseHandler( authenticationService ) );
    }

    /**
     * Overrides the default behaviour to set the status to Response.Status.SC_NOT_IMPLEMENTED
     * instead of NOT_ALLOWED, so that MS office applications are able to open
     * resources
     *
     * @param res
     * @param response
     * @param request
     */
    @Override
    public void respondMethodNotAllowed( Resource res, Response response, Request request ) {
        wrapped.respondMethodNotImplemented( res, response, request );
    }
}
