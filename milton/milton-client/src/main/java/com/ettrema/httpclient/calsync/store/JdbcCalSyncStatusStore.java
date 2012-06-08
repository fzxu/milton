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

package com.ettrema.httpclient.calsync.store;

import com.bradmcevoy.utils.With;
import com.ettrema.db.Table;
import com.ettrema.db.TableCreatorService;
import com.ettrema.db.TableDefinitionSource;
import com.ettrema.db.UseConnection;
import com.ettrema.db.dialects.Dialect;
import com.ettrema.db.types.FieldTypes;
import com.ettrema.httpclient.calsync.CalSyncStatusStore;
import com.ettrema.httpclient.calsync.CalendarStore;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

/**
 * Uses a local database to record the sync status.
 * 
 * A record is recorded whenever a local resource is found to be in sync with
 * the remote resource.
 * 
 * Records are stored against the id of the local and remote stores they are
 * relevent to, so it is safe to use this store for syncing multiple sources
 * and destinations, even if overlapping.
 * 
 * Call the checkCreateTable method on application startup to auto-create tables.
 * This method will only create table if it does not exist, it will not modify the
 * schema.
 *
 * @author brad
 */
public class JdbcCalSyncStatusStore implements CalSyncStatusStore {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JdbcCalSyncStatusStore.class);
    
    public static final SyncStatusTable SYNC_TABLE = new SyncStatusTable();
    private final UseConnection useConnection;
    private final Dialect dialect;

    /**
     *
     * @param useConnection
     * @param dialect
     * @param group - so we can cache different collections in one table
     */
    public JdbcCalSyncStatusStore(UseConnection useConnection, Dialect dialect,String baseRemoteAddress, File root) {
        this.useConnection = useConnection;
        this.dialect = dialect;
        checkCreateTable();
    }
    
    public final void checkCreateTable() {
        TableDefinitionSource defs = new TableDefinitionSource() {

            @Override
            public List<? extends Table> getTableDefinitions() {
                return Arrays.asList(SYNC_TABLE);
            }

            @Override
            public void onCreate(Table t, Connection con) {
            }
        };
        
        final TableCreatorService creatorService = new TableCreatorService(null, Arrays.asList(defs), dialect);

        useConnection.use(new With<Connection, Object>() {

            @Override
            public Object use(Connection con) throws Exception {
                creatorService.processTableDefinitions(con);
                return null;
            }
        });
        
    }

    
    @Override
    public void setLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName, LastSync etags) {
        setStatus(local.getId(), remote.getId(), "l-" + resourceName, etags.getLocalEtag());
        setStatus(local.getId(), remote.getId(), "r-" + resourceName, etags.getRemoteEtag());
    }

    @Override
    public String getLastSyncedCtag(CalendarStore local, CalendarStore remote) {
        return getStatus(local.getId(), remote.getId(), "c");
    }

    @Override
    public LastSync getLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName) {
        String localEtag = getStatus(local.getId(), remote.getId(), "l-" + resourceName);
        String remoteEtag = getStatus(local.getId(), remote.getId(), "r-" + resourceName);
        return new LastSync(localEtag, remoteEtag);
    }

    @Override
    public void setLastSyncedCtag(CalendarStore local, CalendarStore remote, String remoteCtag) {
        setStatus(local.getId(), remote.getId(), "c", remoteCtag);
    }
    
    public String getStatus(final String localStoreId, final String remoteStoreId, final String resourceId) {        
        final SyncStatusTable T = SYNC_TABLE;
        final String sql = T.getSelect() + " WHERE " + T.localStoreId.getName() + " = ? AND " + T.remoteStoreId.getName() + " = ? AND " + T.resourceId.getName() + " = ?";
        String tag = useConnection.use(new With<Connection, String>() {

            @Override
            public String use(Connection con) throws Exception {
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, localStoreId);
                stmt.setString(2, remoteStoreId);
                stmt.setString(2, resourceId);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        return T.tag.get(rs);
                    } else {
                        return null;
                    }
                } finally {
                    UseConnection.close(rs);
                    UseConnection.close(stmt);
                }
            }
        });
        return tag;
    }    
    
    private void setStatus(final String localStoreId, final String remoteStoreId, final String resourceId, final String tag) {
        SyncStatusTable T = SYNC_TABLE;
        String sql = T.getDeleteBy(SYNC_TABLE.localStoreId);
        sql += " AND " + T.remoteStoreId.getName() + " = ?";
        sql += " AND " + T.resourceId.getName() + " = ?";
        final String deleteSql = sql;

        final String insertSql = SYNC_TABLE.getInsert();

        useConnection.use(new With<Connection, Object>() {

            @Override
            public Object use(Connection con) throws Exception {
                // delete any previous record (perhaps none)
                PreparedStatement stmt = con.prepareStatement(deleteSql);
                stmt.setString(1, localStoreId);
                stmt.setString(2, remoteStoreId);
                stmt.setString(3, resourceId);
                stmt.execute();
                UseConnection.close(stmt);

                // create new record
                stmt = con.prepareStatement(insertSql);
                SYNC_TABLE.localStoreId.set(stmt, 1, localStoreId);
                SYNC_TABLE.remoteStoreId.set(stmt, 2, remoteStoreId);
                SYNC_TABLE.resourceId.set(stmt, 3, resourceId);
                SYNC_TABLE.tag.set(stmt, 3, tag);
                stmt.execute();
                UseConnection.close(stmt);
                con.commit();

                return null;
            }
        });        
    }
        
    public static class SyncStatusTable extends com.ettrema.db.Table {
        public final Table.Field<String> localStoreId = add("localStoreId", FieldTypes.CHARACTER_VARYING, false);
        public final Table.Field<String> remoteStoreId = add("remoteStoreId", FieldTypes.CHARACTER_VARYING, false);
        /**
         * for etags this is "e"+resourceName
         * for ctags this is just "c"
         */
        public final Table.Field<String> resourceId = add("resourceId", FieldTypes.CHARACTER_VARYING, false);
        public final Table.Field<String> tag = add("tag", FieldTypes.CHARACTER_VARYING, false);
        

        public SyncStatusTable() {
            super("sync_status");
            this.addIndex("sync_idx", localStoreId, remoteStoreId, resourceId);
        }
    }
        
    
}
