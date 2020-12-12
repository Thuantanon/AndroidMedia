package com.cxh.androidmedia.render_old.test;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.render_old.BaseDrawable;
import com.cxh.androidmedia.render_old.bean.TestColorBean;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Cxh
 * Time : 2020-10-26  00:25
 * Desc :
 */
public class TestColorDrawable extends BaseDrawable {

    private static final String VERTEX_SHADER = "" +
            "attribute vec4 vPosition; " +
            "attribute vec4 aColor; " +
            "varying vec4 vColor; " +
            "varying float vSize; " +
            "void main(){" +
            "gl_Position = vec4(vPosition.x, vPosition.y, vPosition.z, 1.0); " +
            "gl_PointSize = vPosition.w;  " +
            "vSize = vPosition.w;  " +
            "vColor = aColor; " +
            "}";
    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float; " +
            "varying vec4 vColor; " +
            "varying float vSize; " +
            "void main(){ " +
            "float dis = distance(gl_PointCoord, vec2(0.5, 0.5)); " +
            "if(dis > 0.5) " +
            "{" +
            "   discard;" +
            "}" +
            "else" +
            "{" +
            "   float scale = 1.0 - pow(dis / 0.5, 2.0);" +
            "   gl_FragColor = vColor * scale;" +
            "}" +
            "}";

    private float mWidth;
    private float mHeight;
    private boolean mInited;
    private int mCurrCount;

    private int mProgramID;
    private int mPositionHandler;
    private int mColorHandler;

    private float[] mVertexList;
    private float[] mColorList;

    public TestColorDrawable() {
        mProgramID = OpenGLUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandler = GLES30.glGetAttribLocation(mProgramID, "vPosition");
        mColorHandler = GLES30.glGetAttribLocation(mProgramID, "aColor");

    }

    public void init(float width, float height) {
        mWidth = width;
        mHeight = height;
        changeColor();
    }

    @Override
    public void draw(float[] matrix, int width, int height) {
        if (!mInited) {
            mInited = true;
            init(width, height);
        }

        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES20.GL_ONE, GLES30.GL_ONE);
        GLES30.glUseProgram(mProgramID);

        GLES30.glVertexAttribPointer(mPositionHandler, 4, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(mVertexList));
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        GLES30.glVertexAttribPointer(mColorHandler, 4, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(mColorList));
        GLES30.glEnableVertexAttribArray(mColorHandler);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mCurrCount);

        // 禁用顶点数组
        GLES30.glDisableVertexAttribArray(mPositionHandler);
        GLES30.glDisableVertexAttribArray(mColorHandler);
        GLES30.glUseProgram(0);
    }

    public void changeColor() {
        Random random = new Random(System.currentTimeMillis());
        mCurrCount = 10 + random.nextInt(600);
        mVertexList = new float[mCurrCount * 4];
        mColorList = new float[mCurrCount * 4];

        for (int i = 0; i < mCurrCount; i++) {
            mVertexList[i * 4] = 1.0f - random.nextFloat() * 2.0f;
            mVertexList[i * 4 + 1] = 1.0f - random.nextFloat() * 2.0f;
            mVertexList[i * 4 + 2] = 1.0f - random.nextFloat() * 2.0f;
            mVertexList[i * 4 + 3] = random.nextFloat() * 50f;

            mColorList[i * 4] = random.nextFloat();
            mColorList[i * 4 + 1] = random.nextFloat();
            mColorList[i * 4 + 2] = random.nextFloat();
            mColorList[i * 4 + 3] = random.nextFloat();
        }
    }
}
