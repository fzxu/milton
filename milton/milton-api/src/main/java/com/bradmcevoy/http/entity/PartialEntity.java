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

package com.bradmcevoy.http.entity;

import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.io.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PartialEntity implements Response.Entity {

    private static final Logger log = LoggerFactory.getLogger(PartialEntity.class);

    private List<Range> ranges;
    private File temp;

    public PartialEntity(List<Range> ranges, File temp) {
        this.ranges = ranges;
        this.temp = temp;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public File getTemp() {
        return temp;
    }

    @Override
    public void write(Response response, OutputStream outputStream) throws Exception {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(temp);
            writeRanges(fin, ranges, outputStream);
        } finally {
            StreamUtils.close(fin);
        }
    }

    public static void writeRanges(InputStream in, List<Range> ranges, OutputStream responseOut) throws IOException {
        try {
            InputStream bufIn = in; //new BufferedInputStream(in);
            long pos = 0;
            for (Range r : ranges) {
                long skip = r.getStart() - pos;
                bufIn.skip(skip);
                long length = r.getFinish() - r.getStart();
                sendBytes(bufIn, responseOut, length);
                pos = r.getFinish();
            }
        } finally {
            StreamUtils.close(in);
        }
    }

    public static void sendBytes(InputStream in, OutputStream out, long length) throws IOException {
        log.trace("sendBytes: " + length);
        long numRead = 0;
        byte[] b = new byte[1024];
        while (numRead < length) {
            long remainingBytes = length - numRead;
            int maxLength = remainingBytes > 1024 ? 1024 : (int) remainingBytes;
            int s = in.read(b, 0, maxLength);
            if (s < 0) {
                break;
            }
            numRead += s;
            out.write(b, 0, s);
        }

    }

    public static void writeRange(InputStream in, Range r, OutputStream responseOut) throws IOException {
        long skip = r.getStart();
        in.skip(skip);
        long length = r.getFinish() - r.getStart();
        sendBytes(in, responseOut, length);
    }

}
