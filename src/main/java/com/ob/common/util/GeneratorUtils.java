package com.ob.common.util;

import com.google.common.collect.Maps;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by boris on 11/21/2016.
 */
public class GeneratorUtils {
    final public static Map<Character, GeneratorUtils> generators = Maps.newConcurrentMap();
    final AtomicLong counter = new AtomicLong();
    final Lock lock = new ReentrantLock();
    final char key;
    volatile long lastTimestamp = System.currentTimeMillis();
    final static Lock staticLock = new ReentrantLock();

    public GeneratorUtils(char key) {
        this.key = key;
    }


    private String generate(){

        final String result;
        lock.lock();
        try{
            final long timestamp = System.currentTimeMillis();
            if(lastTimestamp != timestamp){
                counter.set(0);
                lastTimestamp = timestamp;
            }
            long count = counter.incrementAndGet();
            result = new StringBuffer().append(timestamp).append(key).append(count).toString();
        }finally {
            lock.unlock();
        }
        return result;
    }
    static GeneratorUtils getOrCreate(final char key){
        GeneratorUtils result = generators.get(key);
        if(result == null){
            staticLock.lock();
            try{
                result = generators.get(key);
                if(result == null){
                    result = new GeneratorUtils(key);
                    generators.put(key, result);
                }
            }finally {
                staticLock.unlock();
            }
        }
        return result;
    }

    public static String generate(final char key){
        Assert.notNull(key);
        GeneratorUtils generator = getOrCreate(key);
        return generator.generate();
    }
}
