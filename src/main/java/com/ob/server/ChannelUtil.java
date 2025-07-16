
package com.ob.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

public class ChannelUtil {
    public static void gather(ChannelHandlerContext ctx, ChannelGroup allChannels) {
        Channel channel = ctx.channel();
        allChannels.add(channel);
    }
}

