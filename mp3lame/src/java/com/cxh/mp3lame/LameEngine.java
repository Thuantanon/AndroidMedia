package com.cxh.mp3lame;

/**
 * Created by Cxh
 * Time : 2020-11-01  17:06
 * Desc :
 */
public class LameEngine {

    public static final int QUALITY_LOW = 7;
    public static final int QUALITY_MIDDLE = 5;
    public static final int QUALITY_HIGH = 3;

    public static final int BIT_RATE_16 = 16;

    public static final int CHANNEL_MONO = 1;
    public static final int CHANNEL_ = 2;

    static {
        System.loadLibrary("mp3lame");
    }

    public static native String getNameVersion();

    public static native void native_Init(int sampleRate, int channels, int bitRate, int quality);

    public static native boolean native_Encoder(String pcmPath, String mp3Path);

    public static native void native_Release();

}
