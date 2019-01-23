/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface Access {
    void check(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list);
}

