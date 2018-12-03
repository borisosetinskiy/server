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

public class RequestServiceImpl
implements RequestService {
    private final RequestSessionFactory requestSessionFactory;
    private final HeartBeatService heartBeatService;

    public RequestServiceImpl(RequestSessionFactory requestSessionFactory, HeartBeatService heartBeatService) {
        this.requestSessionFactory = requestSessionFactory;
        this.heartBeatService = heartBeatService;
    }

    @Override
    public RequestSession process(ChannelRequestDto channelRequestDto) throws Exception {
        RequestSession requestSession = this.requestSessionFactory.newRequestSession(channelRequestDto);
        String sessionId = channelRequestDto.getChannelContext().channel().id().asShortText();
        if(heartBeatService!=null)
            heartBeatService.addSession(sessionId, requestSession);
        requestSession.onOpen();
        ServerLogger.logger.debug(String.format("Session %s. Opened", sessionId));
        channelRequestDto.getChannelContext().channel().closeFuture().addListener(future -> {
            if(heartBeatService!=null)
                heartBeatService.removeSession(sessionId);
            requestSession.onClose();
            ServerLogger.logger.debug(String.format("Session %s, lifecycle %s ms. Closed", sessionId, System.currentTimeMillis() - channelRequestDto.getTimestamp()));
        });
        return requestSession;
    }
}

