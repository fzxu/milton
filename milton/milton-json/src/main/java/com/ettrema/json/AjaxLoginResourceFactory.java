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

package com.ettrema.json;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class AjaxLoginResourceFactory implements ResourceFactory{

    private static final Logger log = LoggerFactory.getLogger( AjaxLoginResourceFactory.class );

    private String suffix = ".login";
    private final ResourceFactory wrapped;

    public AjaxLoginResourceFactory( ResourceFactory wrapped ) {
        this.wrapped = wrapped;
    }    
    
    public AjaxLoginResourceFactory( String suffix, ResourceFactory wrapped ) {
        this.suffix = suffix;
        this.wrapped = wrapped;
    }

    @Override
    public Resource getResource( String host, String path ) throws NotAuthorizedException, BadRequestException {
        if(path.endsWith( suffix )) {
            int i = path.lastIndexOf( suffix);
            String p2 = path.substring( 0, i);
            Resource r = wrapped.getResource( host, p2);
            if( r != null) {
                if( r instanceof GetableResource) {
                    GetableResource gr = (GetableResource) r;
                    Path pathFull = Path.path( path );
                    log.debug( "found an ajax resource, wrapping a: " + gr.getClass());
                    return new AjaxLoginResource( pathFull.getName(), gr );
                } else {
                    return r;
                }
            }
        }
        return wrapped.getResource( host, path );
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    
}
