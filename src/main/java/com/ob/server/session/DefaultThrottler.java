package com.ob.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultThrottler implements Throttler {

    private Map<Object, Timing> timings = new ConcurrentHashMap();

    @Override
    public void add(Object o) {
        Timing timing = timings.get(o);
        if(timing == null){
            timing = new Timing();
            timings.put(o, timing);
        }
        timing.update();
    }
    @Override
    public boolean isExpired(Object key, int time) {
        Timing timing = timings.get(key);
        if(timing == null) return false;
        return timing.isExpired(time);
    }
}
