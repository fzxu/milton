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


package com.ettrema.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Help extends AbstractConsoleCommand {

    final List<ConsoleCommandFactory> factories;

    Help(List<String> args, String host, String currentDir, ConsoleResourceFactory consoleResourceFactory) {
        super(args, host, currentDir, consoleResourceFactory);
        this.factories = consoleResourceFactory.factories;
    }
    
    @Override
    public Result execute() {
        StringBuilder sb = new StringBuilder();
        List<ConsoleCommandFactory> list = new ArrayList<ConsoleCommandFactory>();
        list.addAll(factories );
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                ConsoleCommandFactory f1 = (ConsoleCommandFactory)o1;
                ConsoleCommandFactory f2 = (ConsoleCommandFactory)o2;
                return f1.getCommandNames()[0].compareTo(f2.getCommandNames()[0]);
            }
        });
        for( ConsoleCommandFactory f : list ) {
            sb.append("<b>");
            for( String s : f.getCommandNames() ) {
                sb.append(s).append(" ");
            }
            sb.append("</b>");
            sb.append("<br/>").append("\n");
            sb.append("<br/>").append(f.getDescription());
            sb.append("<br/>").append("\n");
            sb.append("<br/>").append("\n");
        }
        return new Result(this.cursor.getPath().toString(), sb.toString());
    }

}
