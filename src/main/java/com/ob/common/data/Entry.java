package com.ob.common.data;

/**
 * Created by boris on 1/9/2017.
 */
public interface Entry<T>{
    T key();
    Entry EMPTY = () -> 1l;
}
