/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ob.server.http;

import io.netty.handler.codec.http.HttpConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;


public class QueryDecoder {

    public static final String PATH = "P";
    public static Object2ObjectArrayMap decode(String uri) {
        Object2ObjectArrayMap<String, String> params = new Object2ObjectArrayMap<>();
        int pathEndPos = -1;
        if ((pathEndPos = uri.indexOf('?'))!=-1) {
            if (pathEndPos >= 0 && pathEndPos < uri.length() - 1) {
                RequestUtil.decodeParams(uri.substring(pathEndPos + 1), params, HttpConstants.DEFAULT_CHARSET);
            }
        } else {
            if (!uri.isEmpty()) {
                RequestUtil.decodeParams(uri, params, HttpConstants.DEFAULT_CHARSET);
            }
        }
        params.put(PATH, RequestUtil.decodeComponent(pathEndPos < 0 ? uri : uri.substring(0, pathEndPos), HttpConstants.DEFAULT_CHARSET));
        return params;
    }



}
