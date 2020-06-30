package com.cxh.androidmedia.render.shapes;

import android.opengl.GLES30;

import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.utils.BitsUtil;

/**
 * Created by Cxh
 * Time : 2019-03-07  19:12
 * Desc :
 */
public class TriangleDrawable extends BaseDrawable {

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
    private static float[] triangleVertex = {
            0f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };
    private static float[] triangleColor = {
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f
    };

    private int mVertexCount = triangleVertex.length / COORDS_PER_VERTEX;
    private int mVertexStride = COORDS_PER_VERTEX * 4;
    private int mGLTextureID;
    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    public TriangleDrawable() {

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
        /**
         * index：顶点属性的索引
         * size: 指定每个通用顶点属性的元素个数。必须是1、2、3、4。此外，glVertexAttribPointer接受符号常量gl_bgra。初始值为4（也就是涉及颜色的时候必为4）。
         * type：属性的元素类型。（上面都是Float所以使用GLES30.GL_FLOAT）；
         * normalized：转换的时候是否要经过规范化，true：是；false：直接转化；
         * stride：跨距，默认是0。（由于我们将顶点位置和颜色数据分别存放没写在一个数组中，所以使用默认值0）
         * ptr： 本地数据缓存（这里我们的是顶点的位置和颜色数据）。
         */
        GLES30.glVertexAttribPointer(mPositionHandler,
                COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, mVertexStride, BitsUtil.arraysToBuffer(triangleVertex));
        // 启用顶点着色器句柄
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        // 获取片源着色器的color句柄
        mColorHandler = GLES30.glGetAttribLocation(mGLTextureID, "aColor");
        // 上色
        /**
         * index：顶点属性的索引
         * size: 指定每个通用顶点属性的元素个数。必须是1、2、3、4。此外，glVertexAttribPointer接受符号常量gl_bgra。初始值为4（也就是涉及颜色的时候必为4）。
         * type：属性的元素类型。（上面都是Float所以使用GLES30.GL_FLOAT）；
         * normalized：转换的时候是否要经过规范化，true：是；false：直接转化；
         * stride：跨距，默认是0。（由于我们将顶点位置和颜色数据分别存放没写在一个数组中，所以使用默认值0）
         * ptr： 本地数据缓存（这里我们的是顶点的位置和颜色数据）。
         */
        GLES30.glVertexAttribPointer(mColorHandler, 4,  GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(triangleColor));
        // 启用

        GLES30.glEnableVertexAttribArray(mColorHandler);

        // 获取颜色句柄
        // mColorHandler = GLES30.glGetUniformLocation(mGLTextureID, "vColor");
        // 设置绘制三角形的颜色
        // GLES30.glUniform4fv(mColorHandler, 3, triangleColor, 0);
        // GLES30.glUniform4fv(mColorHandler, 3, BitsUtil.arraysToBuffer(triangleColor));
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
