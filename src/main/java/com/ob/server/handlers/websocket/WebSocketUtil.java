/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
 *  io.netty.handler.codec.http.websocketx.WebSocketHandshakeException
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.slf4j.Logger
 */
package com.ob.server.handlers.websocket;

import com.ob.server.ServerLogger;
import com.ob.server.error.ForbiddenException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

public class WebSocketUtil {
    public static void onError(ChannelHandlerContext ctx, Throwable cause) {
        if (cause != null) {
            int code = 1011;
            try {

                String message = cause.getMessage();
                if (cause instanceof ForbiddenException) {
                   code = 4403;
                } else if (cause instanceof UnsupportedOperationException) {
                   code = 1010;
                }
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(code, message)).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
            }
            ServerLogger.loggerProblem.error(
                    String.format("CLOSE webSocket, channel %s, code %s error: "
                            , ctx.channel().id().asShortText(), code)
                    , cause);
        }
    }
}

