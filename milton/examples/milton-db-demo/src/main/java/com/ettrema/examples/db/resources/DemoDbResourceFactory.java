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

package com.ettrema.examples.db.resources;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.SecurityManager;
import com.ettrema.examples.db.domain.Vehicle;
import com.ettrema.examples.db.domain.VehicleDao;
import com.ettrema.http.fs.LockManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class DemoDbResourceFactory implements ResourceFactory {

    private static final Logger log = LoggerFactory.getLogger( DemoDbResourceFactory.class );
    private final com.bradmcevoy.http.SecurityManager sm;
    private final LockManager lockManager;
    private final VehicleDao vehicleDao;

    public DemoDbResourceFactory( SecurityManager sm, LockManager lockManager, VehicleDao vehicleDao ) {
        this.sm = sm;
        this.lockManager = lockManager;
        this.vehicleDao = vehicleDao;
    }


    public Resource getResource( String host, String path ) {
        log.debug( "getResource: " + path );
        Path p = Path.path( path );
        log.debug( "length: " + p.getLength() );
        if( p.getLength() == 0 ) {
            return new AllMakes( this, sm );
        } else if( p.getLength() == 1 ) {
            if( p.getName().equals( "allvehicles.csv")) {
                return new AllVehiclesCsv( "allvehicles.csv", this, sm, vehicleDao);
            } else {
                return new VehicleMake( this, sm, p.getName() );
            }
        } else if( p.getLength() == 2 ) {
            VehicleMake makeResource = new VehicleMake( this, sm, p.getParent().getName() );
            return makeResource.child( p.getName());
        } else {
            return null;
        }
    }

    public List<Resource> getAllMakes() {
        Set<String> makes = vehicleDao.getAllVehicleMakes();
        List<Resource> list = new ArrayList<Resource>();
        for(String s : makes) {
            list.add( new VehicleMake( this, sm, s));
        }
        return list;
    }

    public List<? extends Resource> getAllMakesAndCsv() {
        List<Resource> list = getAllMakes();
        list.add( new AllVehiclesCsv( "allvehicles.csv", this, sm, vehicleDao));
        return list;
    }

    public List<VehicleResource> getVehiclesByMake(String make) {
        List<Vehicle> vehicles = vehicleDao.getVehiclesByMake(make);
        List<VehicleResource> list = new ArrayList<VehicleResource>();
        for( Vehicle v : vehicles ) {
            VehicleResource r = new VehicleResource( this, sm, v, vehicleDao );
            list.add( r );
        }
        return list;
    }
}
