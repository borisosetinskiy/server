package com.ob.server.session;

import com.ob.common.data.Entry;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class EntryWithTime {
    private long code = System.currentTimeMillis()+System.nanoTime();
    private AtomicReference<Entry> ref = new AtomicReference(Entry.EMPTY);
    private final AtomicLong time = new AtomicLong(System.currentTimeMillis());

    public void addEntry(Entry newEntry) {
        Entry oldEntry;
        do {
            oldEntry = ref.get();
        } while (!ref.compareAndSet(oldEntry, newEntry));
        update();
    }

    public void update() {
        time.compareAndSet(-1, System.currentTimeMillis());
    }


    public Entry isExpired(long t){
        long current = time.get();
        if(System.currentTimeMillis() - current > t){
            time.compareAndSet(current, -1);
            return ref.get();
        }
        return Entry.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryWithTime that = (EntryWithTime) o;
        if (code != that.code) return false;
        return ref.get().key().equals(that.ref.get().key());
    }

    @Override
    public int hashCode() {
        int result = (int) (code ^ (code >>> 32));
        result = 31 * result + ref.get().key().hashCode();
        return result;
    }

    public static final EntryWithTime EMPTY = new EntryWithTime();

}
