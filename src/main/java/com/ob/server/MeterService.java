package com.ob.server;

public interface MeterService {
    int incrementCounter(final String counterName, final String... tags);

    int decrementCounter(final String counterName, final String... tags);

    void record(final String key, final long start, final String... tags);
}
