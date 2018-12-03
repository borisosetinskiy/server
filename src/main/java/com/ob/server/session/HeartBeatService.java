/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.session;

public interface HeartBeatService {
    void addSession(String var1, RequestSession var2);
    void removeSession(String var1);
    void start();
    void stop();
}

