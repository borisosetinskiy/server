/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.FullHttpRequest
 *  io.netty.handler.codec.http.HttpConstants
 *  io.netty.handler.codec.http.HttpHeaderNames
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMethod
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
 *  io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
 *  io.netty.handler.ssl.SslHandler
 *  io.netty.util.AsciiString
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 */
package com.ob.server.handlers.websocket;

import com.ob.server.RequestUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;

public class WebSocketServerProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadSize;
    private final boolean allowMaskMismatch;

    WebSocketServerProtocolHandshakeHandler(String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof  FullHttpRequest){
            final FullHttpRequest req = (FullHttpRequest)msg;
            int pathEndPos = req.uri().indexOf(63);
            final String path = RequestUtil.decodeComponent(pathEndPos < 0 ? req.uri() : req.uri().substring(0, pathEndPos), HttpConstants.DEFAULT_CHARSET);
            {
                ctx.fireChannelRead(req.retain());
                try {
                    if (req.method() != HttpMethod.GET) {
                        ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                                , HttpResponseStatus.FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
                        return;
                    }
                    WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                            WebSocketServerProtocolHandshakeHandler.getWebSocketLocation(ctx.pipeline()
                                    , req
                                    , path)
                            , this.subprotocols, this.allowExtensions, this.maxFramePayloadSize, this.allowMaskMismatch);
                    final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                    } else {
                        ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                        handshakeFuture.addListener(future -> {
                            if (!future.isSuccess()) {
                                ctx.fireExceptionCaught(future.cause());
                            } else {
//                            ctx.fireUserEventTriggered(WebSocketServerHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                                ctx.fireUserEventTriggered(new WebSocketServerHandler.HandshakeComplete(req.uri()
                                        , req.headers(), handshaker.selectedSubprotocol()));
                            }
                        });
                        WebSocketServerHandler.setHandshaker(ctx.channel(), handshaker);
                        ctx.pipeline().replace(this, "WS403Responder", WebSocketServerHandler.forbiddenHttpRequestResponder());
                    }
                }
                finally {
                    req.release();
                }
            }
        }else {
            ctx.fireChannelRead(msg);
        }

    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        return protocol + "://" + req.headers().get(HttpHeaderNames.HOST) + path;
    }

}

