package com.ob.server.http;

/**
 * Created by boris on 05.07.2017.
 */
public interface Access {
    boolean check(Object msg);

    Access EMPTY = msg -> true;
}
