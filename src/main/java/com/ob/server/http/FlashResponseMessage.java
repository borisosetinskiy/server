package com.ob.server.http;

/**
 * Created by boris on 3/2/2017.
 */
public class FlashResponseMessage implements ResponseMessage {
    private Object message;
    @Override
    public Object getMessage() {
        return message;
    }
}
