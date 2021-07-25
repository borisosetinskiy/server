/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelId
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.slf4j.Logger
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Required
 */
package com.ob.server;

import com.ob.server.session.HeartBeatService;
import com.ob.server.session.RequestService;
import com.ob.server.session.RequestSession;
import com.ob.server.session.RequestSessionFactory;

import java.util.concurrent.atomic.AtomicLong;

public class RequestServiceImpl
        implements RequestService {
    private final RequestSessionFactory requestSessionFactory;


    public RequestServiceImpl(RequestSessionFactory requestSessionFactory) {
        this.requestSessionFactory = requestSessionFactory;
    }
    @Override
    public RequestSession process(ChannelRequestDto channelRequestDto) throws Exception {
        RequestSession requestSession
                = this.requestSessionFactory
                .newRequestSession(channelRequestDto);
        requestSession.onOpen();
        return requestSession;
    }

 }
