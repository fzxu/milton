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

package com.bradmcevoy.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ApplicationConfig {
    
    final FilterConfig config;
    final ServletConfig servletConfig;
    final ServletContext servletContext;
    final List<String> parameterNames;
    
    public ApplicationConfig() {
        parameterNames = new ArrayList<String>();
        this.config = null;
        this.servletConfig = null;
        this.servletContext = null;
    }
    
    public ApplicationConfig(FilterConfig config) {
        parameterNames = new ArrayList<String>();
        this.config = config;
        this.servletConfig = null;
        servletContext = config.getServletContext();
        if( config == null ) return ;        
        Enumeration en = config.getInitParameterNames();
        while( en.hasMoreElements() ) {
            parameterNames.add( (String)en.nextElement() );
        }        
    }

    public ApplicationConfig(ServletConfig config) {
        parameterNames = new ArrayList<String>();
        this.config = null;
        this.servletConfig = config;
        servletContext = servletConfig.getServletContext();
        if( config == null ) return ;        
        Enumeration en = config.getInitParameterNames();
        while( en.hasMoreElements() ) {
            parameterNames.add( (String)en.nextElement() );
        }        
    }
    
    public String getFilterName() {
        if( servletConfig != null) {
            return servletConfig.getServletName();
        } else {
            return config.getFilterName();
        }
    }

    public String getContextName() {
        return servletContext.getServletContextName();
    }
    
    public String getInitParameter(String string) {        
        if( servletConfig != null) {
            return servletConfig.getInitParameter(string);
        } else {
            return config.getInitParameter(string);
        }        
        
    }

    public Collection<String> getInitParameterNames() {
        return parameterNames;
    }
    
    public File getConfigFile(String path) {
        File f = new File( getWebInfDir(), path);
        return f;
    }

    public File getWebInfDir() {
        String s = servletContext.getRealPath("WEB-INF/" );
        File f = new File(s);
        return f;
    }
    
    public File getRootFolder() {
        String s = servletContext.getRealPath("/");
        File f = new File(s);
        return f;        
    }
    
    public File mapPath( String url ) {
        String pth;
        pth = servletContext.getRealPath(url);
        File file = new File(pth);
        return file;
    }
}
