
package com.ob.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

public interface InitializerFactory {
    public ChannelInitializer createInitializer(ServerConfig var1, ChannelGroup var2);
}

