package com.ob.server.http.websocket;

import com.ob.server.netty.ServerShutdown;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

/**
 * Created by boris on 4/11/2017.
 */
public class WebSocketServerShutdown implements ServerShutdown {
    private ChannelGroup group;
    @Override
    public void shutDown() {
        if(group!=null){
            group.writeAndFlush(new CloseWebSocketFrame(1012, "Technical support.")
                    , ChannelMatchers.isNonServerChannel(), true);
        }

    }

    @Override
    public void setChannelGroup(final ChannelGroup group) {
        this.group = group;
    }
}
