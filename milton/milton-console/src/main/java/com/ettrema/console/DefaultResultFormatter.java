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

package com.ettrema.console;

import com.bradmcevoy.http.Resource;
import java.util.List;

public class DefaultResultFormatter implements ResultFormatter {

    public String format( String href, Resource r1 ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "<a href=\'" ).append( href ).append( "\'>" ).append( r1.getName() ).append( "</a>" ).append( "<br/>" );
        return sb.toString();
    }

    public String begin( List<? extends Resource> list ) {
        return "";
    }

    public String end() {
        return "";
    }
}
