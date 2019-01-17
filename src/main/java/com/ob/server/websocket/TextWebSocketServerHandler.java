/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.handler.codec.http.websocketx.TextWebSocketFrame
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  org.slf4j.Logger
 */
package com.ob.server.websocket;

import com.ob.server.HttpUtils;
import com.ob.server.ServerLogger;
import com.ob.server.AttributeKeys;
import com.ob.server.PrintUtil;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.util.List;

public class TextWebSocketServerHandler
extends MessageToMessageDecoder<TextWebSocketFrame> {
    final static TextWebSocketFrame PONG = new TextWebSocketFrame("1");
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        try {
            String msg = textWebSocketFrame.content().toString(Charset.defaultCharset());
            if("0".equals(msg)){
                channelHandlerContext
                        .writeAndFlush(HttpUtils.safeDuplicate(PONG)
                                , channelHandlerContext.voidPromise());
                return;
            }
            if (msg != null && !msg.isEmpty()) {
                    ServerLogger.loggerMessage.debug("Channel {}, message {}"
                            , channelHandlerContext.channel().id().asShortText(), msg);
                    io.netty.util.Attribute<RequestSession> attribute = channelHandlerContext.channel().attr(AttributeKeys.REQUEST_SESSION_ATTR_KEY);
                    if(attribute == null) {
                        WebSocketUtil.onError(channelHandlerContext, new Exception("No session"));
                        return;
                    }
                    RequestSession requestSession = attribute.get();
                    if(requestSession == null){
                        WebSocketUtil.onError(channelHandlerContext, new Exception("No session"));
                        return;
                    }
                    requestSession.onRead(msg);

            }

        }
        catch (Exception var6) {
            ServerLogger.loggerTrash.error("Channel {}"
                    , channelHandlerContext.channel().id().asShortText()
                    , var6);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}

