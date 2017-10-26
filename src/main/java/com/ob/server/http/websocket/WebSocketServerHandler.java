package com.ob.server.http.websocket;

import com.ob.server.ServerLogger;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;

import java.util.List;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class WebSocketServerHandler extends MessageToMessageDecoder<Object> {



    /**
     * Events that are fired to notify about handshake status
     */
    public enum ServerHandshakeStateEvent {
        /**
         * The Handshake was completed successfully and the channel was upgraded to websockets.
         *
         * @deprecated in favor of {@link WebSocketServerProtocolHandler.HandshakeComplete} class,
         * it provides extra information about the handshake
         */
        @Deprecated
        HANDSHAKE_COMPLETE
    }

    /**
     * The Handshake was completed successfully and the channel was upgraded to websockets.
     */
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
            return requestUri;
        }

        public HttpHeaders requestHeaders() {
            return requestHeaders;
        }

        public String selectedSubprotocol() {
            return selectedSubprotocol;
        }
    }

    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY =
            AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadLength;
    private final boolean allowMaskMismatch;
    private final AccessHandler accessHandler;

    public WebSocketServerHandler(String webSocketPath, AccessHandler accessHandler) {
        this(webSocketPath, null, false, accessHandler);
    }

    public WebSocketServerHandler(String webSocketPath, String subprotocols, AccessHandler accessHandler) {
        this(webSocketPath, subprotocols, false, accessHandler);
    }

    public WebSocketServerHandler(String webSocketPath, String subprotocols, boolean allowExtensions, AccessHandler accessHandler) {
        this(webSocketPath, subprotocols, allowExtensions, 65536, accessHandler);
    }

    public WebSocketServerHandler(String webSocketPath, String subprotocols,
                                          boolean allowExtensions, int maxFrameSize, AccessHandler accessHandler) {
        this(webSocketPath, subprotocols, allowExtensions, maxFrameSize, false,accessHandler);
    }

    public WebSocketServerHandler(String webSocketPath, String subprotocols,
                                          boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, AccessHandler accessHandler) {
        this.websocketPath = webSocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadLength = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
        this.accessHandler = accessHandler;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
        decode0(ctx, object, out);
        if (object instanceof WebSocketFrame) {
            decodeWebSocketFrame(ctx, (WebSocketFrame)object, out);
        }
    }

    protected void decode0(ChannelHandlerContext ctx, Object object, List<Object> out){}


    protected void decodeWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out){
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame)frame;
            ServerLogger.loggerWebSocket.debug(String.format("Channel %s, CloseWebSocketFrame %s, %s", ctx.channel().id().asShortText(), closeWebSocketFrame.statusCode()
                    , closeWebSocketFrame.reasonText()));
            WebSocketServerHandshaker handshaker = getHandshaker(ctx.channel());
            if (handshaker != null) {
                frame.retain();
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            } else {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }else if (frame instanceof PongWebSocketFrame) {
            // Pong frames need to get ignored
            return;
        }
        out.add(frame.retain());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            // Add the WebSocketHandshakeHandler before this one.
            ctx.pipeline().addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(),
                    new WebSocketServerProtocolHandshakeHandler(websocketPath, subprotocols,
                            allowExtensions, maxFramePayloadLength, allowMaskMismatch, accessHandler));
        }
        if (cp.get(Utf8FrameValidator.class) == null) {
            // Add the UFT8 checking before this one.
            ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(),
                    new Utf8FrameValidator());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof FullHttpRequest) {
                    ((FullHttpRequest) msg).release();
                    FullHttpResponse response =
                            new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FORBIDDEN);
                    ctx.channel().writeAndFlush(response);
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        };
    }
}
