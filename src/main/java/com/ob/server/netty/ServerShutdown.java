package com.ob.server.netty;

import io.netty.channel.group.ChannelGroup;

/**
 * Created by boris on 4/11/2017.
 */
public interface ServerShutdown {
    void shutDown();
    void setChannelGroup(ChannelGroup group);
}
