package com.ob.server.session;

public interface Session {
    String getSessionId();
    void onOpen();
    void onClose();
}
