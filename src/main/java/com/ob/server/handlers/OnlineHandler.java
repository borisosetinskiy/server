package com.ob.server.handlers;

import com.ob.server.ServerLogger;
import io.netty.channel.*;

import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class OnlineHandler extends ChannelInboundHandlerAdapter {
    private AtomicLong online = new AtomicLong();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asShortText();
        ServerLogger.logger.info("Session {}. online. Sessions {}"
                , sessionId
                , online.incrementAndGet());
        ctx.fireChannelRegistered();
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().id().asShortText();

        ServerLogger.logger.info("Session {}, offline. Sessions {}"
                , sessionId
                , online.decrementAndGet());

        ctx.fireChannelUnregistered();
    }
}
