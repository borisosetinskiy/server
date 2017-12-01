package com.ob.server.http.handler;

import com.ob.server.ServerConfig;
import com.ob.server.http.AuthenticationFactory;
import com.ob.server.resolvers.ChannelHandlerResolver;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;

public class DefaultHttpChannelInitializer extends AbstractHttpChannelInitializer {

    private final ResponderResolver responderResolver;
    private final ChannelHandlerResolver channelHandlerResolver;
    public DefaultHttpChannelInitializer(ServerConfig config, ResponderResolver responderResolver, ChannelGroup allChannels, AuthenticationFactory authenticationFactory, ChannelHandlerResolver channelHandlerResolver) {
        super(config, allChannels, authenticationFactory);
        this.responderResolver = responderResolver;
        this.channelHandlerResolver = channelHandlerResolver;
    }

    @Override
    protected void last(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        pipeline.addLast(new ResolverHandler(ctx, responderResolver, channelHandlerResolver));
    }
}
