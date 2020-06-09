package com.cxh.androidmedia.render.shapes;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.utils.BitsUtil;

/**
 * Created by Cxh
 * Time : 2019-03-19  17:04
 * Desc : 绘制立方体
 */
public class CubeDrawable extends BaseDrawable {

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
    private static final float R = 0.5f;

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

    private int mVertexCount = rectVertex.length / COORDS_PER_VERTEX;
    private int mGLTextureID;
    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    private int mCameraAngle;

    public CubeDrawable() {

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
        // 使纹理生效
        GLES30.glValidateProgram(mGLTextureID);

        // 删除着色器
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
        float[] combineMatrix = move(matrix, width, height);
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, combineMatrix, 0);

        // 获取顶点着色器的句柄
        mPositionHandler = GLES30.glGetAttribLocation(mGLTextureID, "vPosition");
        // 设置三角形坐标数据
        GLES30.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectVertex));
        // 启用顶点着色器句柄
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        // 获取片源着色器的color句柄
        mColorHandler = GLES30.glGetAttribLocation(mGLTextureID, "aColor");
        // 上色
        GLES30.glVertexAttribPointer(mColorHandler, 4, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(rectColor));
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

    /**
     *
     * @return
     */
    private float[] move(float[] matrix, int width, int height) {
        mCameraAngle++;
        if (mCameraAngle > 360) {
            mCameraAngle = 0;
        }

        float[] viewMatrix = new float[16];
        float[] targetMatrix = new float[16];
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setRotateM(viewMatrix, 0, mCameraAngle, 1f, -1, -1);
        Matrix.multiplyMM(targetMatrix, 0, viewMatrix, 0, matrix, 0);

        return targetMatrix;
    }

}
