
package com.ob.server.error;

public class BadRequestException
        extends ProtocolException {
    public BadRequestException() {
        super("400, BadRequest", 400);
    }

    public BadRequestException(Throwable cause) {
        super("400, BadRequest", 400, cause);
    }
}

