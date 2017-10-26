package com.ob.server.http.websocket;

import com.ob.server.ServerLogger;
import com.ob.server.http.AccessException;
import com.ob.server.session.ChannelUtil;
import com.ob.server.http.HttpConnect;
import com.ob.server.session.RequestSession;
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

    public DefaultWebSocketServerHandler(String webSocketPath, ResponderResolver responderResolver, ChannelGroup allChannels, AccessHandler accessHandler) {
        super(webSocketPath, null, true, accessHandler);
        this.responderResolver = responderResolver;
        this.allChannels = allChannels;
    }

    protected void decode0(ChannelHandlerContext ctx, Object msg, List<Object> out){

        try {
            if (msg instanceof HttpObject) {
                if (handler == null)
                    handler = HttpConnect.state(msg, handler, ctx.channel().id().asShortText());
            }
        }catch (Exception e){
            try {
                WebSocketUtil.onError(ctx, e);
            }catch (Exception cause){}
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            if (evt instanceof HandshakeComplete) {
                if (handler != null) {
                    requestSession = ChannelUtil.channelRequest(ctx, handler.context, responderResolver);
                    if(requestSession == null)
                        throw new AccessException();
                    ctx.channel().attr(ATTR_KEY).set(requestSession);
                }
            }
            ctx.fireUserEventTriggered(evt);
        }catch (Exception e){
            WebSocketUtil.onError(ctx, e);
        }
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s registered.", ctx.channel().id().asShortText()));
        ChannelUtil.gather(ctx, allChannels);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s unregistered.", ctx.channel().id().asShortText()));
        ctx.fireChannelUnregistered();
    }

}
