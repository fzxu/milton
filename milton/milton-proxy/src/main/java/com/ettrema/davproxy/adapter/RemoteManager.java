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

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.ettrema.davproxy.content.FolderHtmlContentGenerator;
import com.ettrema.httpclient.File;
import com.ettrema.httpclient.Folder;
import com.ettrema.httpclient.HttpException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brad
 */
public class RemoteManager {

    private final com.bradmcevoy.http.SecurityManager securityManager;
    private final FolderHtmlContentGenerator contentGenerator;

    public RemoteManager(SecurityManager securityManager, FolderHtmlContentGenerator contentGenerator) {
        this.securityManager = securityManager;
        this.contentGenerator = contentGenerator;
    }

    public List<? extends com.bradmcevoy.http.Resource> getChildren(String hostName, com.ettrema.httpclient.Folder folder) throws IOException, HttpException, NotAuthorizedException, BadRequestException {

        List<com.bradmcevoy.http.Resource> list = new ArrayList<Resource>();
        for (com.ettrema.httpclient.Resource r : folder.children()) {
            list.add(adapt(hostName, r));
        }
        return list;
    }

    public com.bradmcevoy.http.Resource adapt(String hostName, com.ettrema.httpclient.Resource remote) {
        if (remote instanceof com.ettrema.httpclient.Folder) {
            com.ettrema.httpclient.Folder f = (com.ettrema.httpclient.Folder) remote;
            return new FolderResourceAdapter(f, securityManager, hostName, contentGenerator, this);
        } else {
            com.ettrema.httpclient.File f = (com.ettrema.httpclient.File) remote;
            return new FileResourceAdapter(f, securityManager, hostName, this);
        }
    }

    public void copyTo(com.ettrema.httpclient.Resource remoteResource, String destName, Folder destRemoteFolder) throws RuntimeException, ConflictException, BadRequestException, NotAuthorizedException {
        try {
            if (destName.equals(remoteResource.name)) { // this is the normal case, copy with no rename                
                remoteResource.copyTo(destRemoteFolder);
            } else {    // its possible to request a copy with a new name
                remoteResource.copyTo(destRemoteFolder, destName);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new BadRequestException("Remote resource does not exist", ex);
        }
    }

    public void moveTo(com.ettrema.httpclient.Resource remoteResource, String destName, Folder destRemoteFolder) throws NotAuthorizedException, ConflictException, BadRequestException {
        try {
            if (destName.equals(remoteResource.name)) { // this is the normal case, move with no rename                
                remoteResource.moveTo(destRemoteFolder);
            } else {    // its possible to request a copy with a new name
                remoteResource.moveTo(destRemoteFolder, destName);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new BadRequestException("Remote resource does not exist", ex);
        }
    }
}
