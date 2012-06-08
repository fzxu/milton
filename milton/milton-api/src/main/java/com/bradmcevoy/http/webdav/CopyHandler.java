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

package com.bradmcevoy.http.webdav;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.event.DeleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CopyHandler implements ExistingEntityHandler {

    private Logger log = LoggerFactory.getLogger(CopyHandler.class);
    private final WebDavResponseHandler responseHandler;
    private final HandlerHelper handlerHelper;
    private final ResourceHandlerHelper resourceHandlerHelper;
    private DeleteHelper deleteHelper;
    private UserAgentHelper userAgentHelper = new DefaultUserAgentHelper();
    private boolean deleteExistingBeforeCopy = true;

    public CopyHandler(WebDavResponseHandler responseHandler, HandlerHelper handlerHelper, ResourceHandlerHelper resourceHandlerHelper) {
        this.responseHandler = responseHandler;
        this.handlerHelper = handlerHelper;
        this.resourceHandlerHelper = resourceHandlerHelper;
        this.deleteHelper = new DeleteHelperImpl(handlerHelper);
    }

	@Override
    public String[] getMethods() {
        return new String[]{Method.COPY.code};
    }

    @Override
    public boolean isCompatible(Resource handler) {
        return (handler instanceof CopyableResource);
    }

	@Override
    public void processResource(HttpManager manager, Request request, Response response, Resource r) throws NotAuthorizedException, ConflictException, BadRequestException {
        resourceHandlerHelper.processResource(manager, request, response, r, this);
    }

	@Override
    public void process(HttpManager httpManager, Request request, Response response) throws ConflictException, NotAuthorizedException, BadRequestException {
        resourceHandlerHelper.process(httpManager, request, response, this);
    }

	
	@Override
    public void processExistingResource(HttpManager manager, Request request, Response response, Resource resource) throws NotAuthorizedException, BadRequestException, ConflictException {
        CopyableResource r = (CopyableResource) resource;
        Dest dest = Utils.getDecodedDestination(request.getDestinationHeader());
        Resource rDest = manager.getResourceFactory().getResource(dest.host, dest.url);
        log.debug("process: copying from: " + r.getName() + " -> " + dest.url + "/" + dest.name);

        if (rDest == null) {
            log.debug("process: destination parent does not exist: " + dest);
            responseHandler.respondConflict(resource, response, request, "Destination does not exist: " + dest);
        } else if (!(rDest instanceof CollectionResource)) {
            log.debug("process: destination exists but is not a collection");
            responseHandler.respondConflict(resource, response, request, "Destination exists but is not a collection: " + dest);
        } else {
            log.debug("process: copy resource to: " + rDest.getName());

            Resource fDest = manager.getResourceFactory().getResource(dest.host, dest.url + "/" + dest.name);
            if (handlerHelper.isLockedOut(request, fDest)) {
                responseHandler.respondLocked(request, response, resource);
                return;
            } else {
                boolean wasDeleted = false;
                CollectionResource colDest = (CollectionResource) rDest;
                Resource rExisting = colDest.child(dest.name);
                if (rExisting != null) {
                    if( !canOverwrite( request ) ) {
                        // Exists, and overwrite = F, disallow - http://www.webdav.org/specs/rfc4918.html#rfc.section.9.8.4
                        responseHandler.respondPreconditionFailed(request, response, resource);
                        return;
                    } else {
                        // Overwrite is absent or T, so continue
                        if (deleteHelper.isLockedOut(request, rExisting)) {
                            log.info("copy destination exists but is locked");
                            responseHandler.respondPreconditionFailed(request, response, resource);
                            return;
                        } else {
                            if (deleteExistingBeforeCopy) {
                                if (rExisting instanceof DeletableResource) {
                                    log.debug("copy destination exists and is deletable, delete it..");
                                    DeletableResource dr = (DeletableResource) rExisting;
                                    deleteHelper.delete(dr, manager.getEventManager());
                                    wasDeleted = true;
                                } else {
                                    log.warn("copy destination exists and is a collection so must be deleted, but does not implement: " + DeletableResource.class);
                                    responseHandler.respondConflict(rExisting, response, request, dest.toString());
                                    return;
                                }
                            }
                        }
                    }
                }
                r.copyTo(colDest, dest.name);

                // See http://www.ettrema.com:8080/browse/MIL-87
                if (wasDeleted) {
                    responseHandler.respondNoContent( resource, response, request );
                } else {
                    responseHandler.respondCreated(resource, response, request);
                }

            }
        }
    }

    public void setDeleteExistingBeforeCopy(boolean deleteExistingBeforeCopy) {
        this.deleteExistingBeforeCopy = deleteExistingBeforeCopy;
    }

    public boolean isDeleteExistingBeforeCopy() {
        return deleteExistingBeforeCopy;
    }

    private boolean canOverwrite( Request request ) {
        Boolean ow = request.getOverwriteHeader();
        boolean bHasOverwriteHeader = ( ow != null && request.getOverwriteHeader().booleanValue() );
        if( bHasOverwriteHeader) {
            return true;
        } else {
            String us = request.getUserAgentHeader();
            if( userAgentHelper.isMacFinder( us)) {
                log.debug( "no overwrite header, but user agent is Finder so permit overwrite");
                return true;
            } else {
                return false;
            }
        }
    }

    public UserAgentHelper getUserAgentHelper() {
        return userAgentHelper;
    }

    public void setUserAgentHelper( UserAgentHelper userAgentHelper ) {
        this.userAgentHelper = userAgentHelper;
    }

}
