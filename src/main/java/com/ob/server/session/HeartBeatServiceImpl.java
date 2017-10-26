package com.ob.server.session;

import com.google.common.collect.Maps;
import com.ob.common.thread.TFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by boris on 19.05.2016.
 */
public class HeartBeatServiceImpl implements HeartBeatService {
    private ScheduledExecutorService scheduler;
    private Map<String, HeartBeat> sessions = Maps.newConcurrentMap();


    @PostConstruct
    public void startUp(){
        scheduler = Executors.newSingleThreadScheduledExecutor(new TFactory());
        scheduler.scheduleWithFixedDelay(()->{
            sessions.values().forEach(s -> {
                try{
                    s.heartBeat();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutDown(){
        if(!scheduler.isShutdown()){
            scheduler.shutdown();
        }
    }

    @Override
    public void addSession(String key, RequestSession requestSession) {
        if(requestSession instanceof HeartBeat)
            sessions.put(key, (HeartBeat)requestSession);
    }

    @Override
    public void removeSession(String key) {
        sessions.remove(key);
    }
}
