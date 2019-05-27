/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.group.ChannelGroup
 *  io.netty.handler.codec.http.HttpObject
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  org.slf4j.Logger
 */
package com.ob.server.http;

import com.ob.server.*;
import com.ob.server.session.RequestService;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObject;

public class RequestSessionHttpServerHandler
extends SimpleChannelInboundHandler<Object> {
    public final RequestService requestService;
    public final ChannelGroup allChannels;
    private HttpData httpData = HttpData.EMPTY;

    public RequestSessionHttpServerHandler(RequestService requestService, ChannelGroup allChannels) {
        this.requestService = requestService;
        this.allChannels = allChannels;
    }

    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof HttpObject) {
                this.httpData = HttpDataImpl.collectData(msg, this.httpData);
            }
            if (this.httpData != HttpData.EMPTY && this.httpData.finished()) {
                RequestSession requestSession = requestService.process(new ChannelRequestDto(ctx, httpData.context()));
                if (requestSession == null) {
                    throw new AccessException();
                }
            }
        }
        catch (Exception var4) {
            ServerLogger.loggerProblem.error(String.format("Operation read, channel %s, error %s ", ctx.channel().id().asShortText(), PrintUtil.fromStack(var4)));
        }
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s registered.", ctx.channel().id().asShortText()));
        ChannelUtil.gather(ctx, this.allChannels);
        ctx.fireChannelRegistered();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ServerLogger.loggerProblem.error(String.format("Channel %s, error %s ", ctx.channel().id().asShortText(), PrintUtil.fromStack(cause)));
        ctx.fireExceptionCaught(cause);
        this.httpData =  HttpData.EMPTY ;
    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s unregistered.", ctx.channel().id().asShortText()));
        ctx.fireChannelUnregistered();
        this.httpData =  HttpData.EMPTY ;
    }
}
