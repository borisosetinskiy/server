
package com.ob.server.handlers.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.util.List;

public class TextWebSocketServerHandler
        extends MessageToMessageDecoder<TextWebSocketFrame> {
    protected void decode(ChannelHandlerContext channelHandlerContext
            , TextWebSocketFrame textWebSocketFrame
            , List<Object> list) throws Exception {
        String msg = textWebSocketFrame.content().toString(Charset.defaultCharset());
        list.add(msg);
    }
}

