package com.ob.server;

import com.ob.server.http.RequestSessionHttpServerHandler;
import com.ob.server.session.RequestService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;

public class HttpChannelHandlerFactory implements ChannelHandlerFactory {
    @Override
    public ChannelHandler[] create(ChannelPipeline pipeline, RequestService requestService, ChannelGroup channels) {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
        return new ChannelHandler[]{
                new CorsHandler(corsConfig)
                , new RequestSessionHttpServerHandler(requestService, channels)
        };
    }
}
