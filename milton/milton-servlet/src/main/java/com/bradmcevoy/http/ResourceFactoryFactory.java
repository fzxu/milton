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

import com.bradmcevoy.http.webdav.WebDavResponseHandler;

/**
 *  Implement this interface to configure the ResourceFactory instance
 * 
 * To use your implementation, specify its class name in: resource.factory.factory.class
 * as an init parameter on the servlet or filter in web.xml
 * 
 * Example:
 * <PRE>
 * {@code
 * <servlet>
 *   <servlet-name>milton</servlet-name>
 *   <servlet-class>com.bradmcevoy.http.MiltonServlet</servlet-class>
 *     <init-param>
 *       <param-name>resource.factory.factory.class</param-name>
 *       <param-value>com.bradmcevoy.http.SpringResourceFactoryFactory</param-value>
 *     </init-param>
 * </servlet>
 * }
 * </PRE>
 *
 * 
 */
public interface ResourceFactoryFactory {

    /**
     * Create and return a ResponseHandler. Normally this will be DefaultResponseHandler
     *
     * @return
     */
    public WebDavResponseHandler createResponseHandler();

    /**
     * Called immediately after construction
     */
    void init();
    
    /**
     * Create and return a ResourceFactory instance. This single instance
     * will usually be used for the lifetime of the servlet
     * 
     * @return 
     */
    ResourceFactory createResourceFactory();
}
