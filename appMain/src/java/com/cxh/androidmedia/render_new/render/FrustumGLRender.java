package com.cxh.androidmedia.render_new.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FrustumGLRender implements GLSurfaceView.Renderer {

    private static String VERTEX_SHADER = "" +
            "attribute vec4 aVertexArr; \n" +
            "attribute vec4 aColorArr; \n" +
            "uniform mat4 mMVPMatrix; \n " +
            "varying vec4 vColorArr; \n" +
            "void main() { \n" +
            "   gl_Position = mMVPMatrix * aVertexArr; \n" +
            "   vColorArr = aColorArr; \n" +
            "}";

    private static String FRAG_SHADER = "" +
            "precision mediump float; \n" +
            "varying vec4 vColorArr; \n" +
            "void main() { \n" +
            "   gl_FragColor = vColorArr; \n" +
            "}";


    /**
     * 正方体8个点，6个面，一共需要12个三角形，绘制36个点
     * A (-R, R, R)
     * B (R, R, R)
     * C (-R, -R, R)
     * D (R, -R, R)
     * E (-R, -R, -R)
     * F (R, -R, -R)
     * G (-R, R, -R)
     * H (R, R, -R)
     */
    private static float R = 1f;
    private static float[] rectVertex = {
            // ABCD面
            // ABC
            -R, R, R,
            R, R, R,
            -R, -R, R,
            // BCD
            R, R, R,
            -R, -R, R,
            R, -R, R,

            // BDFH面
            // BDF
            R, R, R,
            R, -R, R,
            R, -R, -R,
            // BFH
            R, R, R,
            R, -R, -R,
            R, R, -R,

            // EFHG
            // EFH
            -R, -R, -R,
            R, -R, -R,
            R, R, -R,
            // EHG
            -R, -R, -R,
            R, R, -R,
            -R, R, -R,

            // ACEG
            // ACE
            -R, R, R,
            -R, -R, R,
            -R, -R, -R,
            // AEG
            -R, R, R,
            -R, -R, -R,
            -R, R, -R,

            // CDEF
            // CDE
            -R, -R, R,
            R, -R, R,
            -R, -R, -R,
            // DEF
            R, -R, R,
            -R, -R, -R,
            R, -R, -R,

            // ABGH
            // ABG
            -R, R, R,
            R, R, R,
            -R, R, -R,
            // BGH
            R, R, R,
            -R, R, -R,
            R, R, -R
    };

    /**
     * 每个点对应一种颜色，一共36个点
     * <p>
     * A (1f, 0f, 0f, 1f)
     * B (0f, 1f, 0f, 1f)
     * C (0f, 0f, 1f, 1f)
     * D (1f, 1f, 0f, 1f)
     * E (1f, 1f, 1f, 1f)
     * F (0f, 0f, 0f, 1f)
     * G (1f, 0f, 1f, 1f)
     * H (0f, 1f, 1f, 1f)
     */
    private static float[] rectColor = {
            // ABCD面
            // ABC
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            // BCD
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,

            // BDFH面
            // BDF
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f,
            0f, 0f, 0f, 1f,
            // BFH
            0f, 1f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 1f, 1f, 1f,

            // EFHG
            // EFH
            1f, 1f, 1f, 1f,
            0f, 0f, 0f, 1f,
            0f, 1f, 1f, 1f,
            // EHG
            1f, 1f, 1f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 1f, 1f,

            // ACEG
            // ACE
            1f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 1f, 1f, 1f,
            // AEG
            1f, 0f, 0f, 1f,
            1f, 1f, 1f, 1f,
            1f, 0f, 1f, 1f,

            // CDEF
            // CDE
            0f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
            // DEF
            1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
            0f, 0f, 0f, 1f,

            // ABGH
            // ABG
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 1f, 1f,
            // BGH
            0f, 1f, 0f, 1f,
            1f, 0f, 1f, 1f,
            0f, 0f, 0f, 1f
    };

    private int mProgram;
    private int mVertexHandle;
    private int mColorHandle;

    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mRotate;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(0f, 0f, 0f, 0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // 初始化GL程序
        mProgram = OpenGLUtils.loadProgram(VERTEX_SHADER, FRAG_SHADER);
        mVertexHandle = GLES30.glGetAttribLocation(mProgram, "aVertexArr");
        mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColorArr");

        Matrix.setIdentityM(mModelMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        // 主要还是长宽进行比例缩放
        float aspectRatio = (float) width / (float) height;
        // 设置相机
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 8f, 0, 0, 0, 0, 1f, 0f);
        // 设置投影
        Matrix.frustumM(mProjectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 2f, 12f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glUseProgram(mProgram);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        updateMVPMatrix();

        // 设置变换矩阵
        OpenGLUtils.setUniformMatrix4fv(mProgram, "mMVPMatrix", mMVPMatrix);

        // 设置顶点
        GLES30.glEnableVertexAttribArray(mVertexHandle);
        GLES30.glVertexAttribPointer(mVertexHandle, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectVertex));

        // 设置颜色
        GLES30.glEnableVertexAttribArray(mColorHandle);
        GLES30.glVertexAttribPointer(mColorHandle, 4, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectColor));

        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, rectVertex.length / 3);

        // 禁用
        GLES30.glDisableVertexAttribArray(mVertexHandle);
        GLES30.glDisableVertexAttribArray(mColorHandle);
    }

    private void updateMVPMatrix() {
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mMVMatrix, 0);
    }

    private float mRotateX;
    private float mRotateY;
    private float mLastX;
    private float mLastY;

    public void onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mRotateY = (ev.getX() - mLastX) / 10f;
                mRotateX = (ev.getY() - mLastY) / 10f;
                Matrix.rotateM(mModelMatrix, 0, mRotateX, 1f, 0, 0);
                Matrix.rotateM(mModelMatrix, 0, mRotateY, 0, 1f, 0);
                break;
            case MotionEvent.ACTION_UP:
                mLastX = 0;
                mLastY = 0;
                break;
        }
    }
}
