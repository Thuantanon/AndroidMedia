package com.cxh.mp3lame;

/**
 * Created by Cxh
 * Time : 2020-11-01  17:06
 * Desc :
 */
public class LameEngine {

    static {
        System.loadLibrary("mp3lame");
    }

    public static void nativeInit() {

    }

    public static void nativeRelease() {

    }

    public static native String getNameVersion();
}
