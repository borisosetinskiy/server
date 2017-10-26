package com.ob.server.http.websocket;

/**
 * Created by boris on 05.07.2017.
 */
public interface AccessHandler {
    boolean handle(Object msg);

    AccessHandler EMPTY = new AccessHandler(){

        @Override
        public boolean handle(Object msg) {
            return true;
        }
    };
}
