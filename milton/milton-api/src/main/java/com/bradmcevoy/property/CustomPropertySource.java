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

package com.bradmcevoy.property;

import com.bradmcevoy.http.CustomProperty;
import com.bradmcevoy.http.CustomPropertyResource;
import com.bradmcevoy.http.Resource;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author brad
 */
public class CustomPropertySource implements PropertySource {

    public Object getProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            return prop.getTypedValue();
        } else {
            return null;
        }
    }

    public PropertyMetaData getPropertyMetaData( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            return new PropertyMetaData( PropertyAccessibility.WRITABLE, prop.getValueClass());
        } else {
            return PropertyMetaData.UNKNOWN;
        }

    }


    public void setProperty( QName name, Object value, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            prop.setFormattedValue( value.toString() );
        } else {
            throw new RuntimeException( "property not found: " + name.getLocalPart() );
        }
    }

    public boolean hasProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        return prop != null;
    }

    public void clearProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        prop.setFormattedValue( null );
    }

    private CustomProperty lookupProperty( QName name, Resource r ) {
        if( name == null) throw new IllegalArgumentException( "name is null");
        if( r instanceof CustomPropertyResource ) {
            CustomPropertyResource cpr = (CustomPropertyResource) r;
            if( cpr.getNameSpaceURI() == null ) throw new IllegalArgumentException( "namespace uri is null on CPR");
            if( cpr.getNameSpaceURI().equals( name.getNamespaceURI() ) ) {
                return cpr.getProperty( name.getLocalPart() );
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public List<QName> getAllPropertyNames( Resource r ) {
        List<QName> list = new ArrayList<QName>();
        if( r instanceof CustomPropertyResource ) {
            CustomPropertyResource cpr = (CustomPropertyResource) r;
            for( String n : cpr.getAllPropertyNames() ) {
                QName qname = new QName( cpr.getNameSpaceURI(), n);
                list.add( qname );
            }
        }
        return list;
    }
    
}
