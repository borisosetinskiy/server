package com.ob.server.http;


import com.ob.server.session.RequestSession;

/**
 * Created by boris on 30.06.2016.
 */
public class AttributeKeys {
    public static final io.netty.util.AttributeKey<RequestSession> REQUEST_SESSION_ATTR_KEY = io.netty.util.AttributeKey.valueOf(RequestSession.class, "RequestSession");
}
