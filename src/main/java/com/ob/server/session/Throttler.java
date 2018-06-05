package com.ob.server.session;

/**
 * Created by boris on 1/9/2017.
 */
public interface Throttler {
    void add(Object key);
    boolean isExpired(Object key, int time);
}
