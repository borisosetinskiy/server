package com.ob.server.http.handler;

import com.ob.server.ServerLogger;
import com.ob.server.http.HttpUtils;
import com.ob.server.resolvers.ChannelHandlerResolvers;
import com.ob.server.resolvers.ResponderResolver;
import com.ob.server.session.ChannelRequestImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static com.ob.server.http.AttributeKeys.CHANNEL_REQUEST_ATTR_KEY;
import static com.ob.server.http.PrintUtil.fromStack;

public class ChannelRequestHandler extends MessageToMessageDecoder<Object> {
    private final ResponderResolver responderResolver;
    private final ChannelRequestImpl channelRequest;
    private final ChannelHandlerResolvers channelHandlerResolvers;

    public ChannelRequestHandler(ResponderResolver responderResolver, ChannelHandlerResolvers channelHandlerResolvers) {
        this.channelRequest = new ChannelRequestImpl();
        this.responderResolver = responderResolver;
        this.channelHandlerResolvers = channelHandlerResolvers;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof HttpObject) {
            channelRequest.handle(o);
            if (channelRequest.isFinished()){
                String path = channelRequest.getContext().get(HttpUtils.PATH);
                final ChannelHandlerFactory channelHandlerFactory = channelHandlerResolvers.resolve(path);
                if(channelHandlerFactory != null) {
                    ctx.pipeline().addLast(channelHandlerFactory.create());
                }
                channelRequest.setChannelHandlerContext(ctx);
                ctx.channel().attr(CHANNEL_REQUEST_ATTR_KEY).set(channelRequest);
                ctx.pipeline().addLast(new RequestSessionHandler(responderResolver));

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
