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
package com.ettrema.http.caldav.demo.client;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.bradmcevoy.http.webdav.WebDavProtocol;
import com.ettrema.http.caldav.demo.TResourceFactory;
import com.ettrema.httpclient.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.xml.namespace.QName;

/**
 * This class is a demonstration of using milton-client with calendars
 *
 * It should be run as an executable java class
 *
 * @author brad
 */
public class OptionsExample {

    public static void main(String[] args) throws Exception {
        String server = getArg(args, 0, "localhost");
        int port = Integer.parseInt(getArg(args, 1, "8080"));
        String rootPath = getArg(args, 2, "/");
        String userName = getArg(args, 3, "userA");
        String password = getArg(args, 4, "password");
        String path = getArg(args, 5, "/");

        OptionsExample example = new OptionsExample(server, port, rootPath, userName, password);
        example.cd(path);
        example.options();
        example.propfind();
    }

    private static String getArg(String[] args, int i, String sDefault) {
        if (i < args.length) {
            return args[i];
        } else {
            return sDefault;
        }
    }
    private String server;
    private int port;
    private String rootPath;
    private String userName;
    private String password;
    private Host host;
    private Folder folder;

    public OptionsExample(String server, int port, String rootPath, String userName, String password) {
        this.server = server;
        this.port = port;
        this.rootPath = rootPath;
        host = new Host(server, rootPath, port, userName, password, null, null);
    }

    private void cd(String path) throws IOException, HttpException, NotAuthorizedException, BadRequestException {
        folder = host.getFolder(path);
    }

    private void options() throws Exception {
        Path p = Path.root;
        if (folder != null) {
            p = folder.path();
        }
        host.doOptions(p);
    }

    private void propfind() throws Exception {
        Path p = Path.root;
        if (folder != null) {
            p = folder.path();
        }
        System.out.println("propfind: " + p);
        List<PropFindResponse> list = host.propFind(Path.root, 0, new QName(WebDavProtocol.DAV_URI, "resourcetype", WebDavProtocol.DAV_PREFIX));
        System.out.println("responses: " + list.size());
        for (PropFindResponse r : list) {
            System.out.println("response: " + r.getHref() + " props: " + r.getProperties().size());
            for (Entry<QName, Object> entry : r.getProperties().entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}
