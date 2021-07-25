package com.ob.server;

import com.ob.server.session.RequestService;
import com.ob.server.handlers.websocket.RequestSessionWebSocketServerHandler;
import com.ob.server.handlers.websocket.TextWebSocketServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class WebSocketChannelHandlerFactory implements ChannelHandlerFactory {
    @Override
    public ChannelHandler[] create(ChannelPipeline pipeline
            , RequestService requestService, ChannelGroup channels) {
        return new ChannelHandler[]{
                new WebSocketServerCompressionHandler(),
                new RequestSessionWebSocketServerHandler(requestService, channels)
        };
    }
}
