package com.ob.server.session;

public interface ResponseTransformerFactory {
    ResponseTransformer getResponseTransformer(Object o);
}
