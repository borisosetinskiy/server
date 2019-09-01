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
package com.ob.server.handlers.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.List;

public class TextWebSocketServerHandler
        extends MessageToMessageDecoder<TextWebSocketFrame> {
    protected void decode(ChannelHandlerContext channelHandlerContext
            , TextWebSocketFrame textWebSocketFrame
            , List<Object> list) throws Exception {

        String msg = textWebSocketFrame.content().toString(Charset.defaultCharset());
        list.add(ReferenceCountUtil.retain(msg));
    }
}

