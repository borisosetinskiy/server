package com.ob.server.session;

public interface ResponseFormatterFactory {
    ResponseFormatter getResponseFormatter(ResponseFormatterType responseFormatterType);
}
