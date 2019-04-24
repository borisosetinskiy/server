package com.ob.server.session;

import com.ob.server.ChannelRequestDto;
import com.ob.server.SocketFrameWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GuaranteeRequestSession <IN, OUT> implements RequestSession {
    private Logger logger = LoggerFactory.getLogger(GuaranteeRequestSession.class);

    private ChannelRequestDto channelRequestDto;
    private String sessionId;
    private SocketFrameWrapper<IN, OUT> socketFrameWrapper;

    public GuaranteeRequestSession(ChannelRequestDto channelRequestDto,
                                   SocketFrameWrapper<IN, OUT> socketFrameWrapper) {
        this.channelRequestDto = channelRequestDto;
        this.socketFrameWrapper = socketFrameWrapper;
    }
}
