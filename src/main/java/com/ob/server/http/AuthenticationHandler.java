package com.ob.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class AuthenticationHandler extends MessageToMessageDecoder<Object> {
    protected Access access = Access.EMPTY;

    public AuthenticationHandler(Access access) {
        this.access = access;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
//        list.add(o);
        list.add(ReferenceCountUtil.retain(o));
    }
}
