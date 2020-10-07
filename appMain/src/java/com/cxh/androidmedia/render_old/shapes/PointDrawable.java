package com.cxh.androidmedia.render_old.shapes;

import android.opengl.GLES30;

import com.cxh.androidmedia.render_old.BaseDrawable;

/**
 * Created by Cxh
 * Time : 2019-03-07  19:12
 * Desc :
 */
public class PointDrawable extends BaseDrawable {

    private static final String VERTEX_SHADER = "" +
            "void main() { " +
            "gl_Position = vec4(0.0, 0.0, 0.0, 1.0); " +
            "gl_PointSize = 40.0;" +
            "}";
    private static final String FRAGMENT_SHADER = "" +
            "void main() { " +
            "gl_FragColor = vec4(1.0, 0, 0, 1.0); " +
            "}";
    private int mGLProgramID;

    public PointDrawable() {

        int vsh = loadShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fsh = loadShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        // 创建空的OpenGL ES程序
        mGLProgramID = GLES30.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES30.glAttachShader(mGLProgramID, vsh);
        // 添加片段着色器到程序中
        GLES30.glAttachShader(mGLProgramID, fsh);
        // 创建OpenGL ES程序可执行文件
        GLES30.glLinkProgram(mGLProgramID);
        // 使程序生效
        GLES30.glValidateProgram(mGLProgramID);

        // 删除着色器指针
        GLES30.glDeleteShader(vsh);
        GLES30.glDeleteShader(fsh);

        printLog(mGLProgramID);
    }

    @Override
    public void draw(float[] matrix, int width, int height) {

        GLES30.glUseProgram(mGLProgramID);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1);
    }

    @Override
    public void release() {
        super.release();
    }
}
