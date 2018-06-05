package com.ob.server.http.handler;

import com.ob.server.ServerConfig;
import com.ob.server.http.AgentHandler;
import com.ob.server.http.AuthenticationFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by boris on 19.04.2016.
 */
public class HttpChannelInitializer extends ChannelInitializer {

	private final ServerConfig config;
    private final ChannelGroup allChannels;
    private final AuthenticationFactory authenticationFactory;
    private final Consumer<ChannelHandlerContext> consumer;
    public HttpChannelInitializer(ServerConfig config, ChannelGroup allChannels, AuthenticationFactory authenticationFactory, Consumer<ChannelHandlerContext> consumer) {
    	this.config = config;
       	this.allChannels = allChannels;
        this.authenticationFactory = authenticationFactory;
        this.consumer = consumer;
    }
    @Override
    public void doInitChannel(ChannelHandlerContext ctx)  {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        if(config.isSsl()){
            pipeline.addLast("ssl", config.getSslCtx().newHandler(ctx.channel().alloc()));
        }
        pipeline.addLast("http", new HttpServerCodec());
        if(config.isCors()){
            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
            pipeline.addLast("cors", new CorsHandler(corsConfig));
        }
        pipeline.addLast("agent", new AgentHandler());
        if(authenticationFactory!=null){
            pipeline.addLast("authentication", authenticationFactory.create());
        }
        pipeline.addLast(new HttpObjectAggregator(65536));
        if(consumer!=null)
            consumer.accept(ctx);
        pipeline.addLast("idle", new IdleStateHandler(0, config.getWriteTimeoutSeconds(), config.getAllTimeoutSeconds(), TimeUnit.SECONDS));
        allChannels.add(ctx.channel());
    }

}
