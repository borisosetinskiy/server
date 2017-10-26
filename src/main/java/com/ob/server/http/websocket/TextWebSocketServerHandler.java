package com.ob.server.http.websocket;


import com.ob.server.ServerLogger;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.util.List;

import static com.ob.server.http.PrintUtil.fromStack;
import static com.ob.server.http.websocket.AttributeKeys.ATTR_KEY;

/**
 * Created by boris on 12/26/2016.
 */
public class TextWebSocketServerHandler extends MessageToMessageDecoder<TextWebSocketFrame> {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        try {
            final String msg = textWebSocketFrame.content().toString(Charset.defaultCharset());
            if (msg != null && !msg.isEmpty()) {
                ServerLogger.loggerMessage.debug("Channel {}, message {}"
                        , channelHandlerContext.channel().id().asShortText()
                , msg);
                final RequestSession requestSession = channelHandlerContext.channel().attr(ATTR_KEY).get();
                requestSession.onRead(msg);
            }
        }catch (Exception e){
            ServerLogger.loggerTrash.error("Channel {}, error {}"
                    ,channelHandlerContext.channel().id().asShortText(), fromStack(e));
        }
    }
}
