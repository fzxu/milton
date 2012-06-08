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

import java.util.List;

/**
 * This provides a data structure which is a tree of long values. This
 * can be used to represent the "fanout" structure described in the Bup DESIGN
 * page
 * 
 * https://github.com/apenwarr/bup/blob/master/DESIGN
 *
 * @author brad
 */
public class HashNode {
    private long hashValue;
    private List<HashNode> childNodes;

    public List<HashNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<HashNode> childNodes) {
        this.childNodes = childNodes;
    }

    public long getHashValue() {
        return hashValue;
    }

    public void setHashValue(long hashValue) {
        this.hashValue = hashValue;
    }
    
    
    
}
