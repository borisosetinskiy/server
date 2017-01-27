package com.ob.common.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by boris on 11/4/2016.
 */
public class TUtils {
    public static void shutdown(List<ExecutorService> executors){
        executors.stream().filter(executor -> !executor.isShutdown() && !executor.isTerminated()).forEach(executor -> {
            try {
                executor.shutdown();
            } catch (Exception e) { }
        });
    }
}
