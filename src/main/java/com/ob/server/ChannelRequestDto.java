package com.ob.server;

import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Arrays;

public class ChannelRequestDto {
    private ChannelHandlerContext ctx;
    private Object2ObjectArrayMap<String, String> context;
    private long timestamp = System.currentTimeMillis();

    public ChannelRequestDto(ChannelHandlerContext ctx, Object2ObjectArrayMap context) {
        this.ctx = ctx;
        this.context = context;
    }

    public ChannelHandlerContext getChannelContext() {
        return this.ctx;
    }

    public Object2ObjectArrayMap<String, String> getContext() {
        return this.context;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{channel=").append(this.ctx.channel()).append(", context=");
        builder.append(Arrays.toString(this.context.keySet().toArray())).append(':').append(Arrays.toString(this.context.values().toArray())).append('}');
        return builder.toString();
    }

}

