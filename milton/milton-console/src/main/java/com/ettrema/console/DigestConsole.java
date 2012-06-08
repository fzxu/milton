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

package com.ettrema.console;

import com.bradmcevoy.http.DigestResource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author brad
 */
public class DigestConsole extends Console implements DigestResource{

    final DigestResource digestResource;

    public DigestConsole(String host, final ResourceFactory wrappedFactory, String name, DigestResource secureResource, Date modDate, Map<String,ConsoleCommandFactory> mapOfFactories) {
        super(host, wrappedFactory, name, secureResource, modDate, mapOfFactories );
        this.digestResource = secureResource;
    }

    public Object authenticate( DigestResponse digestRequest ) {
        return digestResource.authenticate( digestRequest );
    }

    public boolean isDigestAllowed() {
        return digestResource.isDigestAllowed();
    }



}
