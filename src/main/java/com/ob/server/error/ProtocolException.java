
package com.ob.server.error;

public class ProtocolException
        extends RuntimeException {
    private final int code;

    public ProtocolException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ProtocolException(String message, int code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

