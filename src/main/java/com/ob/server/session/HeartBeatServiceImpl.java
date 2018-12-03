/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.session;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class HeartBeatServiceImpl
        implements HeartBeatService {
    private final ScheduledExecutorService scheduler;
    private Map<String, HeartBeat> sessions = new ConcurrentHashMap<>(64, 0.75f, 64);
    private final HeartBeatFactory heartBeatFactory;
    public HeartBeatServiceImpl(ScheduledExecutorService scheduler, HeartBeatFactory heartBeatFactory) {
        this.scheduler = scheduler;
        this.heartBeatFactory = heartBeatFactory;
    }

    public void start() {
        this.scheduler.scheduleWithFixedDelay(() ->
                sessions.values().forEach(s -> {
                    try {
                        s.heartBeat();
                    } catch (Exception var2) {
                    }
                }), 5L, 5L, TimeUnit.SECONDS);
    }


    public void stop() {
        if (!this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
        }
    }

    @Override
    public void addSession(String key, RequestSession requestSession) {
        this.sessions.put(key,  new HeartBeat(requestSession));
    }

    @Override
    public void removeSession(String key) {
        this.sessions.remove(key);
    }

    class HeartBeat{
        private AtomicLong lastOperation = new AtomicLong();
        private RequestSession requestSession;

        public HeartBeat(RequestSession requestSession) {
            this.requestSession = requestSession;
        }
        public void heartBeat() {
            if (System.currentTimeMillis() - lastOperation.get() >= 30) {
                requestSession.onWrite(heartBeatFactory.create());
                lastOperation.getAndSet(System.currentTimeMillis());
            }
        }
    }
}

