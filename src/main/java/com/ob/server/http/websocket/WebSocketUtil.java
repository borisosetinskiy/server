package com.ob.server.http.websocket;

import com.ob.server.ServerLogger;
import com.ob.server.http.AccessException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;

import static com.ob.server.http.PrintUtil.fromStack;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by boris on 4/12/2017.
 */
public class WebSocketUtil {
    public static void onError(ChannelHandlerContext ctx, Throwable cause) {
        try {
            if (cause instanceof WebSocketHandshakeException) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else if(cause instanceof AccessException){
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(4403, "403, Forbidden"))
                        .addListener(ChannelFutureListener.CLOSE);
            }else if(cause instanceof  UnsupportedOperationException){
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(1010, cause.getMessage()))
                        .addListener(ChannelFutureListener.CLOSE);
            }
            else{
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(1011, cause.getMessage()))
                        .addListener(ChannelFutureListener.CLOSE);
            }
        }catch (Exception e){}
        finally {
            ServerLogger.loggerProblem.error(String.format("WebSocket, channel %s, error ", ctx.channel().id().asShortText()), fromStack(cause));

        }
    }

}
