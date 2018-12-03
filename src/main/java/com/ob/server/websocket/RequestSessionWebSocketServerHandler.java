/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.channel.group.ChannelGroup
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  org.slf4j.Logger
 */
package com.ob.server.websocket;

import com.ob.server.*;

import com.ob.server.session.RequestService;
import com.ob.server.session.RequestSession;
import com.ob.server.websocket.WebSocketServerHandler;
import com.ob.server.websocket.WebSocketUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObject;

import java.util.List;

public class RequestSessionWebSocketServerHandler
extends WebSocketServerHandler {
    public final RequestService requestService;
    public final ChannelGroup allChannels;
    private HttpData httpData = HttpData.EMPTY;

    public RequestSessionWebSocketServerHandler(RequestService requestService, ChannelGroup allChannels) {
        super();
        this.requestService = requestService;
        this.allChannels = allChannels;
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            if (evt instanceof WebSocketServerHandler.HandshakeComplete
                    && this.httpData != HttpData.EMPTY) {
                RequestSession requestSession = requestService.process(new ChannelRequestDto(ctx, httpData.context()));
                if (requestSession == null) {
                    throw new AccessException();
                }
                ctx.channel().attr(AttributeKeys.REQUEST_SESSION_ATTR_KEY).set(requestSession);
            }
            ctx.fireUserEventTriggered(evt);
        }
        catch (Exception var4) {
            WebSocketUtil.onError(ctx, var4);
        }
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s registered.", ctx.channel().id().asShortText()));
        ChannelUtil.gather(ctx, this.allChannels);
        ctx.fireChannelRegistered();
    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s unregistered.", ctx.channel().id().asShortText()));
        ctx.fireChannelUnregistered();
        this.httpData = HttpData.EMPTY;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        try {
            if (o instanceof HttpObject) {
                this.httpData = HttpDataImpl.collectData(o, this.httpData);
            }
            super.decode(ctx, o, list);
        }catch (Exception var5) {
            WebSocketUtil.onError(ctx, var5);
        }
    }
}

