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

package scratch;

import com.ettrema.httpclient.ReportMethod;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author brad
 */
public class CalendarQueryReportScratch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        calendarQueryReport( client);


    }

    private static void calendarQueryReport( HttpClient client ) throws UnsupportedEncodingException, IOException {

        String s = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
        s += "<x0:calendar-query xmlns:x1=\"DAV:\" xmlns:x0=\"urn:ietf:params:xml:ns:caldav\">";
        s += "<x1:prop>";
        s += "<x1:getetag/><x1:resourcetype/></x1:prop>";
        s += "<x0:filter><x0:comp-filter name=\"VCALENDAR\"><x0:comp-filter name=\"VEVENT\">";
        s += "<x0:time-range start=\"20101109T230000Z\"/></x0:comp-filter></x0:comp-filter></x0:filter>";
        s += "</x0:calendar-query>";

        System.out.println("calendarQueryReport: " + s);

        ReportMethod m = new ReportMethod( "http://localhost:8080/users/userA/calendars/cal1/" );
        m.setRequestHeader( "content-type","text/xml");
        m.setRequestHeader( "connection","close");
        m.setRequestHeader( "content-length",s.length()+"");
        m.setRequestHeader( "depth","1");

        m.setRequestEntity( new StringRequestEntity( s, "text/xml", "UTF-8" ) );
        System.out.println("execute method...");
        int result = client.executeMethod( m );
        System.out.println( "result: " + result );
        String resp = m.getResponseBodyAsString();
        System.out.println( "response--" );
        System.out.println( resp );
    }

}
