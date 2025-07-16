
package com.ob.server;

import io.netty.channel.group.ChannelGroup;

public interface ServerShutdown {
    void shutDown();

    void setChannelGroup(ChannelGroup var1);
}

