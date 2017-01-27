package com.ob.common.actor;

/**
 * Created by boris on 09.04.2016.
 */
public interface CreateEvent<T> {
    void onCreate(T object);
}
