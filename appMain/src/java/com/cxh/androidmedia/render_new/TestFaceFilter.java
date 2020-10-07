package com.cxh.androidmedia.render_new;

import android.opengl.GLES30;
import android.opengl.GLES30;

import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-09-19  02:45
 * Desc :
 */
public class TestFaceFilter {

    private static final String VERTEX_SHADER = "" +
            "attribute vec2 aPosition; \n" +
            "void main(){ \n" +
            "gl_Position = vec4(aPosition, 0.0, 1.0); \n" +
            "gl_PointSize = 10.0; \n" +
            "} ";

    private static final String FRAG_SHADER = "" +
            "precision mediump float; \n" +
            "void main() { \n" +
            "gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0); \n" +
            "} ";

    private int mProgramId;
    private int mPositionLocation;

    public TestFaceFilter() {
        mProgramId = OpenGLUtils.loadProgram(VERTEX_SHADER, FRAG_SHADER);
        mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "aPosition");
    }

    public void draw(float[] points) {
        GLES30.glUseProgram(mProgramId);
        GLES30.glEnableVertexAttribArray(mPositionLocation);
        GLES30.glVertexAttribPointer(mPositionLocation, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(points));
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, points.length / 2);
        GLES30.glUseProgram(0);
    }
}
