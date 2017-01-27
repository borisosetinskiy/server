package com.ob.server.http;


import com.ob.server.resolvers.ChannelRequest;


/**
 * Created by boris on 11.04.2016.
 */
public interface RequestSession<T> {
    void onWrite(final Object message);
    String getName();
    void onOpen();
    void onClose();
    void onRead(final Object message);
    ChannelRequest getChannelRequest();
    T getType();
    void setSelf(RequestSession self);
    int getThrottlePause();
    int getBufferSize();
    RequestSession<T> unwrap();
}
