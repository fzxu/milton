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

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.httpclient.calsync.CalSyncStatusStore.LastSync;
import com.ettrema.httpclient.calsync.ConflictManager.ConflictAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Compares two calendars, usually local and remote, and generates events when
 * differences (aka deltas) are found
 *
 * @author brad
 */
public class CalendarDeltaGenerator {

    private final CalendarStore local;
    private final CalendarStore remote;
    private final CalSyncStatusStore statusStore;
    private final DeltaListener deltaListener;
    private final ConflictManager conflictManager;

    public CalendarDeltaGenerator(CalendarStore local, CalendarStore remote, CalSyncStatusStore statusStore, DeltaListener deltaListener, ConflictManager conflictManager) {
        this.local = local;
        this.remote = remote;
        this.statusStore = statusStore;
        this.deltaListener = deltaListener;
        this.conflictManager = conflictManager;
    }

    public void compareCalendars() throws NotAuthorizedException, BadRequestException {

        // Check ctags
        String remoteCtag = remote.getCtag();
        String lastSyncedCTag = statusStore.getLastSyncedCtag(local, remote);
        if (remoteCtag != null && remoteCtag.equals(lastSyncedCTag)) {
            // no changes, we're done
            return;
        }

        List<CalSyncEvent> remoteChildren = new ArrayList<CalSyncEvent>(remote.getChildren());
        Map<String, CalSyncEvent> remoteMap = SyncUtils.toMap(remoteChildren);
        List<CalSyncEvent> localChildren = new ArrayList<CalSyncEvent>(local.getChildren());
        Map<String, CalSyncEvent> localMap = SyncUtils.toMap(localChildren);

        for (CalSyncEvent remoteRes : remoteChildren) {
            CalSyncEvent localRes = localMap.get(remoteRes.getName());
            LastSync lastSynced = statusStore.getLastSyncedEtag(local, remote, remoteRes.getName());
            if (localRes == null) {
                doMissingLocal(remoteRes, lastSynced);
            } else {                
                if (lastSynced == null) {
                    // event both local and remote, can't tell which is latest
                    onConflict(remoteRes, localRes);
                } else {
                    checkEtags(lastSynced, localRes, remoteRes);
                }
            }
        }

        // Now check for remote deletes by iterating over local children looking for missing remotes
        for (CalSyncEvent localRes : localChildren) {
            CalSyncEvent remoteRes = remoteMap.get(localRes.getName());
            if (remoteRes == null) {
                LastSync lastSynced = statusStore.getLastSyncedEtag(local, remote, localRes.getName());
                doMissingRemote(localRes, lastSynced);
            }
        }

        statusStore.setLastSyncedCtag(local, remote, remoteCtag);
    }

    private void doMissingLocal(CalSyncEvent remoteRes, LastSync lastSynced) {
        // There is a resource in the remote store, but no corresponding local resource
        // Need to determine if it has been locally deleted or remotely created

        if (lastSynced == null) {
            // we've never seen it before, so it must be remotely new
            String newLocalEtag = deltaListener.onRemoteChange(remoteRes, remote, null, local);
            if (newLocalEtag != null) {
                LastSync etags = new LastSync(newLocalEtag, remoteRes.getEtag());
                statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), etags);
            }
        } else {
            // we have previously synced this item, it no longer exists, so must have been deleted
            if (deltaListener.onLocalDeletion(remoteRes, remote)) {
                statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), null);
            }
        }
    }

    /**
     * There is a local event with no corresponding remote event, so either
     * remotely deleted or locall created. Check sync status to find out which
     *
     * @param localRes
     */
    private void doMissingRemote(CalSyncEvent localRes, LastSync lastSynced) {
        if (lastSynced == null) {
            // never before synced, so is locally new
            String newRemoteEtag = deltaListener.onLocalChange(localRes, local, null, remote);
            if (newRemoteEtag != null) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), new LastSync(localRes.getEtag(), newRemoteEtag));
            }
        } else {
            // has been synced before, so was on server and not now = rmotely deleted
            if (deltaListener.onRemoteDelete(localRes, local)) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), null);
            }
        }
    }

    private void checkEtags(LastSync lastSynced, CalSyncEvent localRes, CalSyncEvent remoteRes) {
        boolean localChange = !lastSynced.getLocalEtag().equals(localRes.getEtag());
        boolean remoteChange = !lastSynced.getRemoteEtag().equals(remoteRes.getEtag());
        if (localChange && remoteChange) {
            onConflict(remoteRes, localRes);
        } else if (localChange) {
            String newRemoteEtag = deltaListener.onLocalChange(localRes, local, remoteRes, remote);
            if (newRemoteEtag != null) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), new LastSync(localRes.getEtag(), newRemoteEtag));
            }
        } else if( remoteChange) {
            String newLocalEtag = deltaListener.onRemoteChange(remoteRes, remote, localRes, local);
            if (newLocalEtag != null) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), new LastSync(newLocalEtag, remoteRes.getEtag()));
            }
        } else {
            // no change
        }
    }

    private boolean onConflict(CalSyncEvent remoteRes, CalSyncEvent localRes) {
        // local and remote are different, but we don't have sync status to tell us which is
        // we should use. This can happen if the user has removed the sync status database
        // or has just installed this sync software.
        // So we delegate to the conflict handler to decide what to do
        ConflictAction action = conflictManager.resolveConflict(remoteRes, localRes, remote, local);
        switch (action) {
            case NO_CHANGE:
                return true;
            case USE_LOCAL:
                String newRemoteEtag = deltaListener.onLocalChange(localRes, local, remoteRes, remote);
                if (newRemoteEtag != null) {
                    statusStore.setLastSyncedEtag(local, remote, localRes.getName(), new LastSync(localRes.getEtag(), newRemoteEtag));
                }
                return true;
            case USE_REMOTE:
                String newLocalEtag = deltaListener.onRemoteChange(remoteRes, remote, localRes, local);
                if (newLocalEtag != null) {
                    statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), new LastSync(newLocalEtag, remoteRes.getEtag()));
                }
        }
        return false;
    }
}
