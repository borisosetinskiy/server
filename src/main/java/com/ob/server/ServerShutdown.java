/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.channel.group.ChannelGroup
 */
package com.ob.server;

import io.netty.channel.group.ChannelGroup;

public interface ServerShutdown {
    void shutDown();

    void setChannelGroup(ChannelGroup var1);
}

