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
import com.bradmcevoy.http.webdav.ResourceTypeHelper;
import com.ettrema.http.AddressBookResource;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class AddressBookResourceTypeHelper implements ResourceTypeHelper {

    private static final Logger log = LoggerFactory.getLogger(AddressBookResourceTypeHelper.class);
    private final ResourceTypeHelper wrapped;

    public AddressBookResourceTypeHelper(ResourceTypeHelper wrapped) {
        log.debug("CalendarResourceTypeHelper constructed :" + wrapped.getClass().getSimpleName());
        this.wrapped = wrapped;
    }

    @Override
    public List<QName> getResourceTypes(Resource r) {
        if (log.isTraceEnabled()) {
            log.trace("getResourceTypes:" + r.getClass().getCanonicalName());
        }
        QName qn;
        List<QName> list = wrapped.getResourceTypes(r);

        if (r instanceof AddressBookResource) {
            log.trace("getResourceTypes: is a calendar");
            qn = new QName(CardDavProtocol.CARDDAV_NS, "addressbook");
            if (list == null) {
                list = new ArrayList<QName>();
            }
            list.add(qn);
        }
        return list;
    }

    /**
     *
     *
     * @param r
     * @return
     */
    @Override
    public List<String> getSupportedLevels(Resource r) {
        log.debug("getSupportedLevels");
        List<String> list = wrapped.getSupportedLevels(r);
        if (r instanceof AddressBookResource) {
            list.add("addressbook");
        }
        return list;
    }
}
