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

package com.ettrema.tutorial.hr.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static long nextId;
    
    private Long id;
    
    private String name;
     
    public static Department create(String name) {
    	Department d = new Department();
    	d.setId(nextId++);
    	d.setName(name);
    	return d;
    }


    @Column(length = 15)
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

}
