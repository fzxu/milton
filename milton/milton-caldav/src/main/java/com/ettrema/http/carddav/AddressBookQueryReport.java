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

package com.ettrema.http.carddav;

import com.bradmcevoy.http.Resource;
import com.ettrema.http.report.Report;
import org.jdom.Document;

/**
 * The CARDDAV:addressbook-query REPORT performs a search for all address object 
 * resources that match a specified filter. The response of this report will 
 * contain all the WebDAV properties and address object resource data specified 
 * in the request. In the case of the CARDDAV:address-data XML element, one can 
 * explicitly specify the vCard properties that should be returned in the address 
 * object resource data that matches the filter.
 * 
 * The format of this report is modeled on the PROPFIND method. The request and 
 * response bodies of the CARDDAV:addressbook-query report
 * use XML elements that are also used by PROPFIND. In particular, the
 * request can include XML elements to request WebDAV properties to be
 * returned. When that occurs, the response should follow the same
 * behavior as PROPFIND with respect to the DAV:multistatus response
 * elements used to return specific WebDAV property results. For
 * instance, a request to retrieve the value of a WebDAV property that
 * does not exist is an error and MUST be noted with a response XML
 * element that contains a 404 (Not Found) status value.
 * 
 * @author nabil.shams
 */
public class AddressBookQueryReport implements Report {

    @Override
    public String getName() {
        return "addressbook-query";
    }

    @Override
    public String process(String host, String path, Resource r, Document doc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
