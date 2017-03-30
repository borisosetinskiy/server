package com.ob.server.http.websocket;

import com.ob.server.http.ChannelUtil;
import com.ob.server.http.HttpConnect;
import com.ob.server.http.RequestSession;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObject;

import java.util.List;

import static com.ob.server.http.websocket.AttributeKeys.ATTR_KEY;

/**
 * Created by boris on 12/16/2016.
 */
public class DefaultWebSocketServerHandler extends WebSocketServerHandler {
    public final ResponderResolver responderResolver;
    public final ChannelGroup allChannels;
    private HttpConnect handler;
    private RequestSession requestSession;

    public DefaultWebSocketServerHandler(String webSocketPath, ResponderResolver responderResolver, ChannelGroup allChannels) {
        super(webSocketPath, null, true);
        this.responderResolver = responderResolver;
        this.allChannels = allChannels;
    }

    protected void decode0(ChannelHandlerContext ctx, Object msg, List<Object> out){
        if (msg instanceof HttpObject) {
            if (handler == null)
                handler = HttpConnect.state(msg, handler, ctx.channel().id().asShortText());
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof HandshakeComplete){
            if (handler != null) {
                requestSession = ChannelUtil.channelRequest(ctx, handler.context, responderResolver);
                ctx.channel().attr(ATTR_KEY).set(requestSession);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

}
