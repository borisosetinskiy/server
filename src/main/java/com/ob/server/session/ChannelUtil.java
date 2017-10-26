package com.ob.server.session;

import com.ob.server.http.QueryDecoder;
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
    }


    public static RequestSession channelRequest(final ChannelHandlerContext ctx
            , Object2ObjectArrayMap<String, String> context
            , ResponderResolver responderResolver)throws Exception{
        RequestSession requestSession = null;
        if (context != null) {
            final Responder responder = responderResolver.resolve(context.get(QueryDecoder.PATH));
            if (responder != null) {
                final ChannelRequest channelRequest = new ChannelRequestImpl(ctx, context);
                requestSession = responder.respond(channelRequest);
            }
        }
        return requestSession;

    }

}
