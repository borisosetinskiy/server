package com.ob.server.session;

import java.util.concurrent.atomic.AtomicLong;

public class Timing {
    private long code = System.currentTimeMillis()+System.nanoTime();
    private final AtomicLong time = new AtomicLong(0);
    public void update() {
        time.compareAndSet(-1, System.currentTimeMillis());
    }

    public Boolean isExpired(long t){
        long current = time.get();
        if(System.currentTimeMillis() - current > t){
            time.compareAndSet(current, -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timing that = (Timing) o;
        return (code == that.code);

    }

    @Override
    public int hashCode() {
        int result = (int) (code ^ (code >>> 32));
        return result;
    }

    public static final Timing EMPTY = new Timing();

}
