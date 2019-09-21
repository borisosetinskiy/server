/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.util.ReferenceCountUtil
 */
package com.ob.server.security;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

@ChannelHandler.Sharable
public class SecurityHandler
        extends MessageToMessageDecoder<Object> {
    private SecurityProcessor securityProcessor;

    public SecurityHandler(SecurityProcessor securityProcessor) {
        this.securityProcessor = securityProcessor;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        if(o instanceof HttpMessage) {
            SecurityProcessor currentSecurityProcessor;
            SecurityProcessor nextSecurityProcessor = securityProcessor;
            do {
                currentSecurityProcessor = nextSecurityProcessor;
                currentSecurityProcessor.process(channelHandlerContext, (HttpMessage)o);
            } while ((nextSecurityProcessor = currentSecurityProcessor.next()) != null
                    && nextSecurityProcessor != currentSecurityProcessor
            );
        }
        list.add(ReferenceCountUtil.retain((Object)o));
    }
}

