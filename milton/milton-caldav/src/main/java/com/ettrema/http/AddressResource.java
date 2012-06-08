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

package com.ettrema.http;

import com.bradmcevoy.http.Resource;
/**
 * Represents an address resource. 
 * 
 * Example(1):
 * <C:address-data>.......................</C:address-data>
 * 
 * 
 * Example(2): (Not Supported yet)
 * <C:address-data>
 *   <C:prop name="VERSION"/>
 *   <C:prop name="UID"/>
 *   <C:prop name="NICKNAME"/>
 *   <C:prop name="EMAIL"/>
 *   <C:prop name="FN"/>
 * </C:address-data>
 * 
 * @author nabil.shams
 */
public interface AddressResource extends Resource{
    String getAddressData();
}
