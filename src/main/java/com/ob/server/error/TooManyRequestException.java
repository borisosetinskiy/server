/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.error;

public class TooManyRequestException
        extends ProtocolException {
    public TooManyRequestException() {
        super("429, Too Many Requests", 429);
    }

    public TooManyRequestException(Throwable cause) {
        super("429, Too Many Requests", 429, cause);
    }
}

