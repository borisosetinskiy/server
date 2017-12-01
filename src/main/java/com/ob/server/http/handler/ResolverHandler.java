package com.ob.server.http.handler;

import com.ob.server.ServerLogger;
import com.ob.server.http.HttpUtils;
import com.ob.server.resolvers.ChannelHandlerResolver;
import com.ob.server.resolvers.Responder;
import com.ob.server.resolvers.ResponderResolver;
import com.ob.server.session.ChannelRequestImpl;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static com.ob.server.http.AttributeKeys.REQUEST_SESSION_ATTR_KEY;
import static com.ob.server.http.PrintUtil.fromStack;

public class ResolverHandler extends MessageToMessageDecoder<Object> {
    private final ResponderResolver responderResolver;
    private final ChannelRequestImpl channelRequest;
    private final ChannelHandlerResolver channelHandlerResolver;

    public ResolverHandler(ChannelHandlerContext ctx, ResponderResolver responderResolver, ChannelHandlerResolver channelHandlerResolver) {
        this.channelRequest = new ChannelRequestImpl(ctx);
        this.responderResolver = responderResolver;
        this.channelHandlerResolver = channelHandlerResolver;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof HttpObject) {
            channelRequest.handle(o);
            if (channelRequest.isFinished()){
                String path = channelRequest.getContext().get(HttpUtils.PATH);
                final ChannelHandlerFactory channelHandlerFactory = channelHandlerResolver.resolve(path);
                if(channelHandlerFactory != null) {
                    ctx.pipeline().addLast(channelHandlerFactory.create());
                }
                RequestSession requestSession = null;
                if (!channelRequest.getContext().isEmpty()) {
                    final Responder responder = responderResolver.resolve(path);
                    if (responder != null) {
                        requestSession = responder.respond(channelRequest);
                    }
                }
                if (requestSession != null)
                    ctx.channel().attr(REQUEST_SESSION_ATTR_KEY).set(requestSession);
            }
        }
        list.add(ReferenceCountUtil.retain(o));
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s registered.", ctx.channel().id().asShortText()));
        ctx.fireChannelRegistered();
    }
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, Throwable cause) {
        ServerLogger.loggerProblem.error(String.format("Channel %s, error %s ",ctx.channel().id().asShortText(), fromStack(cause)));
        ctx.fireExceptionCaught(cause);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerLogger.loggerChannel.debug(String.format("Channel %s unregistered.", ctx.channel().id().asShortText()));
        ctx.fireChannelUnregistered();

    }
}
