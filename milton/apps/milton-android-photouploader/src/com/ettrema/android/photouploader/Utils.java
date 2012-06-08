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

package com.ettrema.android.photouploader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class with support functions
 * 
 * @author Hooiveld
 */
public final class Utils {

    public static void textDialog( Context context, String title, String text ) {
        new AlertDialog.Builder( context ).setTitle( title ).setMessage( text ).setNeutralButton( "OK",
            new DialogInterface.OnClickListener() {

                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    dialog.cancel();
                }
            } ).show();
    }

    public static byte[] toByteArray( File file ) throws IOException {
        InputStream is = new FileInputStream( file );
        byte[] bytes = new byte[(int) file.length()];
        int offset = 0;
        int numRead = 0;

        // reaf file into byte array
        while( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 ) {
            offset += numRead;
        }

        // errror checking
        if( offset < bytes.length ) {
            throw new IOException( "Could not completely read file " + file.getName() );
        }

        // close input stream
        is.close();

        return bytes;

    }
}
