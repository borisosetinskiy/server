package com.ob.server.actor;

import com.ob.server.MessageAggregator;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.ob.common.collection.QueueUtil.findNextPositivePowerOfTwo;

/**
 * Created by boris on 1/9/2017.
 */
public class ConcurrentQueueMessageAggregator implements MessageAggregator {
    private final Queue messages=
            new ConcurrentLinkedQueue();
    private int capacity;


    public ConcurrentQueueMessageAggregator(int capacity, int ratio) {
        if(ratio < 1)throw new IllegalArgumentException("Can be less 1");
        this.capacity = Math.max(findNextPositivePowerOfTwo(capacity), 256)/ratio;
    }


    @Override
    public void add(Object o) {
        if(messages.size()>=capacity){
            try {
                messages.remove();
            }catch (Exception e){}
        }
        messages.offer(o);
    }

    @Override
    public Object poll() {
        return messages.poll();
    }

    @Override
    public MessageAggregator flash() {return this;}


}
