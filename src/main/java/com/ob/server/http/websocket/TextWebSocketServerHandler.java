package com.ob.server.http.websocket;


import com.ob.server.http.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.util.List;

import static com.ob.server.http.websocket.AttributeKeys.ATTR_KEY;

/**
 * Created by boris on 12/26/2016.
 */
public class TextWebSocketServerHandler extends MessageToMessageDecoder<TextWebSocketFrame> {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        final String msg = textWebSocketFrame.content().toString(Charset.defaultCharset());
        if(msg != null && !msg.isEmpty()){
            final RequestSession requestSession = channelHandlerContext.channel().attr(ATTR_KEY).get();
            requestSession.onRead(msg);
        }
    }
}
