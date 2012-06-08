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

package com.ettrema.httpclient.calsync;

import java.io.IOException;

/**
 * A "delta" here refers to some difference between the client and server
 * file systems.
 * 
 *
 * @author brad
 */
public interface DeltaListener {

    /**
     * Called when a local deletion has been detected, which means we should
     * delete the event from the server
     * 
     * @param remote
     * @param remoteStore
     * @return - true if the event was deleted
     */
    boolean onLocalDeletion(CalSyncEvent remote, CalendarStore remoteStore);
    
    /**
     * Called when there is a remotely new or remotely modified calendar event
     * 
     * @param remote - the remote calendar event
     * @param local - the local CalendarStore
     * @return null if no updates were made, otherwise the locally modified etag
     * @throws IOException 
     */
    String onRemoteChange(CalSyncEvent remote, CalendarStore remoteStore, CalSyncEvent localEvent, CalendarStore localStore);
    
    /**
     * Called when an event has been deleted from the server, but is still locally present
     * 
     * @param localRes
     * @param localStore 
     * @return true if the event was deleted
     */
    boolean onRemoteDelete(CalSyncEvent localRes, CalendarStore localStore);
        
    /**
     * Called when a local change has been detected, so should update remote server
     * 
     * @param localRes
     * @param localStore
     * @param remoteRes
     * @param remoteStore 
     * @return null if no changes were made, otherwise return the new etag for the remotely updated or created event
     */
    String onLocalChange(CalSyncEvent localRes,CalendarStore localStore, CalSyncEvent remoteRes, CalendarStore remoteStore);
     
}
