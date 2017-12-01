package com.ob.server.http.handler;

import com.ob.server.resolvers.ChannelHandlerResolver;

import java.util.Map;

public class DefaultChannelHandlerResolver implements ChannelHandlerResolver {
    private final Map<String, ChannelHandlerFactory> channelHandlerCreators;

    public DefaultChannelHandlerResolver(Map<String, ChannelHandlerFactory> channelHandlerCreators) {
        this.channelHandlerCreators = channelHandlerCreators;
    }

    @Override
    public ChannelHandlerFactory resolve(String path) {
         return channelHandlerCreators.get(path);
    }
}
