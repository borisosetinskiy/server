package com.ob.server.http.handler;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerFactory {
    ChannelHandler[] create();
}
