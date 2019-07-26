/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.bootstrap.AbstractBootstrap
 *  io.netty.bootstrap.ServerBootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.WriteBufferWaterMark
 *  io.netty.channel.group.ChannelGroup
 *  io.netty.channel.group.ChannelGroupFuture
 *  io.netty.channel.group.DefaultChannelGroup
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioServerSocketChannel
 *  io.netty.handler.logging.LogLevel
 *  io.netty.handler.logging.LoggingHandler
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GlobalEventExecutor
 *  org.springframework.beans.factory.annotation.Required
 */
package com.ob.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ob.server.session.*;
import com.ob.server.websocket.RequestSessionWebSocketServerHandler;
import com.ob.server.websocket.TextWebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NettyServer {

    private final ServerConfig config = new ServerConfig();
    private RequestService requestService;
    private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;
    private ServerShutdown serverShutdown;
    private final int port;
    private AuthenticationHandler authenticationHandler;
    private AgentHandler agentHandler = new AgentHandler();
    private boolean withAgentHandler;
    private HeartBeatFactory heartBeatFactory;
    private final RequestSessionFactory requestSessionFactory;
    private HeartBeatService heartBeatService;
    private boolean epoll;

    public NettyServer setEpoll(boolean epoll) {
        this.epoll = epoll;
        return this;
    }

    public NettyServer(int port, RequestSessionFactory requestSessionFactory) {
        this.port = port;
        this.requestSessionFactory = requestSessionFactory;
    }

    public NettyServer setServerShutdown(ServerShutdown serverShutdown) {
        this.serverShutdown = serverShutdown;
        return this;
    }

    public NettyServer setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
        return this;
    }

    public NettyServer withAgentHandler() {
        this.withAgentHandler = true;
        return this;
    }

    public NettyServer setHeartBeatFactory(HeartBeatFactory heartBeatFactory) {
        this.heartBeatFactory = heartBeatFactory;
        return this;
    }

    public NettyServer start() {
        bossGroup = epoll? new EpollEventLoopGroup(config.getBossNumber()) : new NioEventLoopGroup(config.getBossNumber());
        workerGroup = epoll? new EpollEventLoopGroup(config.getWorkNumber()) : new NioEventLoopGroup(config.getWorkNumber());
        ServerBootstrap bootstrap = new ServerBootstrap();
        if (this.serverShutdown != null) {
            this.serverShutdown.setChannelGroup(this.allChannels);
        }
        if (heartBeatFactory != null) {
            heartBeatService = new HeartBeatServiceImpl(
                    Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                            .setDaemon(true).setNameFormat("Ping-Pong-%d").build()), heartBeatFactory);
            heartBeatService.start();
        }
        requestService = new RequestServiceImpl(requestSessionFactory, heartBeatService);
        ((((bootstrap.group(bossGroup, workerGroup)
                .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class))
                .handler(new LoggingHandler(LogLevel.DEBUG)))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (config.isSsl()) {
                            pipeline.addLast("ssl", config.getSslCtx().newHandler(socketChannel.alloc()));
                        }
                        pipeline.addLast("http", new HttpServerCodec());
                        if (withAgentHandler) {
                            pipeline.addLast("agent", agentHandler);
                        }
                        if (authenticationHandler != null) {
                            pipeline.addLast("authentication", authenticationHandler);
                        }
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        if(config.getChannelHandlerFactory() != null){
                            pipeline.addLast( config.getChannelHandlerFactory().create(pipeline, requestService, allChannels));
                        }
                        pipeline.addLast("idle", new IdleStateHandler(0L
                                , 300l
                                , 300l
                                , TimeUnit.SECONDS));
                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true))
                .option(ChannelOption.SO_BACKLOG, 1024))
                .childOption(ChannelOption.SO_RCVBUF, config.getReceiveBuffer())
                .childOption(ChannelOption.SO_SNDBUF, config.getSendBuffer())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK
                        , new WriteBufferWaterMark(config.getWriteBufferWaterMarkLow(), config.getWriteBufferWaterMarkHigh()))
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        this.future = bootstrap.bind(port);
        this.allChannels.add(this.future.channel());
        return this;
    }

    public void stop() throws Exception {
        if (this.serverShutdown != null) {
            this.serverShutdown.shutDown();
        }
        try {
            ChannelGroupFuture future = this.allChannels.close();
            future.awaitUninterruptibly();
        } catch (Exception ignored) {
            // empty catch block
        }
        try {
            if (!bossGroup.isShutdown()) {
                bossGroup.shutdownGracefully();
            }
        } catch (Exception ignored) {
            // empty catch block
        }
        try {
            if (!workerGroup.isShutdown()) {
                workerGroup.shutdownGracefully();
            }
        } catch (Exception ignored) {
            // empty catch block
        }
        if (heartBeatService != null) {
            try {
                heartBeatService.stop();
            } catch (Exception e) {
            }

        }
    }


    public static NettyServer create(int port, RequestSessionFactory requestSessionFactory) {
        return new NettyServer(port, requestSessionFactory);
    }

    public NettyServer setCertificate(String key, String cert) {
        config.setCertificate(key, cert);
        return this;
    }

    public NettyServer setBossNumber(int bossNumber) {
        config.setBossNumber(bossNumber);
        return this;
    }

    public NettyServer setWorkNumber(Integer workNumber) {
        config.setWorkNumber(workNumber);
        return this;
    }
    public NettyServer setChannelHandlerFactory(com.ob.server.ChannelHandlerFactory channelHandlerFactory) {
        config.setChannelHandlerFactory(channelHandlerFactory);
        return this;
    }
    public NettyServer setWebsocket() {
        config.setChannelHandlerFactory(new WebSocketChannelHandlerFactory());
        return this;
    }
    public NettyServer setHttp() {
        config.setChannelHandlerFactory(new HttpChannelHandlerFactory());
        return this;
    }

    public ServerConfig getConfig() {
        return config;
    }

}

