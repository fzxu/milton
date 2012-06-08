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

import com.bradmcevoy.http.http11.DefaultHttp11ResponseHandler;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Loads the spring context from classpath at applicationContext.xml
 *
 * This filter then gets the bean named milton.http.manager and uses that for
 * milton processing.
 *
 * Requests with a path which begins with one of the exclude paths will not be
 * processed by milton. Instead, for these requests, the filter chain will be
 * invoked so the request can be serviced by JSP or a servlet, etc
 *
 * This uses an init parameter called milton.exclude.paths, which should be a
 * comma seperated list of paths to ignore. For example:
 * /static,/images,/login.jsp
 *
 * This allows non-milton resources to be accessed, while still mapping all urls
 * to milton
 *
 * @author bradm
 */
public class SpringMiltonFilter implements javax.servlet.Filter {

    private ClassPathXmlApplicationContext context;
    private HttpManager httpManager;
    private FilterConfig filterConfig;
    private ServletContext servletContext;
    /**
     * Resources with this as the first part of their path will not be served
     * from milton. Instead, this filter will allow filter processing to
     * continue so they will be served by JSP or a servlet
     */
    private String[] excludeMiltonPaths;

    @Override
    public void init(FilterConfig fc) throws ServletException {        
        StaticApplicationContext parent = new StaticApplicationContext();
        parent.getBeanFactory().registerSingleton("servletContext", fc.getServletContext());
        parent.refresh();
        context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"}, parent);
        this.httpManager = (HttpManager) context.getBean("milton.http.manager");
        httpManager.setBuffering(DefaultHttp11ResponseHandler.BUFFERING.never);
        this.filterConfig = fc;
        servletContext = fc.getServletContext();
        String sExcludePaths = fc.getInitParameter("milton.exclude.paths");
        excludeMiltonPaths = sExcludePaths.split(",");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest hsr = (HttpServletRequest) req;
            String url = hsr.getRequestURI();
            // Allow certain paths to be excluded from milton, these might be other servlets, for example
            for (String s : excludeMiltonPaths) {
                if (url.startsWith(s)) {
                    fc.doFilter(req, resp);
                    return;
                }
            }
            doMiltonProcessing((HttpServletRequest) req, (HttpServletResponse) resp);
        } else {
            fc.doFilter(req, resp);
            return;
        }
    }

    @Override
    public void destroy() {
        context.close();
    }

    private void doMiltonProcessing(HttpServletRequest req, HttpServletResponse resp) throws IOException {        
        try {
            MiltonServlet.setThreadlocals(req, resp);
            Request request = new com.bradmcevoy.http.ServletRequest(req,servletContext);
            Response response = new com.bradmcevoy.http.ServletResponse(resp);
            httpManager.process(request, response);
        } finally {
            MiltonServlet.clearThreadlocals();
            resp.getOutputStream().flush();
            resp.flushBuffer();
        }
    }
}
