package com.ob.common.akka;

/**
 * Created by boris on 09.04.2016.
 */
public interface CreateEvent<T> {
    void onCreate(T object);
}
