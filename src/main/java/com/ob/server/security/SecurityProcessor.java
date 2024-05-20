/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.security;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMessage;

public interface SecurityProcessor extends SecurityChain {
    void process(ChannelHandlerContext channelHandlerContext
            , DefaultHttpRequest o);
}

