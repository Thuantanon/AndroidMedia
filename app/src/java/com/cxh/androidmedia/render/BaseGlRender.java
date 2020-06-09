package com.cxh.androidmedia.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import androidx.annotation.CallSuper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cxh
 * Time : 2019-03-06  00:32
 * Desc :
 */
public abstract class BaseGlRender implements GLSurfaceView.Renderer {

    protected final float[] mProjectionMatrix = new float[16];
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    @CallSuper
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 清空屏幕颜色
        GLES30.glClearColor(0f, 0f, 0f, 1.0f);

    }

    @CallSuper
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // 设置画布的大小
        GLES30.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;

        // 主要还是长宽进行比例缩放
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        // 正交投影，大小不随距离变化
        // 利用矩阵变换将纹理投影到屏幕上
        if (width > height) {
            // 横屏。需要设置的就是左右。
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1f, -1.f, 1f);
        } else {
            // 竖屏。需要设置的就是上下
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1f, -aspectRatio, aspectRatio, -1.f, 1f);
        }

        // 透视投影，大小随距离变化
//            if (width > height) {
//                // 横屏。需要设置的就是左右。
//                Matrix.frustumM(mProjectionMatrix,0, -aspectRatio, aspectRatio,-1,1,3f,10f);
//            } else {
//                // 竖屏。需要设置的就是上下
//                Matrix.frustumM(mProjectionMatrix,0,-1, 1f, -aspectRatio, aspectRatio,3f,10f);
//            }

    }

    @CallSuper
    @Override
    public void onDrawFrame(GL10 gl) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        // 开启深度测试，防止出现穿透
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    public int getSurfaceWidth() {
        return mSurfaceWidth;
    }

    public int getSurfaceHeight() {
        return mSurfaceHeight;
    }
}
