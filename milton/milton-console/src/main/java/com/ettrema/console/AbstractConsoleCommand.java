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

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.event.EventManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConsoleCommand implements ConsoleCommand{
    private static final Logger log = LoggerFactory.getLogger(AbstractConsoleCommand.class);
    
    protected final List<String> args;
    protected final Cursor cursor;
    protected final ResourceFactory resourceFactory;
    protected EventManager eventManager;

    AbstractConsoleCommand(List<String> args, String host, String currentDir, ResourceFactory resourceFactory) {
        this.args = args;
        cursor = new Cursor( resourceFactory, host, currentDir );
        this.resourceFactory = resourceFactory;
    }    
    
    /**
     * The current resource must be a collection
     *
     * @return
     */
    protected CollectionResource currentResource() throws NotAuthorizedException, BadRequestException {
        return (CollectionResource) cursor.getResource();
    }
    
    protected Result result(String msg) {
        return new Result(cursor.getPath().toString(),msg);
    }    
    
    protected Resource host() throws NotAuthorizedException, BadRequestException {
        return cursor.host();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager( EventManager eventManager ) {
        this.eventManager = eventManager;
    }
}
