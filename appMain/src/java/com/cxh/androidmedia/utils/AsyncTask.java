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

    public void onStart() {

    }

    public void onFinish() {

    }

    public void execute() {
        ThreadPoolManager.getInstance().execute(this);
    }

    @Override
    public void run() {
        Handler handler = new Handler(Looper.getMainLooper());
        // 结果在主线
        handler.post(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });

        try {
            R result = doWork();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(result);
                }
            });
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFailed(e);
                }
            });
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                onFinish();
            }
        });
    }
}
