
package com.ob.server.security;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

@ChannelHandler.Sharable
public class SecurityHandler
        extends MessageToMessageDecoder<Object> {
    private final SecurityProcessor securityProcessor;

    public SecurityHandler(SecurityProcessor securityProcessor) {
        this.securityProcessor = securityProcessor;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {

        if (o instanceof DefaultHttpRequest ) {
            DefaultHttpRequest defaultHttpRequest = (DefaultHttpRequest) o;
            SecurityProcessor currentSecurityProcessor;
            SecurityProcessor nextSecurityProcessor = securityProcessor;
            do {
                currentSecurityProcessor = nextSecurityProcessor;
                currentSecurityProcessor.process(channelHandlerContext, defaultHttpRequest);
            } while ((nextSecurityProcessor = currentSecurityProcessor.next()) != null
                    && nextSecurityProcessor != currentSecurityProcessor
            );
        }
        list.add(ReferenceCountUtil.retain((Object) o));
    }
}

