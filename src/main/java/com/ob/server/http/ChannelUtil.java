package com.ob.server.http;

import com.ob.server.resolvers.ChannelRequest;
import com.ob.server.resolvers.Responder;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

/**
 * Created by boris on 12/24/2016.
 */
public class ChannelUtil {

    public static void gather(final ChannelHandlerContext ctx, final ChannelGroup allChannels){
        final Channel channel = ctx.channel();
        allChannels.add(channel);
        channel.closeFuture().addListener(future -> {
            allChannels.remove(channel);
        });
    }

    public static void disassemble(final ChannelHandlerContext ctx, final ChannelGroup allChannels){
        try{
            allChannels.remove(ctx.channel());
        }catch (Exception e){}
    }

    public static RequestSession channelRequest(final ChannelHandlerContext ctx
            , Object2ObjectArrayMap<String, String> context
            , ResponderResolver responderResolver)throws Exception{
        RequestSession requestSession = null;
        if (context != null) {
            final ChannelRequest channelRequest = new ChannelRequestImpl(ctx, context);
            final Responder responder = responderResolver.resolve(context.get(QueryDecoder.PATH));
            if (responder != null) {
                requestSession = responder.respond(channelRequest);
            }
        }
        return requestSession;

    }

}
