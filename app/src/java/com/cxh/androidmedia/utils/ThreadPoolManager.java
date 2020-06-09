package com.cxh.androidmedia.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Cxh
 * Time : 2018-09-25  15:12
 * Desc :
 */
public class ThreadPoolManager {

    private volatile static ThreadPoolManager INSTANCE = null;

    private ExecutorService mExecutorService;

    private ThreadPoolManager(){
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static ThreadPoolManager getInstance() {
        if (null == INSTANCE) {
            synchronized (ThreadPoolManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ThreadPoolManager();
                }
            }
        }

        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

}
