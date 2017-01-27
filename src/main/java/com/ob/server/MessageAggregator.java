package com.ob.server;

/**
 * Created by boris on 1/9/2017.
 */
public interface MessageAggregator<T> {
    void add(T o);
    T poll();
    MessageAggregator flash();

}
