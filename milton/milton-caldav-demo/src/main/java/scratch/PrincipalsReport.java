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

import com.ettrema.httpclient.PropFindMethod;
import com.ettrema.httpclient.ReportMethod;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author brad
 */
public class PrincipalsReport {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        reportPrincipals( client );
        propfindCalendar( client);


    }

    private static void propfindCalendar( HttpClient client ) throws UnsupportedEncodingException, IOException {
        PropFindMethod m = new PropFindMethod( "http://localhost:9080/calendarHome/calendarOne" );
        m.setRequestHeader( "depth", "0" );
        m.setRequestEntity( new StringRequestEntity( "<x0:principal-search-property-set xmlns:x0=\"DAV:\"/>", "text/xml", "UTF-8" ) );
        int result = client.executeMethod( m );
        System.out.println( "result: " + result );
        String resp = m.getResponseBodyAsString();
        System.out.println( "response--" );
        System.out.println( resp );
    }

    private static void reportPrincipals( HttpClient client ) throws UnsupportedEncodingException, IOException {
        ReportMethod rm = new ReportMethod( "http://localhost:9080/principals" );
        rm.setRequestHeader( "depth", "0" );
        rm.setRequestEntity( new StringRequestEntity( "<x0:principal-search-property-set xmlns:x0=\"DAV:\"/>", "text/xml", "UTF-8" ) );
        int result = client.executeMethod( rm );
        System.out.println( "result: " + result );
        String resp = rm.getResponseBodyAsString();
        System.out.println( "response--" );
        System.out.println( resp );
 
    }

}
