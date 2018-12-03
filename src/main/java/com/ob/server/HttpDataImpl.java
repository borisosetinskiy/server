/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.LastHttpContent
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.concurrent.atomic.AtomicBoolean;

public class HttpDataImpl
implements HttpData {
    private Object2ObjectArrayMap<String, String> context;
    private AtomicBoolean finished = new AtomicBoolean();

    @Override
    public void collect(Object msg) {
        if (!this.finished()) {
            if (msg instanceof HttpRequest) {
                this.context = HttpUtils.params((HttpObject)msg, null);
            }
            if (msg instanceof LastHttpContent) {
                HttpUtils.params((HttpObject)msg, this.context);
                this.finished.getAndSet(true);
            }
        }
    }

    @Override
    public boolean finished() {
        return this.finished.get();
    }

    @Override
    public Object2ObjectArrayMap<String, String> context() {
        return this.context;
    }

    public static HttpData collectData(Object msg, HttpData data) {
        if (data == HttpData.EMPTY || data == null) {
            data = new HttpDataImpl();
        }
        data.collect(msg);
        return data;
    }
}

