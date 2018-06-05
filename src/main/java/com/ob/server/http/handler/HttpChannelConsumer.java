package com.ob.server.http.handler;

import com.ob.server.resolvers.ChannelHandlerResolvers;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.util.function.Consumer;

public class HttpChannelConsumer implements Consumer<ChannelHandlerContext> {

    private final ResponderResolver responderResolver;
    private final ChannelHandlerResolvers channelHandlerResolvers;
    public HttpChannelConsumer(ResponderResolver responderResolver, ChannelHandlerResolvers channelHandlerResolvers) {
        this.responderResolver = responderResolver;
        this.channelHandlerResolvers = channelHandlerResolvers;
    }


    @Override
    public void accept(ChannelHandlerContext channelHandlerContext) {
        ChannelPipeline pipeline = channelHandlerContext.channel().pipeline();
        pipeline.addLast(new ChannelRequestHandler(responderResolver, channelHandlerResolvers));
    }
}
