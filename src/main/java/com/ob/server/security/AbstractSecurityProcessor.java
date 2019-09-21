package com.ob.server.security;


import io.netty.handler.codec.http.HttpMessage;

public abstract class AbstractSecurityProcessor implements  SecurityProcessor {
    private SecurityProcessor nextSecurityProcessor;

    public AbstractSecurityProcessor(SecurityProcessor nextSecurityProcessor) {
        this.nextSecurityProcessor = nextSecurityProcessor;
    }

    @Override
    public SecurityProcessor next() {
        return nextSecurityProcessor;
    }
}
