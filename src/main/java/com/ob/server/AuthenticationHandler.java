/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.util.ReferenceCountUtil
 */
package com.ob.server;

import io.netty.handler.codec.MessageToMessageDecoder;

public abstract class AuthenticationHandler
extends MessageToMessageDecoder<Object> {
    protected Access access;

    public AuthenticationHandler(Access access) {
        this.access = access;
    }


}

