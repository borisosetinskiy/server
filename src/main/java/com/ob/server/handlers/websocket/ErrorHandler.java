package com.ob.server.handlers.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ErrorHandler extends ChannelInboundHandlerAdapter {
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        WebSocketUtil.onError(ctx, cause);
    }
}
