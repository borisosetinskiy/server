package com.ob.server.session;


import com.ob.server.resolvers.ChannelRequest;


/**
 * Created by boris on 19.04.2016.
 */
public class HeartBeatRequestSession extends AbstractRequestSession implements HeartBeat{

    final HeartBeatFactory heartBeatFactory;
    final int timeFrame;
    private volatile long lastOperation;

    public HeartBeatRequestSession(String sessionId, String withDispatcher, String withMailbox, ChannelRequest channelRequest, ResponseFormatter responseFormatter, HeartBeatFactory heartBeatFactory, int timeFrame) {
        super(sessionId, withDispatcher, withMailbox, channelRequest, responseFormatter);
        this.heartBeatFactory = heartBeatFactory;
        this.timeFrame = timeFrame;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        lastOperation();
    }

    @Override
    public void tell(Object o) {

    }

    @Override
    protected void initSessionParams() {

    }

    void lastOperation(){
        lastOperation = System.currentTimeMillis();
    }

    @Override
    public void onWrite(Object message) {
        lastOperation();
    }

    @Override
    public void heartBeat() {
        if(System.currentTimeMillis()- lastOperation >= timeFrame) {
            onWrite(heartBeatFactory.create());
            lastOperation();
        }
    }
}
