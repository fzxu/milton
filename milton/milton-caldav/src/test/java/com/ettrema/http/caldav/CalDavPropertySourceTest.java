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

package com.ettrema.http.caldav;

import java.net.URI;
import java.net.URISyntaxException;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class CalDavPropertySourceTest extends TestCase {

    public void testGetProperty() throws URISyntaxException {
        URI uri = new URI( "mailto:bernard@example.com" );
        URI[] arr = new URI[1];
        arr[0] = uri;
        if( arr instanceof URI[]) {
            System.out.println( "hiii" );
        }
    }

    public void testSetProperty() {
    }

    public void testGetPropertyMetaData() {
    }

    public void testClearProperty() {
    }

    public void testGetAllPropertyNames() {
    }
}
