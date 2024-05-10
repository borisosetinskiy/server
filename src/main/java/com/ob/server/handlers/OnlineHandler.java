package com.ob.server.handlers;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ChannelHandler.Sharable
public class OnlineHandler extends ChannelInboundHandlerAdapter {
    private final AtomicLong online = new AtomicLong();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asShortText();
        log.info("Session {}. online. Sessions {}"
                , sessionId
                , online.incrementAndGet());
        ctx.fireChannelRegistered();
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().id().asShortText();

        log.info("Session {}, offline. Sessions {}"
                , sessionId
                , online.decrementAndGet());

        ctx.fireChannelUnregistered();
    }
}
