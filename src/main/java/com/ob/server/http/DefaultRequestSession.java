package com.ob.server.http;

import akka.actor.ActorSystem;
import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 11.04.2016.
 */
public abstract class DefaultRequestSession<T> implements RequestSession<T> {
    protected final ChannelRequest channelRequest;
    protected final ActorSystem system;
    protected String name;
    protected RequestSession self;
    protected DefaultRequestSession(ChannelRequest channelRequest, ActorSystem system) {
        this.system = system;
        assert channelRequest != null;
        this.channelRequest = channelRequest;
        name = channelRequest.getChannelContext().channel().id().asShortText();
        self = this;
    }

    @Override
    public void onWrite(Object message) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {
        if(channelRequest.getChannelContext().channel().isOpen()){
            try{
                channelRequest.getChannelContext().channel().close();
            }catch (Exception e){}
        }
    }

    @Override
    public void onRead(Object message) {

    }

    @Override
    public ChannelRequest getChannelRequest() {
        return channelRequest;
    }

    @Override
    public T getType() {
        return type();
    }

    @Override
    public int getThrottlePause() {
        return 0;
    }

    protected abstract T type();
    protected RequestSession<T> getSelf(){
        return self;
    };

    @Override
    public RequestSession<T> unwrap() {
        return this;
    }


}
