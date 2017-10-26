package com.ob.server.session;

/**
 * Created by boris on 1/9/2017.
 */
public interface EntryAggregator<T> {
    void add(T o);
    T[] array();
}
