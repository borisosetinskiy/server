/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.FullHttpRequest
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
 *  io.netty.handler.codec.http.websocketx.PingWebSocketFrame
 *  io.netty.handler.codec.http.websocketx.PongWebSocketFrame
 *  io.netty.handler.codec.http.websocketx.Utf8FrameValidator
 *  io.netty.handler.codec.http.websocketx.WebSocketFrame
 *  io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.slf4j.Logger
 */
package com.ob.server.websocket;

import com.ob.server.ServerLogger;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;

import java.util.List;

public class WebSocketServerHandler
        extends MessageToMessageDecoder<Object> {
    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, (String) "HANDSHAKER");
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadLength;
    private final boolean allowMaskMismatch;

    public WebSocketServerHandler() {
        this(null, true);
    }

    public WebSocketServerHandler(String subprotocols) {
        this(subprotocols, true);
    }

    public WebSocketServerHandler(String subprotocols, boolean allowExtensions) {
        this(subprotocols, allowExtensions, 65536);
    }

    public WebSocketServerHandler(String subprotocols, boolean allowExtensions, int maxFrameSize) {
        this(subprotocols, allowExtensions, maxFrameSize, false);
    }

    public WebSocketServerHandler(String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    protected void decode(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
        if (object instanceof WebSocketFrame) {
            this.decodeWebSocketFrame(ctx, (WebSocketFrame) object, out);
        }
    }

    protected void decodeWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame) frame;
            ServerLogger.loggerWebSocket.debug(String.format("Channel %s, CloseWebSocketFrame %s, %s", ctx.channel().id().asShortText(), closeWebSocketFrame.statusCode(), closeWebSocketFrame.reasonText()));
            WebSocketServerHandshaker handshaker = WebSocketServerHandler.getHandshaker(ctx.channel());
            if (handshaker != null) {
                frame.retain();
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            } else {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        } else if (!(frame instanceof PongWebSocketFrame)) {
            out.add(frame.retain());
        }
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName()
                    , new WebSocketServerProtocolHandshakeHandler(this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch));
        }
        if (cp.get(Utf8FrameValidator.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof WebSocketHandshakeException) {
            ServerLogger.loggerProblem.error(String.format("Channel %s, error: "
                    , ctx.channel().id().asShortText()), cause);

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                    , HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;

        }
        WebSocketUtil.onError(ctx, cause);
    }

    static WebSocketServerHandshaker getHandshaker(Channel channel) {
        return channel.attr(HANDSHAKER_ATTR_KEY).get();
    }

    static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
        channel.attr(HANDSHAKER_ATTR_KEY).set(handshaker);
    }

    static ChannelHandler forbiddenHttpRequestResponder() {
        return new ChannelInboundHandlerAdapter() {
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof FullHttpRequest) {
                    ((FullHttpRequest) msg).release();
                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
                    ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        };
    }


    public static final class HandshakeComplete {
        private final String requestUri;
        private final HttpHeaders requestHeaders;
        private final String selectedSubprotocol;

        HandshakeComplete(String requestUri, HttpHeaders requestHeaders, String selectedSubprotocol) {
            this.requestUri = requestUri;
            this.requestHeaders = requestHeaders;
            this.selectedSubprotocol = selectedSubprotocol;
        }

        public String requestUri() {
            return this.requestUri;
        }

        public HttpHeaders requestHeaders() {
            return this.requestHeaders;
        }

        public String selectedSubprotocol() {
            return this.selectedSubprotocol;
        }
    }

}

