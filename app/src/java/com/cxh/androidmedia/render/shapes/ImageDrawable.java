package com.cxh.androidmedia.render.shapes;

import android.opengl.GLES30;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2019-03-17  16:25
 * Desc : 绘制纹理
 */
public class ImageDrawable extends BaseDrawable {

    /**
     * 与普通形状绘制基本相同，变化的地方
     * 1. 将原来顶点颜色处理程序变为了纹理处理；
     * 2. 变换矩阵因为显示图片具有宽高尺寸在获取的时候发生变化。
     */

    private static final String VERTEX_SHADER = "" +
            "attribute vec4 vPosition; " +
            "attribute vec2 aTextureCoord; " +
            "uniform mat4 u_Matrix; " +
            "varying vec2 vTexCoord; " +
            "void main(){ " +
            "gl_Position = vPosition * u_Matrix; " +
            "vTexCoord = aTextureCoord; " +
            "}";
    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float; " +
            "uniform sampler2D uTextureUnit; " +
            "varying vec2 vTexCoord; " +
            "void main(){ " +
            "gl_FragColor = texture2D(uTextureUnit,vTexCoord); " +
            "}";

    private static float[] TEXTURE_ARRAY = {
            0, 0,
            1f, 0,
            1f, 1f,
            0, 1f
    };

    private static short[] VERTEX_ARRAY = {
            0, 1, 2,
            0, 2, 3
    };

    private float[] getPositionArray(float x, float y) {
        return new float[]{
                -x, y, 0,
                x, y, 0,
                x, -y, 0,
                -x, -y, 0
        };
    }

    private int mGLProgram;
    private int mPositionHandler;
    private int mTextureHandler;
    private int mMatrixHandler;
    private BitmapTexture mBitmapTexture;

    public ImageDrawable() {

        int vsh = loadShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fsh = loadShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        // 创建空的OpenGL ES程序
        mGLProgram = GLES30.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES30.glAttachShader(mGLProgram, vsh);
        // 添加片段着色器到程序中
        GLES30.glAttachShader(mGLProgram, fsh);
        // 创建OpenGL ES程序可执行文件
        GLES30.glLinkProgram(mGLProgram);
        // 使程序生效
        GLES30.glValidateProgram(mGLProgram);

        // 删除着色器指针
        GLES30.glDeleteShader(vsh);
        GLES30.glDeleteShader(fsh);

        printLog(mGLProgram);

        mBitmapTexture = OpenGLUtils.loadTexture(AMApp.get(), R.drawable.beauty5);
    }

    @Override
    public void draw(float[] matrix, int width, int height) {
        mBitmapTexture.calculateScale(width, height);

        // 将程序添加到OpenGL ES环境
        GLES30.glUseProgram(mGLProgram);
        // 设置变换矩阵
        mMatrixHandler = GLES30.glGetUniformLocation(mGLProgram, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, matrix, 0);

        float[] vertextArray = getPositionArray(mBitmapTexture.mVertexScaleX, mBitmapTexture.mVertexScaleY);
        // 获取顶点着色器的句柄
        mPositionHandler = GLES30.glGetAttribLocation(mGLProgram, "vPosition");
        // 设置三角形坐标数据
        GLES30.glVertexAttribPointer(mPositionHandler, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(vertextArray));
        // 启用顶点着色器句柄
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        // 纹理坐标
        mTextureHandler = GLES30.glGetAttribLocation(mGLProgram, "aTextureCoord");
        // 设置纹理坐标
        GLES30.glVertexAttribPointer(mTextureHandler, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(TEXTURE_ARRAY));
        // 启用纹理属性句柄
        GLES30.glEnableVertexAttribArray(mTextureHandler);

        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBitmapTexture.mTextureId);
        // 绘制三角形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, VERTEX_ARRAY.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(VERTEX_ARRAY));

        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mPositionHandler);
        GLES30.glDisableVertexAttribArray(mTextureHandler);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    @Override
    public void release() {
        super.release();
    }
}
