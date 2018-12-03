/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sun.misc.BASE64Decoder;

import java.util.List;

public class BasicAuthentication extends AuthenticationHandler {
    private static final String BASIC_AUTHENTICATION_REGEX = "Basic\\s";
    private static final String EMPTY_STRING = "";
    private static final String USERNAME_PASSWORD_SEPARATOR = ":";
    private static final String WWW_AUTHENTICATE_HEADER_VALUE = "Basic realm=\"Default realm\"";
    private static final BASE64Decoder DECODER = new BASE64Decoder();

    public BasicAuthentication(Access access) {
        super(access);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof HttpRequest) {
            Object2ObjectArrayMap params = HttpUtils.params((HttpObject)o, null);
            if (!params.containsKey("Authorization")) {
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            String authHeader = (String)params.get("Authorization");
            authHeader = authHeader.replaceFirst(BASIC_AUTHENTICATION_REGEX, EMPTY_STRING);
            String[] creds = (new String(DECODER.decodeBuffer(authHeader))).split(USERNAME_PASSWORD_SEPARATOR);
            String username = creds[0];
            if (!this.access.check(new UserDetails(username, creds[1]))) {
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                response.headers().add("WWW-Authenticate", WWW_AUTHENTICATE_HEADER_VALUE);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        list.add(ReferenceCountUtil.retain(o));
    }
}

