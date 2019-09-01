package com.ob.server;

import com.ob.server.session.RequestService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;

public interface ChannelHandlerFactory {
    ChannelHandler[] create(ChannelPipeline pipeline
            , RequestService requestService
            , ChannelGroup channels);
}
