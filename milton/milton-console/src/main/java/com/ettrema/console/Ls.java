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
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ls extends AbstractConsoleCommand{

    private static final Logger log = LoggerFactory.getLogger(Ls.class);

    private final ResultFormatter resultFormatter;

    Ls(List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory, ResultFormatter resultFormatter) {
        super(args, host, currentDir, resourceFactory);
        this.resultFormatter = resultFormatter;
    }

    
    
    @Override
    public Result execute() {
        try {
            Resource cur = currentResource();
            if( cur == null ) {
                return result("current dir not found: " + cursor.getPath().toString());
            }
            CollectionResource target;
            Cursor newCursor;
            if( args.size() > 0 ) {
                String dir = args.get(0);
                log.debug( "dir: " + dir);
                newCursor = cursor.find( dir );

                if( !newCursor.exists() ) {
                    return result("not found: " + dir);
                } else if( !newCursor.isFolder() ) {
                    return result("not a folder: " + dir);
                }
                target = (CollectionResource) newCursor.getResource();
            } else {
                newCursor = cursor;
                target = currentResource();
            }
            StringBuilder sb = new StringBuilder();
            List<? extends Resource> children = target.getChildren();
            sb.append( resultFormatter.begin( children));
            for( Resource r1 : target.getChildren() ) {
                String href = newCursor.getPath().child(r1.getName()).toString();
                sb.append(resultFormatter.format( href, r1 ));
            }
            sb.append( resultFormatter.end());
            return result(sb.toString());
        } catch (NotAuthorizedException ex) {
            log.error("not authorised", ex);
            return result(ex.getLocalizedMessage());
        } catch (BadRequestException ex) {
            log.error("bad req", ex);
            return result(ex.getLocalizedMessage());
        }
    }
}
