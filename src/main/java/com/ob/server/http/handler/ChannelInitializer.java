package com.ob.server.http.handler;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.ConcurrentMap;

@Sharable
public abstract class ChannelInitializer extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(io.netty.channel.ChannelInitializer.class);
    private final ConcurrentMap<ChannelHandlerContext, Boolean> initMap = PlatformDependent.newConcurrentHashMap();

    public ChannelInitializer() {
    }

    protected abstract void doInitChannel(ChannelHandlerContext ctx) throws Exception;

    public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.initChannel(ctx)) {
            ctx.pipeline().fireChannelRegistered();
        } else {
            ctx.fireChannelRegistered();
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
        ctx.close();
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isRegistered()) {
            this.initChannel(ctx);
        }

    }

    private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
        if (this.initMap.putIfAbsent(ctx, Boolean.TRUE) == null) {
            try {
                this.doInitChannel(ctx);
            } catch (Throwable var6) {
                this.exceptionCaught(ctx, var6);
            } finally {
                this.remove(ctx);
            }

            return true;
        } else {
            return false;
        }
    }

    private void remove(ChannelHandlerContext ctx) {
        try {
            ChannelPipeline pipeline = ctx.pipeline();
            if (pipeline.context(this) != null) {
                pipeline.remove(this);
            }
        } finally {
            this.initMap.remove(ctx);
        }

    }
}
