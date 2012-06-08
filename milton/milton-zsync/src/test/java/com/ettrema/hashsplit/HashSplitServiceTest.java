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

package com.ettrema.hashsplit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author brad
 */
public class HashSplitServiceTest {

    HashSplitService service;
    
    //@Test
    public void testClientUploads() throws IOException {
        // Get the test data
        InputStream inOrig = this.getClass().getResourceAsStream("/hashsplit-original.txt");
        assertNotNull(inOrig);
        InputStream inMod = this.getClass().getResourceAsStream("/hashsplit-modified.txt");
        assertNotNull(inMod);
        
        // Parse the original
        List<HashNode> rootNodes = service.parse(inOrig);
        LocalHashNodeProvider hashNodeProvider = new LocalHashNodeProvider(rootNodes);
        HashSplitDeltaGenerator deltaGenerator = new HashSplitDeltaGenerator(hashNodeProvider);
        File dest = File.createTempFile("hashsplit-test", null);
        deltaGenerator.generateDeltas(inMod, dest);
        
        // so we should now have changed blocks in the "dest" file
        
    }

    @Before
    public void setUp() {
        service = new HashSplitService();
    }
    
    @After
    public void tearDown() {
    }

}
