package com.ob.server.security;

public interface SecurityChain {
    SecurityProcessor next();
}
