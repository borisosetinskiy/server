package com.ob.server.handlers;

import com.ob.server.MeterService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@EqualsAndHashCode(callSuper = false)
@Slf4j
@ChannelHandler.Sharable
@Data
public class OnlineHandler extends ChannelInboundHandlerAdapter {
    final MeterService meterService;
    AtomicLong online = new AtomicLong(0);
    AtomicLong offline = new AtomicLong(0);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asShortText();
        online.getAndSet(meterService.incrementCounter("online"));
        log.info("Session {}. online. Sessions {}"
                , sessionId
                , online());
        ctx.fireChannelRegistered();
    }

    Long online() {
        return online.get() - offline.get();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().id().asShortText();
        offline.getAndSet(meterService.decrementCounter("online"));
        log.info("Session {}, offline. Sessions {}"
                , sessionId
                , online());

        ctx.fireChannelUnregistered();
    }
}
