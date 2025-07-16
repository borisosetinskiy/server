
package com.ob.server;

import com.ob.server.security.SecurityHandler;
import com.ob.server.session.RequestSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.NettyRuntime;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public final class ServerConfig {
    @Getter
    private final int port;
    @Getter
    private final RequestSessionFactory requestSessionFactory;
    @Getter
    @Setter
    MeterService meterService = new MeterService() {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public int incrementCounter(String counterName, String... tags) {
            return counter.incrementAndGet();
        }

        @Override
        public int decrementCounter(String counterName, String... tags) {
            return counter.incrementAndGet();
        }

        @Override
        public void record(String key, long start, String... tags) {

        }
    };
    @Getter
    @Setter
    private long readerIdleTime = 60000;
    @Getter
    @Setter
    private long writerIdleTime = 300000;
    @Getter
    @Setter
    private long allIdleTime = 300000;
    @Getter
    private SslContext sslCtx;
    @Getter
    @Setter
    private boolean chunked;
    @Setter
    @Getter
    private int receiveBuffer = 32 * 1024;
    @Setter
    @Getter
    private int sendBuffer = 64 * 1024;
    @Setter
    @Getter
    private int writeBufferWaterMarkLow = 8 * 1024;
    @Setter
    @Getter
    private int writeBufferWaterMarkHigh = 32 * 1024;
    @Setter
    @Getter
    private int bossNumber = 1;
    @Setter
    @Getter
    private int workNumber = NettyRuntime.availableProcessors() * 3;
    @Setter
    @Getter
    private ChannelHandlerFactory channelHandlerFactory;
    private boolean epoll;
    @Setter
    @Getter
    private CorsConfig corsConfig;
    private Supplier<ChannelHandler[]> handlers = () -> null;
    @Setter
    @Getter
    private SecurityHandler securityHandler;
    @Setter
    private Supplier<ChannelHandler> errorHandler;

    public ServerConfig(int port, RequestSessionFactory requestSessionFactory) {
        this.port = port;
        this.requestSessionFactory = requestSessionFactory;
    }

    public ChannelHandler getErrorHandler() {
        return errorHandler.get();
    }

    public ChannelHandler[] getHandlers() {
        return handlers.get();
    }

    public ServerConfig setHandlers(Supplier<ChannelHandler[]> handlers) {
        this.handlers = handlers;
        return this;
    }


    public boolean isSsl() {
        return sslCtx != null;
    }

    public void setCertificate(String key, String cert) {
        assert key != null;
        assert cert != null;
        Path keyPath = Paths.get(key);
        Path certPath = Paths.get(cert);
        if (!Files.exists(keyPath)
                || !Files.exists(certPath)
        ) throw new RuntimeException("Key or cert file not found");

        File certFile = certPath.toFile();
        File keyFile = keyPath.toFile();
        try {
            this.sslCtx = SslContextBuilder.forServer(certFile, keyFile).build();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }


}

