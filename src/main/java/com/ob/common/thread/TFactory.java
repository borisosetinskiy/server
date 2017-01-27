package com.ob.common.thread;

import java.util.concurrent.ThreadFactory;

/**
 * Created by boris on 11/3/2016.
 */
public class TFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
}
