/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslContextBuilder
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.util.Assert
 */
package com.ob.server;

import com.ob.server.security.SecurityHandler;
import com.ob.server.session.RequestSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.NettyRuntime;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public final class ServerConfig {
    private SslContext sslCtx;
    private File certFile;
    private File keyFile;
    private int receiveBuffer = 32 * 1024;
    private int sendBuffer = 64 * 1024;
    private int writeBufferWaterMarkLow = 8 * 1024;
    private int writeBufferWaterMarkHigh = 32 * 1024;
    private int bossNumber = 1;
    private int workNumber = NettyRuntime.availableProcessors() * 3;
    private ChannelHandlerFactory channelHandlerFactory;
    private boolean epoll;
    private CorsConfig corsConfig;
    private Supplier<ChannelHandler[]> handlers;
    private int port;
    private RequestSessionFactory requestSessionFactory;
    private SecurityHandler securityHandler;
    private Supplier<ChannelHandler> errorHandler;

    public ChannelHandler getErrorHandler() {
        return errorHandler.get();
    }

    public void setErrorHandler(Supplier<ChannelHandler> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ServerConfig(int port, RequestSessionFactory requestSessionFactory) {
        this.port = port;
        this.requestSessionFactory = requestSessionFactory;
    }

    public CorsConfig getCorsConfig() {
        return corsConfig;
    }

    public ServerConfig setCorsConfig(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
        return this;
    }

    public ServerConfig setWorkNumber(int workNumber) {
        this.workNumber = workNumber;
        return this;
    }

    public boolean isEpoll() {
        return epoll;
    }

    public ServerConfig setEpoll() {
        this.epoll = true;
        return this;
    }



    public ChannelHandler[] getHandlers() {
        return handlers.get();
    }

    public ServerConfig setHandlers(Supplier<ChannelHandler[]> handlers) {
        this.handlers = handlers;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RequestSessionFactory getRequestSessionFactory() {
        return requestSessionFactory;
    }


    public SecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    public ServerConfig setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
        return this;
    }

    public SslContext getSslCtx() {
        return this.sslCtx;
    }

    public boolean isSsl() {
        return sslCtx != null;
    }

    public void setCertificate(String key, String cert) {
        Path keyPath = Paths.get(key);
        Path certPath = Paths.get(cert);
        if (cert == null || key == null
                || !Files.exists(keyPath)
                || !Files.exists(certPath)
        ) throw new RuntimeException();

        this.certFile = certPath.toFile();
        this.keyFile = keyPath.toFile();
        try {
            this.sslCtx = SslContextBuilder.forServer(this.certFile, this.keyFile).build();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public com.ob.server.ChannelHandlerFactory getChannelHandlerFactory() {
        return channelHandlerFactory;
    }

    public ServerConfig setChannelHandlerFactory(com.ob.server.ChannelHandlerFactory channelHandlerFactory) {
        this.channelHandlerFactory = channelHandlerFactory;
        return this;
    }

    public int getReceiveBuffer() {
        return receiveBuffer;
    }

    public ServerConfig setReceiveBuffer(int receiveBuffer) {
        this.receiveBuffer = receiveBuffer;
        return this;
    }

    public int getSendBuffer() {
        return sendBuffer;
    }

    public ServerConfig setSendBuffer(int sendBuffer) {
        this.sendBuffer = sendBuffer;
        return this;
    }

    public int getWriteBufferWaterMarkLow() {
        return writeBufferWaterMarkLow;
    }

    public ServerConfig setWriteBufferWaterMarkLow(int writeBufferWaterMarkLow) {
        this.writeBufferWaterMarkLow = writeBufferWaterMarkLow;
        return this;
    }

    public int getWriteBufferWaterMarkHigh() {
        return writeBufferWaterMarkHigh;
    }

    public ServerConfig setWriteBufferWaterMarkHigh(int writeBufferWaterMarkHigh) {
        this.writeBufferWaterMarkHigh = writeBufferWaterMarkHigh;
        return this;
    }

    public int getBossNumber() {
        return bossNumber;
    }

    public ServerConfig setBossNumber(int bossNumber) {
        this.bossNumber = bossNumber;
        return this;
    }

    public int getWorkNumber() {
        return workNumber;
    }


}

