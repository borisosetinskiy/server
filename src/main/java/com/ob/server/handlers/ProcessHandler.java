package com.ob.server.handlers;

import com.ob.server.AttributeKeys;
import com.ob.server.ServerLogger;
import com.ob.server.handlers.websocket.WebSocketUtil;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class ProcessHandler extends MessageToMessageDecoder<Object> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext
            , Object message
            , List<Object> list) throws Exception {
        if (message != null) {
            ServerLogger.loggerMessage.debug("Channel {}, message {}"
                    , channelHandlerContext.channel().id().asShortText(), message);
            io.netty.util.Attribute<RequestSession> attribute = channelHandlerContext.channel().attr(AttributeKeys.REQUEST_SESSION_ATTR_KEY);
            if(attribute == null) {
                throw  new Exception("No session");
            }
            RequestSession requestSession = attribute.get();
            if(requestSession == null){
                throw new Exception("No session");
            }
            requestSession.onRead(channelHandlerContext, message);
            list.add(ReferenceCountUtil.retain(message));
        }
    }
}
