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

import com.bradmcevoy.common.ContentTypeService;
import com.bradmcevoy.common.DefaultContentTypeService;
import com.bradmcevoy.http.Handler;
import com.bradmcevoy.http.HandlerHelper;
import com.bradmcevoy.http.HttpExtension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author brad
 */
public class Http11Protocol implements HttpExtension{

    private final Set<Handler> handlers;

    private final HandlerHelper handlerHelper;

    private List<CustomPostHandler> customPostHandlers;

    public Http11Protocol( Set<Handler> handlers, HandlerHelper handlerHelper ) {
        this.handlers = handlers;
        this.handlerHelper = handlerHelper;
    }

    /**
     * OPTIONS authentication is disabled by default
     *
     * @param responseHandler
     * @param handlerHelper
     */
    public Http11Protocol(Http11ResponseHandler responseHandler, HandlerHelper handlerHelper) {
        this(responseHandler, handlerHelper, null, false, null );
    }

    public Http11Protocol(Http11ResponseHandler responseHandler, HandlerHelper handlerHelper, ContentTypeService contentTypeService) {
        this(responseHandler, handlerHelper, contentTypeService, false, null );
    }
	
	/**
	 * 
	 * @param responseHandler
	 * @param handlerHelper
	 * @param contentTypeService - if null, a DefaultContentTypeService is created
	 * @param enableOptionsAuth 
	 */
    public Http11Protocol(Http11ResponseHandler responseHandler, HandlerHelper handlerHelper, ContentTypeService contentTypeService, boolean enableOptionsAuth, ETagGenerator eTagGenerator) {
        this.handlers = new HashSet<Handler>();
        this.handlerHelper = handlerHelper;
		if( contentTypeService == null ) {
			contentTypeService = new DefaultContentTypeService();
		}
		if( eTagGenerator == null ) {
			eTagGenerator = new DefaultETagGenerator();
		}
		MatchHelper matchHelper = new MatchHelper(eTagGenerator);
        handlers.add(new OptionsHandler(responseHandler, handlerHelper, enableOptionsAuth));
        handlers.add(new GetHandler(responseHandler, handlerHelper, matchHelper));
        handlers.add(new PostHandler(responseHandler, handlerHelper));
        handlers.add(new DeleteHandler(responseHandler, handlerHelper));
        handlers.add(new PutHandler(responseHandler, handlerHelper, contentTypeService, matchHelper));
    }

	@Override
    public Set<Handler> getHandlers() {
        return handlers;
    }

    public HandlerHelper getHandlerHelper() {
        return handlerHelper;
    }

	@Override
    public List<CustomPostHandler> getCustomPostHandlers() {
        return customPostHandlers;
    }
}
