package com.ob.server.resolvers;

import com.ob.server.http.handler.ChannelHandlerFactory;

public interface ChannelHandlerResolvers {
    ChannelHandlerFactory resolve(String path);
}
