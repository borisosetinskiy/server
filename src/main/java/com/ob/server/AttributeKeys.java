/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  io.netty.util.AttributeKey
 */
package com.ob.server;

import com.ob.server.session.RequestSession;
import io.netty.util.AttributeKey;

public class AttributeKeys {
    public static final AttributeKey<RequestSession> REQUEST_SESSION_ATTR_KEY = AttributeKey.valueOf(RequestSession.class, (String) "RequestSession");
}

