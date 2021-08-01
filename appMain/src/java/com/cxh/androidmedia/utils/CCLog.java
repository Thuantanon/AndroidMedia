package com.cxh.androidmedia.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Cxh
 * Time : 2018-09-23  10:50
 * Desc :
 */
public class CCLog {

    private static final String TAG = "cai";

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}
