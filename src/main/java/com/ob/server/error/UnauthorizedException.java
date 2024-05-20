/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.error;

public class UnauthorizedException
        extends ProtocolException {
    public UnauthorizedException() {
        super("401, Unauthorized", 401);
    }

    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException(Throwable cause) {
        super("401, Unauthorized", 401, cause);
    }
}

