/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server;

public class AccessException
extends Exception {
    public AccessException() {
        super("403, Forbidden");
    }

    public AccessException(Throwable cause) {
        super("403, Forbidden", cause);
    }
}

