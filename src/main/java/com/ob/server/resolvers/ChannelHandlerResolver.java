package com.ob.server.resolvers;

import com.ob.server.http.handler.ChannelHandlerFactory;

public interface ChannelHandlerResolver {
    ChannelHandlerFactory resolve(String path);
}
