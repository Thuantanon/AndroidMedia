package com.cxh.androidmedia.render_old.shapes;

import android.opengl.GLES30;

import com.cxh.androidmedia.render_old.BaseDrawable;
import com.cxh.androidmedia.utils.BitsUtil;

/**
 * Created by Cxh
 * Time : 2019-03-07  19:13
 * Desc :
 */
public class RectDrawable extends BaseDrawable {

    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix; " +
            "attribute vec4 vPosition; " +
            "attribute vec4 aColor; " +
            "varying vec4 vColor; " +
            "void main(){" +
            "gl_Position = vPosition * u_Matrix; " +
            "vColor = aColor; " +
            "}";
    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float; " +
            "varying vec4 vColor;" +
            "void main(){ " +
            "gl_FragColor = vColor;" +
            "}";
    private static final int COORDS_PER_VERTEX = 3;
    private static float[] rectVertex = {
            -0.5f, 0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };
    private static float[] rectColor = {
            0f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 1f, 1f,
            0f, 1f, 1f, 1f,
            01f, 0f, 0f, 1f,
    };

    private int mVertexCount = rectVertex.length / COORDS_PER_VERTEX;
    private int mGLTextureID;
    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    public RectDrawable() {

        int vsh = loadShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fsh = loadShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        // 创建空的OpenGL ES程序
        mGLTextureID = GLES30.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES30.glAttachShader(mGLTextureID, vsh);
        // 添加片段着色器到程序中
        GLES30.glAttachShader(mGLTextureID, fsh);
        // 创建OpenGL ES程序可执行文件
        GLES30.glLinkProgram(mGLTextureID);
        // 使程序生效
        GLES30.glValidateProgram(mGLTextureID);

        // 删除着色器指针
        GLES30.glDeleteShader(vsh);
        GLES30.glDeleteShader(fsh);

        printLog(mGLTextureID);
    }

    @Override
    public void draw(float[] matrix, int width, int height) {

        // 将程序添加到OpenGL ES环境
        GLES30.glUseProgram(mGLTextureID);

        // 设置变换矩阵
        mMatrixHandler = GLES30.glGetUniformLocation(mGLTextureID, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, matrix, 0);

        // 获取顶点着色器的句柄
        mPositionHandler = GLES30.glGetAttribLocation(mGLTextureID, "vPosition");
        // 设置三角形坐标数据
        GLES30.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectVertex));
        // 启用顶点着色器句柄
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        // 获取片源着色器的color句柄
        mColorHandler = GLES30.glGetAttribLocation(mGLTextureID, "aColor");
        // 上色
        GLES30.glVertexAttribPointer(mColorHandler, 4,  GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectColor));
        // 启用
        GLES30.glEnableVertexAttribArray(mColorHandler);

        // 获取颜色句柄
        // mColorHandler = GLES30.glGetUniformLocation(mGLTextureID, "vColor");
        // 设置绘制三角形的颜色
        // GLES30.glUniform4fv(mColorHandler, 1, rectColor, 0);
        // 绘制三角形
        // GLES30.GL_TRIANGLES 累计三个点绘制
        // GLES30.GL_TRIANGLE_STRIP 相邻三个点绘制，绘制出的图形总是连接在一起的
        // GLES30.GL_TRIANGLE_FAN 以第一个点为基础，依此取点绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCount);

        // 禁用顶点数组
        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mPositionHandler);
        GLES30.glDisableVertexAttribArray(mColorHandler);
    }
}
