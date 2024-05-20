package com.ob.server.security;


public abstract class AbstractSecurityProcessor implements SecurityProcessor {
    private final SecurityProcessor nextSecurityProcessor;

    public AbstractSecurityProcessor(SecurityProcessor nextSecurityProcessor) {
        this.nextSecurityProcessor = nextSecurityProcessor;
    }

    @Override
    public SecurityProcessor next() {
        return nextSecurityProcessor;
    }
}
