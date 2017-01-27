package com.ob.server.http;

import com.ob.server.ServerConfig;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by boris on 19.04.2016.
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

	ServerConfig config;
	ResponderResolver responderResolver;
	ChannelGroup allChannels;

    public HttpChannelInitializer(ServerConfig config, ResponderResolver responderResolver, ChannelGroup allChannels) {
    	this.config = config;
    	this.responderResolver = responderResolver;
    	this.allChannels = allChannels;

    }
    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();
        if(config.isSsl()){
            pipeline.addLast(config.getSslCtx().newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        if(config.isCors()){
            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
            pipeline.addLast(new CorsHandler(corsConfig));
        }
        pipeline.addLast(new DefaultHttpServerHandler(responderResolver, allChannels));
        pipeline.addLast(new IdleStateHandler(0, config.getWriteTimeoutSeconds(), config.getAllTimeoutSeconds(), TimeUnit.SECONDS));
    }
}
