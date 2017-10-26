package com.ob.server.http;

/**
 * Created by boris on 05.07.2017.
 */
public class AccessException extends Exception {
    public AccessException() {
        super("403, Forbidden");
    }

    public AccessException(Throwable cause) {
        super("403, Forbidden", cause);
    }

}
