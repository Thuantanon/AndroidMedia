package com.cxh.androidmedia.base;

import android.app.Application;

/**
 * Created by Cxh
 * Time : 2019-03-07  19:27
 * Desc :
 */
public class AMApp extends Application {

    private static AMApp APP = null;

    @Override
    public void onCreate() {
        super.onCreate();
        APP = this;
    }

    public static AMApp get() {
        return APP;
    }
}
