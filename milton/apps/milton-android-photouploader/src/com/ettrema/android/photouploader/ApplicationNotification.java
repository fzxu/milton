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

/**
 * This file is part of Picasa Photo Uploader.
 *
 * Picasa Photo Uploader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Picasa Photo Uploader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Picasa Photo Uploader. If not, see <http://www.gnu.org/licenses/>.
 */
package com.ettrema.android.photouploader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 *
 * @author Jan Peter Hooiveld
 */
public class ApplicationNotification {

    /**
     * Singleton that refers to this class
     */
    private static ApplicationNotification instance;
    /**
     * Manager that deald with notifications
     */
    private NotificationManager manager;
    /**
     * If spplication notificattion is enabled
     */
    private boolean isEnabled = false;
    /**
     * Unique id for application notification
     */
    private static final int APP_NOTIFICAION = 999999999;

    /**
     * Constructor
     */
    private ApplicationNotification() {
    }

    /**
     * Singleton function
     *
     * @return Instance of this class
     */
    public static ApplicationNotification getInstance() {
        if( instance == null ) {
            instance = new ApplicationNotification();
        }

        return instance;
    }

    /**
     * Block cloning since we use singleton
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Enable application notification
     *
     * @param application Main application instance
     */
    public void enable( Context context ) {
        // check if already is enabled
        if( isEnabled ) {
            return;
        }

        try {
            // create  manager
            manager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );

            // create notificaion, flags and intent
            Notification notification = new Notification( R.drawable.uploader, null, System.currentTimeMillis() );
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            Intent notificationIntent = new Intent( context, MiltonPhotoUploader.class );
            PendingIntent contentIntent = PendingIntent.getActivity( context, 0, notificationIntent, 0 );

            // add event info to notification
            notification.setLatestEventInfo( context, context.getString( R.string.app_name ), context.getString( R.string.app_name ) + " is running.", contentIntent );

            // add notification to manager so it shows up for the user
            manager.notify( APP_NOTIFICAION, notification );

            // set enabled to true
            isEnabled = true;
        } catch( Exception e ) {
        }
    }

    /**
     * Dinable application notification
     */
    public void disable() {
        // check if already is disabled
        if( !isEnabled ) {
            return;
        }

        try {
            // remove the notification
            manager.cancel( APP_NOTIFICAION );

            // set enabled to false
            isEnabled = false;
        } catch( Exception e ) {
        }
    }
}
