package com.ob.server.session;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by boris on 1/9/2017.
 */
public class QueuedEntryAggregator implements EntryAggregator {
    private final Queue messages=
            new ConcurrentLinkedQueue();
    private int limit = 1024;
    private AtomicInteger counter = new AtomicInteger();

    public QueuedEntryAggregator(int limit) {
        if(limit>0)
            this.limit = limit;
    }

    @Override
    public void add(Object o) {
        if(counter.incrementAndGet()>limit) {
            messages.poll();
            counter.decrementAndGet();
        }
        messages.offer(o);
    }

    @Override
    public Object[] array() {
        ArrayList cache = new ArrayList();
        Object o;
        while((o=messages.poll())!=null){
            counter.decrementAndGet();
            cache.add(o);
        }
        return cache.toArray();
    }



}
