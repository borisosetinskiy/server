
package com.ob.server.handlers.websocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
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

    protected void decode(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
        if (object instanceof WebSocketFrame) {
            this.decodeWebSocketFrame(ctx, (WebSocketFrame) object, out);
        }
    }

    protected void decodeWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame) frame;
            log.info(String.format("Channel %s, CloseWebSocketFrame %s, %s", ctx.channel().id().asShortText(), closeWebSocketFrame.statusCode(), closeWebSocketFrame.reasonText()));
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
            log.error(String.format("Channel %s, error: "
                    , ctx.channel().id().asShortText()), cause);

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                    , HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;

        }
        WebSocketUtil.onError(ctx, cause);
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

