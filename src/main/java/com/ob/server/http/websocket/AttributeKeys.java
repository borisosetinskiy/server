package com.ob.server.http.websocket;


import com.ob.server.session.RequestSession;

/**
 * Created by boris on 30.06.2016.
 */
public class AttributeKeys {
    public static final io.netty.util.AttributeKey<RequestSession> ATTR_KEY = io.netty.util.AttributeKey.valueOf(RequestSession.class, "RequestSession");
}
