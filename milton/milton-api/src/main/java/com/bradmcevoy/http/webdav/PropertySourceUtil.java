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

import com.bradmcevoy.property.BeanPropertySource;
import com.bradmcevoy.property.CustomPropertySource;
import com.bradmcevoy.property.MultiNamespaceCustomPropertySource;
import com.bradmcevoy.property.PropertySource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brad
 */
public class PropertySourceUtil {
    /**
     * Create default extension property sources. These are those additional
     * to the webdav default properties defined on the protocol itself
     *
     * @param resourceTypeHelper
     * @return
     */
    public static List<PropertySource> createDefaultSources(ResourceTypeHelper resourceTypeHelper) {
        List<PropertySource> list = new ArrayList<PropertySource>();
        CustomPropertySource customPropertySource = new CustomPropertySource();
        list.add( customPropertySource );
        MultiNamespaceCustomPropertySource mncps = new MultiNamespaceCustomPropertySource();
        list.add( mncps );
        BeanPropertySource beanPropertySource = new BeanPropertySource();
        list.add( beanPropertySource);
        return list;
    }
}
