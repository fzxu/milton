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

package com.ettrema.davproxy.adapter;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.common.LogUtils;
import com.ettrema.davproxy.content.FolderHtmlContentGenerator;
import com.ettrema.httpclient.Host;
import com.ettrema.httpclient.HostBuilder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author brad
 */
public class RemoteDavResourceFactory implements ResourceFactory {

    private static final Logger log = LoggerFactory.getLogger(RemoteDavResourceFactory.class);
    private final com.bradmcevoy.http.SecurityManager securityManager;
    private final FolderHtmlContentGenerator contentGenerator;
    private final RemoteManager remoteManager;
    private final Map<String,Host> roots;

    public RemoteDavResourceFactory(com.bradmcevoy.http.SecurityManager securityManager, FolderHtmlContentGenerator contentGenerator,RemoteManager remoteManager, Map<String,HostBuilder> roots) {
        this.securityManager = securityManager;
        this.contentGenerator = contentGenerator;
        this.remoteManager = remoteManager;
        this.roots = new ConcurrentHashMap();
        for( Entry<String, HostBuilder> entry : roots.entrySet()) {
            this.roots.put(entry.getKey(), entry.getValue().buildHost());
        }
    }

    @Override
    public Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException {
        LogUtils.trace(log, "getResource: path:", path);
        Path p = Path.path(path);
        return find(host, p);
    }

    /**
     * Recursive method which walks the parts of the path resolving it to a
     * Resource by using the child method on CollectionResource
     *
     * @param p
     * @return
     */
    private Resource find(String host, Path p) throws NotAuthorizedException, BadRequestException {
        if (p.isRoot()) {
            return new RootFolder(host, roots, this, contentGenerator, securityManager, remoteManager);
        } else {
            Resource rParent = find(host, p.getParent());
            if (rParent == null) {
                return null;
            } else {
                if (rParent instanceof CollectionResource) {
                    CollectionResource parent = (CollectionResource) rParent;
                    return parent.child(p.getName());
                } else {
                    return null;
                }
            }
        }
    }
           
}
