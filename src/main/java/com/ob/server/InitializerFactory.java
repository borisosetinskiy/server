package com.ob.server;


import com.ob.server.http.handler.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

/**
 * Created by boris on 06.04.2016.
 */
public interface InitializerFactory {
    ChannelInitializer createInitializer(ServerConfig options, ChannelGroup allChannels);
}
