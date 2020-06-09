package com.cxh.androidmedia.render.shapes;

import android.opengl.GLES30;

import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.utils.BitsUtil;

/**
 * Created by Cxh
 * Time : 2019-03-19  17:12
 * Desc : 画圆其实和画矩形流程差不多，所以直接在矩形代码上修改
 */
public class RoundDrawable extends BaseDrawable {

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

    private static float[] rectVertex;
    private static float[] rectColor;

    private int mGLTextureID;
    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    private int mVertexCount;

    public RoundDrawable() {

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

        // 可以尝试绘制不同个数三角形查看效果
        initVertexAndColor(0f, 360f, 0.5f, 360);
    }

    /**
     * @param startAngle 圆的开始角度
     * @param endAngle   圆的结束角度
     * @param count      绘制三角形个数，个数越多，越圆滑
     */
    private void initVertexAndColor(float startAngle, float endAngle, float radius, int count) {
        // 所有三角形的数量, 每一度一个三角形，数量越多看起来越圆
        int vertexCount = count + 2;
        rectVertex = new float[vertexCount * 3];
        rectColor = new float[vertexCount * 4];

        float angleSpan = (endAngle - startAngle) / count;
        float currentAngle = startAngle;
        // 圆心
        rectVertex[0] = 0f;
        rectVertex[1] = 0f;
        rectVertex[2] = 0f;

        rectColor[0] = 1f;
        rectColor[1] = 1f;
        rectColor[2] = 1f;
        rectColor[3] = 1f;
        for (int i = 1; i < vertexCount; i++) {
            float sin = (float) Math.sin(currentAngle * Math.PI / 180f);
            float cos = (float) Math.cos(currentAngle * Math.PI / 180f);
            // 分别是x,y,z坐标
            float x = radius * sin;
            float y = radius * cos;
            rectVertex[3 * i] = x;
            rectVertex[3 * i + 1] = y;
            rectVertex[3 * i + 2] = 0;

            rectColor[4 * i] = 1f * sin;
            rectColor[4 * i + 1] = 1f * cos;
            rectColor[4 * i + 2] = 1f;
            rectColor[4 * i + 3] = 1f;

            currentAngle += angleSpan;
        }

        mVertexCount = rectVertex.length / 3;
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
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, mVertexCount);

        // 禁用顶点数组
        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mPositionHandler);
        GLES30.glDisableVertexAttribArray(mColorHandler);
    }
}
