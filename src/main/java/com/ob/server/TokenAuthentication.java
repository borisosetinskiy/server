/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.List;

@ChannelHandler.Sharable
public class TokenAuthentication
extends AuthenticationHandler {
    public TokenAuthentication(Access access) {
        super(access);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof HttpRequest) {
            Object2ObjectArrayMap params = HttpUtils.params((HttpRequest)o, null);
            Object token = params.get("token");
            if (token == null || !this.access.check(token)) {
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED
                        , Unpooled.wrappedBuffer("Wrong token.".getBytes())))
                        .addListener(ChannelFutureListener.CLOSE);
                ServerLogger.loggerProblem.error(String.format("Channel %s, UNAUTHORIZED", ctx.channel().id().asShortText())
                        );
                return;
            }
        }
        list.add(ReferenceCountUtil.retain(o));
    }
}

