package com.cxh.androidmedia.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Cxh
 * Time : 2018-12-11  21:07
 * Desc :
 */
public abstract class AsyncTask<R> implements Runnable {

    protected abstract R doWork() throws Exception;

    protected abstract void onSuccess(R result);

    protected abstract void onFailed(Exception e);

    public void execute() {
        ThreadPoolManager.getInstance().execute(this);
    }

    @Override
    public void run() {
        // 结果在主线
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    onSuccess(doWork());
                } catch (Exception e) {
                    onFailed(e);
                }
            }
        });
    }
}
