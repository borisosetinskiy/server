package com.ob.server.session;

public interface SessionParams<T> {
    int getThrottlePause();
    T getType();
    int limit();
    SessionParams EMPTY = new SessionParams(){

        @Override
        public int getThrottlePause() {
            return 0;
        }

        @Override
        public Object getType() {
            return null;
        }

        @Override
        public int limit() {
            return -1;
        }
    };
}
