package com.ob.server.http;


public interface HeartBeatService {
    void addSession(String key, RequestSession requestSession);
    void removeSession(String key);
}
