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
 */
package com.mycompany;

import com.bradmcevoy.common.ContentTypeService;
import com.bradmcevoy.common.DefaultContentTypeService;
import com.bradmcevoy.http.*;
import com.bradmcevoy.http.http11.*;
import com.bradmcevoy.http.values.ValueWriters;
import com.bradmcevoy.http.webdav.*;
import com.ettrema.http.acl.ACLProtocol;
import com.ettrema.http.acl.AccessControlledResourceTypeHelper;
import com.ettrema.http.caldav.CalDavProtocol;
import com.ettrema.http.carddav.AddressBookResourceTypeHelper;
import com.ettrema.http.carddav.CardDavProtocol;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author brad
 */
public class ScratchServlet implements Servlet {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ScratchServlet.class);
    private ServletConfig config;
    private HttpManager httpManager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;

// Here is how the resource factory of CardDavResource classes is connected to Milton
        //CardDavResourceFactory carddavResourceFactory = new CardDavResourceFactory();
        
        Map<String,List<String>> mapOfContentTypes = new ConcurrentHashMap<String, List<String>>();        
        
        ETagGenerator eTagGenerator = new JustCopyTheUniqueIdETagGenerator();
        ContentTypeService contentTypeService = new DefaultContentTypeService(mapOfContentTypes);
        
        TResourceFactory carddavResourceFactory = new TResourceFactory();
        WebDavResourceTypeHelper rth = new WebDavResourceTypeHelper();
        AddressBookResourceTypeHelper crth = new AddressBookResourceTypeHelper(new AccessControlledResourceTypeHelper(rth));
        // Authentication server - Notice that digest is disabled
        AuthenticationService authService = new AuthenticationService();
        authService.setDisableDigest(true);
        HandlerHelper hh = new HandlerHelper(authService);
        
        ValueWriters valueWriters = new ValueWriters();
        
        PropFindXmlGenerator propFindXmlGenerator = new PropFindXmlGenerator(valueWriters);
        
        Http11ResponseHandler http11ResponseHandler = new DefaultHttp11ResponseHandler(authService, eTagGenerator);
        WebDavResponseHandler webDavResponseHandler = new DefaultWebDavResponseHandler(http11ResponseHandler, crth, propFindXmlGenerator);
        
        // Protocols supported - notice that CalDav support is not a mistake
        Http11Protocol http11 = new Http11Protocol(webDavResponseHandler, hh, contentTypeService, false, eTagGenerator);
        WebDavProtocol webdav = new WebDavProtocol(hh, crth, webDavResponseHandler, null);
        webdav.seteTagGenerator(eTagGenerator);
        CalDavProtocol caldav = new CalDavProtocol(carddavResourceFactory, webDavResponseHandler, hh, webdav);
        CardDavProtocol carddav = new CardDavProtocol(carddavResourceFactory, webDavResponseHandler, hh, webdav);
        // Install our own optimized multiget report handler
//        ImprovedAddressBookMultiGetReport.patch(webdav);

        ACLProtocol acl = new ACLProtocol(webdav);
        ProtocolHandlers protocols = new ProtocolHandlers(Arrays.asList(http11, webdav, caldav, carddav, acl));

        httpManager = new HttpManager(carddavResourceFactory, webDavResponseHandler, protocols);
    }


    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        try {
            Request request = new com.bradmcevoy.http.ServletRequest(req, config.getServletContext());
            Response response = new com.bradmcevoy.http.ServletResponse(resp);
            httpManager.process(request, response);
        } finally {
            servletResponse.getOutputStream().flush();
            servletResponse.flushBuffer();
        }
    }

    @Override
    public String getServletInfo() {
        return "scratch";
    }

    @Override
    public void destroy() {
    }
}
