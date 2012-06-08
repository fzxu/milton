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

package com.bradmcevoy.http;

import java.util.Set;

/**
 * Extension to PropFindableResource which allows custom
 * properties to be returned.
 *
 * See MultiNamespaceCustomPropertySource to support multiple namespaces
 *
 * @author brad
 */
public interface CustomPropertyResource extends PropFindableResource {

    /**
     * 
     * @return - a list of all the properties of this namespace which exist
     * on this resource
     */
    public Set<String> getAllPropertyNames();

    /**
     * Return an accessor for the given property if it is supported or known. Note
     * that this includes cases where the value of the property is null
     *
     * @param name
     * @return - null if the property is unknown or not supported. Otherwise an
     * accessor to the property
     */
    public CustomProperty getProperty(String name);

    /**
     * Returns a URI used as a namespace for these properties.
     * 
     * @return
     */
    public String getNameSpaceURI();



}
