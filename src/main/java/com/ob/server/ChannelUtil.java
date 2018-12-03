/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.group.ChannelGroup
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public class ChannelUtil {
    public static void gather(ChannelHandlerContext ctx, ChannelGroup allChannels) {
        Channel channel = ctx.channel();
        allChannels.add(channel);
    }
}

