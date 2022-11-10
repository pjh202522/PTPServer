package com.pjh.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yueyinhaibao
 */
public class PTPThreadManager {

    private static ThreadPoolExecutor threadPool;

    static {
        threadPool = new ThreadPoolExecutor(10, 50, 15, TimeUnit.MINUTES, new ArrayBlockingQueue<>(40), r -> new Thread(r));
    }

    public static void submit(Runnable task) {
        if(threadPool != null) {
            threadPool.submit(task);
        } else {
            throw new RuntimeException("thread pool is not initialize");
        }
    }

}
