package com.ob.server.session;

import com.ob.server.resolvers.ChannelRequest;

public class ThrottlerRequestSession  extends HeartBeatRequestSession{
    protected Throttler throttler;
    protected final int throttlerPause;

    public ThrottlerRequestSession(String sessionId, String withDispatcher, String withMailbox
            , ChannelRequest channelRequest, HeartBeatFactory heartBeatFactory, int timeFrame, int throttlerPause) {
        super(sessionId, withDispatcher, withMailbox, channelRequest, heartBeatFactory, timeFrame);
        this.throttlerPause = throttlerPause;
        throttler = new DefaultThrottler();
    }


}
