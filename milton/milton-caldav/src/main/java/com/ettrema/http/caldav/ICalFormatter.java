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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to parse and format the iCalendar specification
 *
 * To use this, first implement the appropriate interface (E.g. EventResource for VEVENT)
 * and then use this class to implement getICalData
 *
 * Eg
 *
 *     public String getICalData() {
ICalFormatter formatter = new ICalFormatter();
return formatter.formatEvent( this );
}
 *
 * @author brad
 */
public class ICalFormatter {

    // Changed T to space. removed trailing Z
    private static final String PATTERN_ICAL = "yyyyMMdd HHmmss";

    public void parseEvent( EventResource r, String data ) {
        String[] lines = data.split( "\n" );
        Map<String, String> mapOfPairs = new HashMap<String, String>();
        for( String line : lines ) {
            String[] arr = line.split( ":" );
            mapOfPairs.put( arr[0], arr[1] );
        }
        r.setSummary( mapOfPairs.get( "SUMMARY"));
        r.setStart( parseDate(mapOfPairs.get( "DTSTART")));
        r.setEnd( parseDate(mapOfPairs.get( "DTEND")));
    }

    public String formatEvent( EventResource r ) {
        return "BEGIN:VCALENDAR\n"
            + "VERSION:2.0\n"
            + "PRODID:-//MiltonCalDAV//EN\n"
            + "BEGIN:VEVENT\n"
            + "UID:" + r.getUniqueId() + "\n"
            + "DTSTAMP:19970714T170000Z\n"
            + "SUMMARY:" + r.getSummary() + "\n"
            + "DTSTART:" + formatDate( r.getStart() ) + "\n"
            + "DTEND:" + formatDate( r.getEnd() ) + "\n"
            + "END:VEVENT\n"
            + "END:VCALENDAR";
    }

    private Date parseDate(String s) {
        if( s == null || s.length() == 0) return null;
        s = s.replace( "T", " ");
        s = s.replace( "Z", "");
        SimpleDateFormat sdf = new SimpleDateFormat( PATTERN_ICAL);
        try {
            return sdf.parse( s );
        } catch( ParseException ex ) {
            throw new RuntimeException( s, ex );
        }
    }

    private String formatDate( Date d ) {
        if( d == null ) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTime( d );
        StringBuilder sb = new StringBuilder();
        sb.append( cal.get( Calendar.YEAR ) + "" );
        sb.append( pad2( cal.get( Calendar.MONTH ) + 1 ) );
        sb.append( pad2( cal.get( Calendar.DAY_OF_MONTH ) ) );
        sb.append( 'T' );
        sb.append( pad2( cal.get( Calendar.HOUR_OF_DAY ) ) );
        sb.append( pad2( cal.get( Calendar.MINUTE ) ) );
        sb.append( pad2( cal.get( Calendar.SECOND ) ) );
        sb.append( 'Z' );
        String s = sb.toString();
        return s;
    }

    private static String pad2( int i ) {
        if( i < 10 ) {
            return "0" + i;
        } else {
            return i + "";
        }
    }
}
