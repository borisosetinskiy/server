package com.ob.server;

import com.ob.server.handlers.AgentHandler;
import com.ob.server.handlers.AuthenticationHandler;
import com.ob.server.handlers.ProcessHandler;
import com.ob.server.handlers.websocket.ErrorHandler;
import com.ob.server.session.*;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Server {
    private final ChannelGroup allChannels
            = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;
    private ServerConfig serverConfig;

    public Server(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public Server(int port, RequestSessionFactory requestSessionFactory) {
        this.serverConfig = new ServerConfig(port, requestSessionFactory);
    }

    public Server start() {
        bossGroup = serverConfig.isEpoll()
                ? new EpollEventLoopGroup(serverConfig.getBossNumber())
                : new NioEventLoopGroup(serverConfig.getBossNumber());
        workerGroup = serverConfig.isEpoll()
                ? new EpollEventLoopGroup(serverConfig.getWorkNumber())
                : new NioEventLoopGroup(serverConfig.getWorkNumber());
        ServerBootstrap bootstrap = new ServerBootstrap();
        RequestService requestService = new RequestServiceImpl(serverConfig
                .getRequestSessionFactory());
        ((((bootstrap.group(bossGroup, workerGroup)
                .channel(serverConfig.isEpoll()
                        ? EpollServerSocketChannel.class
                        : NioServerSocketChannel.class))
                .handler(new LoggingHandler(LogLevel.DEBUG)))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (serverConfig.isSsl()) {
                            pipeline.addLast("ssl"
                                    , serverConfig.getSslCtx()
                                            .newHandler(socketChannel.alloc()));
                        }
                        pipeline.addLast("http", new HttpServerCodec());
                        pipeline.addLast("agent", new AgentHandler());
                        if (serverConfig.getAuthenticationHandler() != null) {
                            pipeline.addLast("authentication"
                                    , serverConfig.getAuthenticationHandler());
                        }
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));

                        pipeline.addLast("idle"
                                , new IdleStateHandler(0L
                                        , 300L
                                        , 300L
                                        , TimeUnit.SECONDS));

                        if (serverConfig.getChannelHandlerFactory() != null) {
                            pipeline.addLast(
                                    serverConfig.getChannelHandlerFactory()
                                            .create(pipeline
                                                    , requestService
                                                    , allChannels));
                        }
                        ChannelHandler[] handlers = serverConfig.getHandlers();
                        if (handlers != null) {
                            pipeline.addLast(handlers);
                        }
                        pipeline.addLast(new ProcessHandler());
                        if (serverConfig.getErrorHandler() != null)
                            pipeline.addLast(serverConfig.getErrorHandler());

                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true))
                .option(ChannelOption.SO_BACKLOG, 1024))
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBuffer())
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBuffer())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK
                        , new WriteBufferWaterMark(
                                serverConfig.getWriteBufferWaterMarkLow()
                                , serverConfig.getWriteBufferWaterMarkHigh()))
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        this.future = bootstrap.bind(serverConfig.getPort());
        this.allChannels.add(this.future.channel());
        return this;
    }

    public void stop() throws Exception {
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
    }

    public static class ServerBuilder {
        private ServerConfig serverConfig;

        public ServerBuilder(int port, RequestSessionFactory requestSessionFactory) {
            serverConfig = new ServerConfig(port, requestSessionFactory);
        }

        public ServerBuilder setCertificate(String key, String cert) {
            serverConfig.setCertificate(key, cert);
            return this;
        }

        public ServerBuilder setChannelHandlerFactory(com.ob.server.ChannelHandlerFactory channelHandlerFactory) {
            serverConfig.setChannelHandlerFactory(channelHandlerFactory);
            return this;
        }

        public ServerBuilder setReceiveBuffer(int receiveBuffer) {
            serverConfig.setReceiveBuffer(receiveBuffer);
            return this;
        }

        public ServerBuilder setSendBuffer(int sendBuffer) {
            serverConfig.setSendBuffer(sendBuffer);
            return this;
        }

        public ServerBuilder setWriteBufferWaterMarkLow(int writeBufferWaterMarkLow) {
            this.serverConfig.setWriteBufferWaterMarkLow(writeBufferWaterMarkLow);
            return this;
        }

        public ServerBuilder setWriteBufferWaterMarkHigh(int writeBufferWaterMarkHigh) {
            this.serverConfig.setWriteBufferWaterMarkHigh(writeBufferWaterMarkHigh);
            return this;
        }

        public ServerBuilder setBossNumber(int bossNumber) {
            this.serverConfig.setBossNumber(bossNumber);
            return this;
        }

        public ServerBuilder setWorkNumber(int workNumber) {
            this.serverConfig.setWorkNumber(workNumber);
            return this;
        }

        public ServerBuilder setWebsocket() {
            this.serverConfig.setChannelHandlerFactory(
                    new WebSocketChannelHandlerFactory());
            this.serverConfig.setErrorHandler(ErrorHandler::new);
            return this;
        }

        public ServerBuilder setHttp() {
            this.serverConfig.setChannelHandlerFactory(
                    new HttpChannelHandlerFactory());
            return this;
        }

        public ServerBuilder setEpoll() {
            this.serverConfig.setEpoll();
            return this;
        }

        public ServerBuilder setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
            this.serverConfig.setAuthenticationHandler(authenticationHandler);
            return this;
        }

        public ServerBuilder setHandlers(Supplier<ChannelHandler[]> handlers) {
            this.serverConfig.setHandlers(handlers);
            return this;
        }


        public Server build() {
            return new Server(serverConfig);
        }


    }


}

