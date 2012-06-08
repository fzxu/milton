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

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Rm extends AbstractConsoleCommand {

    private static final Logger log = LoggerFactory.getLogger(Rm.class);

    public Rm( List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory ) {
        super( args, host, currentDir, resourceFactory );
    }

    @Override
    public Result execute() {
        try {
            String sPath = args.get( 0 );
            Path path = Path.path( sPath );

            Cursor sourceCursor = cursor.find( path );

            if( !sourceCursor.exists() ) {
                // try regex
                List<Resource> list = sourceCursor.getParent().childrenWithFilter( sourceCursor.getPath().getName() );
                if( list != null ) {
                    return doDelete( list );
                } else {
                    return result( "Not found: " + path );
                }
            } else {
                return doDelete( sourceCursor.getResource() );
            }
        } catch (NotAuthorizedException ex) {
            log.error("not authorised", ex);
            return result(ex.getLocalizedMessage());
        } catch (BadRequestException ex) {
            log.error("bad req", ex);
            return result(ex.getLocalizedMessage());
        }
    }

    private Result delete( List<DeletableResource> deletables ) {
        try {
            StringBuilder sb = new StringBuilder( "deleted: " );
            for( DeletableResource dr : deletables ) {
                sb.append( dr.getName() ).append( ',' );
                dr.delete();
            }
            return result( sb.toString() );
        } catch( NotAuthorizedException e ) {
            return result( "not authorised to delete: " + e.getResource().getName());
        } catch( ConflictException e ) {
            return result( "conflict error deleting: " + e.getResource().getName());
        } catch( BadRequestException e ) {
            return result( "bad request error deleting: " + e.getResource().getName());
        }
    }

    private Result doDelete( List<Resource> list ) {
        List<DeletableResource> deletables = new ArrayList<DeletableResource>();
        for( Resource r : list ) {
            if( r instanceof DeletableResource ) {
                deletables.add( (DeletableResource) r );
            } else {
                return result( "Can't delete: " + r.getName() );
            }
        }
        if( deletables.size() > 0 ) {
            return delete( deletables );
        } else {
            return result( "No files found to delete" );
        }
    }

    private Result doDelete( Resource r ) {
        if( r instanceof DeletableResource ) {
            try {
                DeletableResource dr = (DeletableResource) r;
                dr.delete();
                return result( "deleted: " + r.getName() );
            } catch( NotAuthorizedException e ) {
                return result( "not authorised to delete: " + e.getResource().getName());
            } catch( ConflictException e ) {
                return result( "conflict deleting: " + e.getResource().getName());
            } catch( BadRequestException e ) {
                return result( "bad request deleting: " + e.getResource().getName());
            }
        } else {
            return result( "Can't delete: " + r.getName() );
        }

    }
}
