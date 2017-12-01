package com.ob.server.http.handler;

import com.ob.server.http.websocket.TextWebSocketServerHandler;
import com.ob.server.http.websocket.WebSocketServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class WebsocketChannelHandlerFactory implements ChannelHandlerFactory {
    final String path;

    public WebsocketChannelHandlerFactory(String path) {
        this.path = path;
    }

    @Override
    public ChannelHandler[] create() {
        return new ChannelHandler[]{
                new WebSocketServerCompressionHandler(),
                new WebSocketServerHandler(path),
                new TextWebSocketServerHandler()};
    }
}
