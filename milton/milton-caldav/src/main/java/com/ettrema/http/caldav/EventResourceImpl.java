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

import java.util.Date;

/**
 *
 * @author brad
 */
public class EventResourceImpl implements EventResource {
    private String uniqueId;
    private Date start;
    private Date end;
    private String summary;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    
    
    public Date getStart() {
        return start;
    }

    public void setStart(Date d) {
        this.start = d;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date d) {
        this.end = d;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String s) {
        this.summary = s;
    }

}
