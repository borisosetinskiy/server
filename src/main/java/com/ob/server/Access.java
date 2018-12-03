/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server;

public interface Access {
    Access EMPTY = msg -> true;

    boolean check(Object var1);
}

