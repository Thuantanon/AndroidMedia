package com.cxh.androidmedia.render_new;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cxh
 * Time : 2020-09-17  22:00
 * Desc :
 */
public class EGLHelper {

    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private boolean mbIsEglReady = false;

    public void initEGL() {
        if (mbIsEglReady) {
            return;
        }

        // 初始化EGL
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (EGL14.EGL_NO_DISPLAY == mEGLDisplay) {
            throw new RuntimeException("EGL14.eglGetDisplay error...");
        }

        // 初始化显示设备
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw new RuntimeException("EGL14.eglInitialize error...");
        }

        // EGL参数
        int[] configAttrs = {
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 8,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
        };
        EGLConfig[] eglConfig = new EGLConfig[1];
        int[] configNum = new int[1];
        EGL14.eglChooseConfig(mEGLDisplay, configAttrs, 0, eglConfig, 0, 1, configNum, 0);

        // 初始化上下文
        int[] contextArray = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, eglConfig[0], EGL14.EGL_NO_CONTEXT, contextArray, 0);

        // 初始化显示Surface
        int[] surfaceAttrs = {
                EGL14.EGL_NONE
        };
        mEGLSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, eglConfig[0], surfaceAttrs, 0);

        // 设置当前线程为绘制环境
        EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
        mbIsEglReady = true;
    }

    public void initEGL(Surface surface) {
        if (mbIsEglReady) {
            return;
        }

        // 初始化EGL
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (EGL14.EGL_NO_DISPLAY == mEGLDisplay) {
            throw new RuntimeException("EGL14.eglGetDisplay error...");
        }

        // 初始化显示设备
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw new RuntimeException("EGL14.eglInitialize error...");
        }

        // EGL参数
        int[] configAttrs = {
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 8,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
        };
        EGLConfig[] eglConfig = new EGLConfig[1];
        int[] configNum = new int[1];
        EGL14.eglChooseConfig(mEGLDisplay, configAttrs, 0, eglConfig, 0, 1, configNum, 0);

        // 初始化上下文
        int[] contextArray = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, eglConfig[0], EGL14.EGL_NO_CONTEXT, contextArray, 0);

        // 初始化显示Surface
        int[] surfaceAttrs = {
                EGL14.EGL_NONE
        };
        mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, eglConfig[0], surface, surfaceAttrs, 0);

        // 设置当前线程为绘制环境
        EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
        mbIsEglReady = true;
    }


    public void swapBuffers() {
        // 更新缓冲去数据到屏幕
        EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    public void release() {
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
            mEGLSurface = EGL14.EGL_NO_SURFACE;
        }
        if (mEGLContext != EGL14.EGL_NO_CONTEXT) {
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = EGL14.EGL_NO_CONTEXT;
        }
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglTerminate(mEGLDisplay);
            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        }

        mbIsEglReady = false;
    }

    public EGLContext getEGLContext() {
        return mEGLContext;
    }

    public EGLDisplay getEGLDisplay() {
        return mEGLDisplay;
    }

    public EGLSurface getEGLSurface() {
        return mEGLSurface;
    }
}
