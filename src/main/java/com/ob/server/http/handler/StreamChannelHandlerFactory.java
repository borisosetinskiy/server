package com.ob.server.http.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class StreamChannelHandlerFactory implements ChannelHandlerFactory {
    @Override
    public ChannelHandler[] create() {
        return new ChannelHandler[]{new ChunkedWriteHandler()};
    }
}
