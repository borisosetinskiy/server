/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.security;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessage;

import java.util.List;

public interface SecurityProcessor<T extends HttpMessage> extends  SecurityChain{
    void process(ChannelHandlerContext channelHandlerContext
            , T o);
}

