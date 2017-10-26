package com.ob.server.session;


public interface HeartBeatService {
    void addSession(String key, RequestSession requestSession);
    void removeSession(String key);
}
