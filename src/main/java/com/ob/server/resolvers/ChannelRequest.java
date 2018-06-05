package com.ob.server.resolvers;


import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public interface ChannelRequest {
    Object2ObjectArrayMap<String, String> getContext();
    long getTimestamp();
    ChannelHandlerContext getChannelContext();
}
