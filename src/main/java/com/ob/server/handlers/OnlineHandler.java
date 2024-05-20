package com.ob.server.handlers;

import com.ob.server.MeterService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = false)
@Slf4j
@ChannelHandler.Sharable
@Data
public class OnlineHandler extends ChannelInboundHandlerAdapter {
    final MeterService meterService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asShortText();
        log.info("Session {}. online. Sessions {}"
                , sessionId
                , meterService.incrementCounter("online"));
        ctx.fireChannelRegistered();
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String sessionId = ctx.channel().id().asShortText();

        log.info("Session {}, offline. Sessions {}"
                , sessionId
                , meterService.decrementCounter("online"));

        ctx.fireChannelUnregistered();
    }
}
