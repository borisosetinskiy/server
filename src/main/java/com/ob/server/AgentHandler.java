/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.util.ReferenceCountUtil
 *  org.slf4j.Logger
 */
package com.ob.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

@ChannelHandler.Sharable
public class AgentHandler
extends MessageToMessageDecoder<Object> {
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        if (o instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)o;
            ServerLogger.agentLogger.debug(String.format("Channel %s \nRequest %s", channelHandlerContext.channel().id().asShortText(), PrintUtil.appendRequest(new StringBuilder(256), request)));
        }
        list.add(ReferenceCountUtil.retain((Object)o));
    }
}

