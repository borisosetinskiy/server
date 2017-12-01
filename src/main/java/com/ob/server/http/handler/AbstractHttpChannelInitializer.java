package com.ob.server.http.handler;

import com.ob.server.ServerConfig;
import com.ob.server.http.AgentHandler;
import com.ob.server.http.AuthenticationFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by boris on 19.04.2016.
 */
public abstract class AbstractHttpChannelInitializer extends ChannelInitializer {

	protected final ServerConfig config;
    protected final ChannelGroup allChannels;
    protected final AuthenticationFactory authenticationFactory;
    protected AbstractHttpChannelInitializer(ServerConfig config,  ChannelGroup allChannels, AuthenticationFactory authenticationFactory) {
    	this.config = config;
       	this.allChannels = allChannels;
        this.authenticationFactory = authenticationFactory;
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
        if(config.isWithCompressor())
            pipeline.addLast("compressor", new HttpContentCompressor());
        if(config.isWithAggregator())
            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        if(config.isWithIdle())
            pipeline.addLast("idle", new IdleStateHandler(0, config.getWriteTimeoutSeconds(), config.getAllTimeoutSeconds(), TimeUnit.SECONDS));
        last(ctx);
        allChannels.add(ctx.channel());
    }

    protected abstract void last(ChannelHandlerContext ctx);
}
