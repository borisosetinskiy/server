package com.ob.server.http.handler;

import com.ob.server.resolvers.ChannelHandlerResolvers;

import java.util.Map;

public class MapChannelHandlerResolvers implements ChannelHandlerResolvers {
    private final Map<String, ChannelHandlerFactory> channelHandlerCreators;

    public MapChannelHandlerResolvers(Map<String, ChannelHandlerFactory> channelHandlerCreators) {
        this.channelHandlerCreators = channelHandlerCreators;
    }

    @Override
    public ChannelHandlerFactory resolve(String path) {
         return channelHandlerCreators.get(path);
    }
}
