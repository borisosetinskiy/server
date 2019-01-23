/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.util.ReferenceCountUtil
 */
package com.ob.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class AuthenticationHandler
extends MessageToMessageDecoder<Object> {
    private Access access;

    public AuthenticationHandler(Access access) {
        this.access = access;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        access.check(channelHandlerContext, o, list);
    }
}

