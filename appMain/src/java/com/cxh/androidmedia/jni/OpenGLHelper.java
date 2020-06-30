package com.cxh.androidmedia.jni;

import android.content.res.AssetManager;

/**
 * Created by Cxh
 * Time : 2019-06-20  01:30
 * Desc :
 */
public class OpenGLHelper {

    static {
        System.loadLibrary("android_media");
    }

    /**
     * 初始化EGL环境
     */
    public static native void glInit(AssetManager assetManager);

    /**
     * 创建绘制Surface
     *
     */
    public static native void glOnCreateSurface();

    /**
     * 绘制窗口大小变化
     *
     * @param w
     * @param h
     */
    public static native void glSizeChanged(int w, int h);

    /**
     * 绘制内容
     */
    public static native void glDrawFrame();

    /**
     * 释放EGL
     */
    public static native void glRelease();
}
