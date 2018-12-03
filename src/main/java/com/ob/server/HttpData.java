/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public interface HttpData {
    Object2ObjectArrayMap<String, String> EMPTY_MAP = new Object2ObjectArrayMap();
    HttpData EMPTY = new HttpData(){};
    default void collect(Object var1){}
    default boolean finished() {
        return false;
    }
    default Object2ObjectArrayMap<String, String> context() {
        return EMPTY_MAP;
    }
}

