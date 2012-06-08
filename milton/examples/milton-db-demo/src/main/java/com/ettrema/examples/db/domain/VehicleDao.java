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

package com.ettrema.examples.db.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

/**
 *
 * @author brad
 */
public class VehicleDao {

    private final SessionFactory sessionFactory;
    private long nextId;

    public VehicleDao( SessionFactory sessionFactory ) {
        this.sessionFactory = sessionFactory;
    }

    public Set<String> getAllVehicleMakes() {
        Session sess = sessionFactory.openSession();
        Query query = sess.createQuery( "SELECT distinct  v.vehicleMake FROM Vehicle v" );
        List list = query.list();
        Set<String> makes = new LinkedHashSet<String>();
        for( Object o : list ) {
            String m = (String) o;
            makes.add( m );
        }
        return makes;
    }

    public void deleteAll() {
        Session sess = sessionFactory.openSession();
        Query query = sess.createQuery( "DELETE FROM Vehicle v" );
        query.executeUpdate();
    }

    public Vehicle add( Vehicle v ) {
        v.setId( nextId++ );
        Session sess = sessionFactory.openSession();
        Transaction t = sess.beginTransaction();
        sess.save( v );
        sess.flush();
        t.commit();


        return v;
    }

    public List<Vehicle> getAllVehicles() {
        Session sess = sessionFactory.openSession();
        List list = sess.createQuery( "SELECT v FROM Vehicle v" ).list();
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        for( Object o : list ) {
            vehicles.add( (Vehicle) o );
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByMake( String make ) {
        Session sess = sessionFactory.openSession();
        List list = sess.createQuery( "SELECT v FROM Vehicle v WHERE v.vehicleMake = '" + make + "'" ).list();
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        for( Object o : list ) {
            vehicles.add( (Vehicle) o );
        }
        return vehicles;
    }

    public void update( Vehicle vehicle ) {
        Session sess = sessionFactory.openSession();
        Transaction t = sess.beginTransaction();
        sess.saveOrUpdate( vehicle );
        t.commit();
    }

    public void delete( Vehicle vehicle ) {
        Session sess = sessionFactory.openSession();
        Transaction t = sess.beginTransaction();
        sess.delete( vehicle);
        t.commit();
    }
}
