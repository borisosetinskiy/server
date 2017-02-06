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
    RequestSession EMPTY = new RequestSession(){

        @Override
        public void onWrite(Object message) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void onOpen() {

        }

        @Override
        public void onClose() {

        }

        @Override
        public void onRead(Object message) {

        }

        @Override
        public ChannelRequest getChannelRequest() {
            return null;
        }

        @Override
        public Object getType() {
            return null;
        }

        @Override
        public void setSelf(RequestSession self) {

        }

        @Override
        public int getThrottlePause() {
            return 0;
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public RequestSession unwrap() {
            return null;
        }
    };
}
