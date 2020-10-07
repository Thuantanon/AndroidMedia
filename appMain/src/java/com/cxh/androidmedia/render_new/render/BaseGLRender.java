package com.cxh.androidmedia.render_new.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cxh
 * Time : 2020-09-17  01:14
 * Desc :
 */
public class BaseGLRender implements GLSurfaceView.Renderer {

    protected int mWidth;
    protected int mHeight;

    public void initRender(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void drawFrame() {

    }

    public void release() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        initRender(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawFrame();
    }
}
