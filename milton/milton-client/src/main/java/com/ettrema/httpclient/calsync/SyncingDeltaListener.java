/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.ettrema.httpclient.calsync;

/**
 * A DeltaListener which updates the stores to bring them into sync
 *
 * @author brad
 */
public class SyncingDeltaListener implements DeltaListener{

    @Override
    public boolean onLocalDeletion(CalSyncEvent remote, CalendarStore remoteStore) {
        remoteStore.deleteEvent(remote); 
        return true;
    }

    @Override
    public String onRemoteChange(CalSyncEvent remote, CalendarStore remoteStore, CalSyncEvent localEvent, CalendarStore localStore) {
        String icalText = remoteStore.getICalData(remote);
        String newLocalEtag;
        if( localEvent == null ) {
            newLocalEtag = localStore.createICalEvent(remote.getName(), icalText);
        } else {
            newLocalEtag = localStore.setICalData(localEvent, icalText);
        }
        return newLocalEtag;
    }

    @Override
    public boolean onRemoteDelete(CalSyncEvent localRes, CalendarStore localStore) {
        localStore.deleteEvent(localRes); 
        return true;
    }

    @Override
    public String onLocalChange(CalSyncEvent localRes,CalendarStore localStore, CalSyncEvent remoteRes, CalendarStore remoteStore) {
        String s = localStore.getICalData(localRes);
        String newRemoteEtag;
        if( remoteRes == null ) {
            newRemoteEtag = remoteStore.createICalEvent(localRes.getName(), s);
        } else {
            newRemoteEtag = remoteStore.setICalData(remoteRes, s);
        }   
        return newRemoteEtag;
    }
    
}
