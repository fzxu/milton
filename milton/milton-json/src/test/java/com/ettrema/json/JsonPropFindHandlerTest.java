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

import com.bradmcevoy.http.webdav.PropFindPropertyBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class JsonPropFindHandlerTest extends TestCase {
    
    public JsonPropFindHandlerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Test of parseField method, of class JsonPropFindHandler.
     */
    public void testParseField() {
        System.out.println("parseField");
        String field = "foo>bar";
        Set<QName> fields = new HashSet<QName>();
        Map<QName, String> aliases = new HashMap<QName, String>();
        
        JsonPropFindHandler instance = new JsonPropFindHandler((PropFindPropertyBuilder)null);
        instance.parseField(field, fields, aliases);

        QName actualQName = fields.iterator().next();
        assertEquals("foo", actualQName.getLocalPart());
        Entry<QName, String> actualAlias = aliases.entrySet().iterator().next();
        assertSame(actualQName, actualAlias.getKey());
        assertEquals("bar", actualAlias.getValue());
        
    }

}
