
package com.ob.server.handlers.websocket;

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

