/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.group.ChannelGroup
 *  io.netty.channel.group.ChannelGroupFuture
 *  io.netty.channel.group.ChannelMatcher
 *  io.netty.channel.group.ChannelMatchers
 *  io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
 */
package com.ob.server.websocket;

import com.ob.server.ServerShutdown;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

public class WebSocketServerShutdown
implements ServerShutdown {
    private ChannelGroup group;

    @Override
    public void shutDown() {
        if (this.group != null) {
            this.group.writeAndFlush(new CloseWebSocketFrame(1012, "Technical support.")
                    , ChannelMatchers.isNonServerChannel(), true).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void setChannelGroup(ChannelGroup group) {
        this.group = group;
    }
}

