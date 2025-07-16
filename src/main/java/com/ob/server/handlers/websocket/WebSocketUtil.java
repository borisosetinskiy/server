
package com.ob.server.handlers.websocket;

import com.ob.server.error.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                } else if (cause instanceof BadRequestException) {
                    code = ((ProtocolException) cause).getCode();
                } else if (cause instanceof TooManyRequestException) {
                    code = ((ProtocolException) cause).getCode();
                } else if (cause instanceof UnauthorizedException) {
                    code = ((ProtocolException) cause).getCode();
                }
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(code, message)).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
            }
            log.error("CLOSE webSocket, channel {}, code {} error {} "
                    , ctx.channel().id().asShortText(), code, cause.getMessage(), cause);
        }
    }
}

