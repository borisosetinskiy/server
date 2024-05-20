/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.error;

public class ForbiddenException
        extends ProtocolException {
    public ForbiddenException() {
        super("403, Forbidden", 403);
    }

    public ForbiddenException(Throwable cause) {
        super("403, Forbidden", 403, cause);
    }
}

