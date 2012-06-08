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

package com.ettrema.tutorial.hr.web;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.ettrema.tutorial.hr.domain.Department;


public class HrResourceFactory implements ResourceFactory {
	
	private Logger log = LoggerFactory.getLogger(HrResourceFactory.class);
	
	public static final String REALM = "MyCompany";
	
	private final SessionFactory sessionFactory;
	
	public HrResourceFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	
	
	@Override
	public Resource getResource(String host, String p) {		
		Path path = Path.path(p).getStripFirst();
		log.debug("getResource: " + path);
		Session session = sessionFactory.openSession();
		if( path.isRoot() ) {
			return new AllDepartmentsResource(this, session); 
		} else if( path.getLength() == 1 ) {
			return findDepartment(path.getName(), session);
		} else if( path.getLength() == 2) {
			// TODO
			return null;
		} else {
			return null;
		}
	}

	public List<Resource> findAllDepartments(Session session) {
		Criteria crit = session.createCriteria(Department.class);
		List list = crit.list();
		if( list == null || list.size() == 0) {
			return Collections.EMPTY_LIST;
		} else {
			List<Resource> departments = new ArrayList<Resource>();
			for( Object o : list ) {
				departments.add( new DepartmentResource(this, (Department)o) );
			}
			return departments;
		}
		
	}
	
	public Resource findDepartment(String name, Session session) {
		log.debug("findDepartment: " + name);
		Criteria crit = session.createCriteria(Department.class);
		crit.add(Expression.eq("name", name));
		List list = crit.list();
		if( list == null || list.size() == 0 ) {
			log.debug("not found");
			return null;
		} else {
			Department d = (Department) list.get(0);
			log.debug("found: " + d.getName());
			return new DepartmentResource(this, d);
		}
	}

}
