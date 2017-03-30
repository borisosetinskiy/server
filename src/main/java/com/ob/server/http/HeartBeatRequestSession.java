package com.ob.server.http;


import com.ob.server.MessageAggregator;
import com.ob.server.resolvers.ChannelRequest;


/**
 * Created by boris on 19.04.2016.
 */
public class HeartBeatRequestSession implements RequestSession, HeartBeat{

    final HeartBeatFactory heartBeatFactory;
    final int timeFrame;
    private RequestSession requestSession;
    public HeartBeatRequestSession(RequestSession requestSession
            , HeartBeatFactory heartBeatFactory, int timeFrame) {
        assert requestSession != null;
        this.requestSession = requestSession;
        this.heartBeatFactory = heartBeatFactory;
        this.timeFrame = timeFrame;
    }

    private volatile long lastOperation;


    @Override
    public void onOpen() {
        requestSession.onOpen();
        lastOperation();
    }

    @Override
    public void onClose() {
        requestSession.onClose();
    }

    @Override
    public void onRead(Object message) {
        requestSession.onRead(message);
    }



    @Override
    public ChannelRequest getChannelRequest() {
        return requestSession.getChannelRequest();
    }

    @Override
    public Object getType() {
        return requestSession.getType();
    }

    @Override
    public void setSelf(RequestSession self) {
        requestSession.setSelf(self);
    }

    @Override
    public int getThrottlePause() {
        return requestSession.getThrottlePause();
    }

    @Override
    public int getBufferSize() {
        return requestSession.getBufferSize();
    }

    @Override
    public RequestSession unwrap() {
        return this;
    }


    void lastOperation(){
        lastOperation = System.currentTimeMillis();
    }

    @Override
    public void onWrite(Object message) {
        requestSession.onWrite(message);
        if(message instanceof MessageAggregator){
            lastOperation();
        }
    }

    @Override
    public String getName() {
        return requestSession.getName();
    }

    @Override
    public void heartBeat() {
        if(System.currentTimeMillis()- lastOperation >= timeFrame) {
            onWrite(heartBeatFactory.create());
            lastOperation();
        }
    }
}
