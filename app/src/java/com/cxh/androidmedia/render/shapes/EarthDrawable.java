package com.cxh.androidmedia.render.shapes;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.bean.BitmapTexture;
import com.cxh.androidmedia.render.bean.GLPoint2;
import com.cxh.androidmedia.render.bean.GLPoint3;
import com.cxh.androidmedia.render.bean.GLTriangle2;
import com.cxh.androidmedia.render.bean.GLTriangle3;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.OpenGLUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cxh
 * Time : 2019-06-07  16:25
 * Desc : 绘制地球
 */
public class EarthDrawable extends BaseDrawable {

    /**
     * 先求出所有圆的顶点，再依次渲染每个多边形，得到一个地球
     * 画一个半径为mRadius，圆心为0的球，将世界地图贴再表面
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
//            "gl_FragColor = vec4(1f, 1f,1f, 1f); " +
            "}";

    private final float mRadius = 0.9f;
    private final int mBlockCount = 360;

    private float[] mVertexArray;
    private float[] mTextureArray;

    private int mGLProgram;
    private int mPositionHandler;
    private int mTextureHandler;
    private int mMatrixHandler;
    private float mCameraAngle;
    private BitmapTexture mBitmapTexture;

    public EarthDrawable() {

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
        // 使纹理生效
        GLES30.glValidateProgram(mGLProgram);

        // 删除着色器
        GLES30.glDeleteShader(vsh);
        GLES30.glDeleteShader(fsh);

        printLog(mGLProgram);

        mBitmapTexture = OpenGLUtils.loadTexture(AMApp.get(), R.drawable.earth);

        initPositionAndTexture();
    }

    @Override
    public void draw(float[] matrix, int width, int height) {

        // 将程序添加到OpenGL ES环境
        GLES30.glUseProgram(mGLProgram);
        // 设置变换矩阵
        mMatrixHandler = GLES30.glGetUniformLocation(mGLProgram, "u_Matrix");
        float[] combineMatrix = move(matrix, width, height);
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, combineMatrix, 0);

        // 获取顶点着色器的句柄
        mPositionHandler = GLES30.glGetAttribLocation(mGLProgram, "vPosition");
        // 设置三角形坐标数据
        GLES30.glVertexAttribPointer(mPositionHandler, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(mVertexArray));
        // 启用顶点着色器句柄
        GLES30.glEnableVertexAttribArray(mPositionHandler);

        // 纹理坐标
        mTextureHandler = GLES30.glGetAttribLocation(mGLProgram, "aTextureCoord");
        // 设置纹理坐标
        GLES30.glVertexAttribPointer(mTextureHandler, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(mTextureArray));
        // 启用纹理属性句柄
        GLES30.glEnableVertexAttribArray(mTextureHandler);

        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBitmapTexture.mTextureId);
        // 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexArray.length / 3);

        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mPositionHandler);
        GLES30.glDisableVertexAttribArray(mTextureHandler);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    @Override
    public void release() {
        super.release();
    }

    /**
     * @return
     */
    private float[] move(float[] matrix, int width, int height) {
        mCameraAngle -= 0.5f;
        if (mCameraAngle <= 0) {
            mCameraAngle = 360f;
        }

        float[] viewMatrix = new float[16];
        float[] targetMatrix = new float[16];
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setRotateM(viewMatrix, 0, mCameraAngle, 0f, 1f, 0f);
        Matrix.multiplyMM(targetMatrix, 0, viewMatrix, 0, matrix, 0);

        return targetMatrix;
    }

    private void initPositionAndTexture() {
        List<GLTriangle3> triangles = new ArrayList<>();
        List<GLTriangle2> textureArray = new ArrayList<>();

        // 每个方块的角度（纬度）
        float minAngle = 360f / mBlockCount;
        // 纬度只需要遍历180度，经度需要遍历360度
        int mLatitudeCount = mBlockCount / 2;
        // 纹理的最小渲染尺寸
        float minTextureBlockX = 1f / mBlockCount;
        float minTextureBlockY = 1f / mLatitudeCount;

        for (int i = 0; i < mLatitudeCount; i++) {
            float angleLatitude = minAngle * i;
            float angleLatitudeNext = minAngle * (i + 1);
            float texBlockY = minTextureBlockY * i;
            float texBlockYNext = minTextureBlockY * (i + 1);
            if (0 == i) {
                // 北极圈
                for (int j = 0; j < mBlockCount; j++) {
                    float angleLongitude = minAngle * j;
                    float angleLongitudeNext = minAngle * (j + 1);
                    float texBlockX = minTextureBlockX * j;
                    float texBlockXNext = minTextureBlockX * (j + 1);
                    // 构成北极圈的三角形顶点
                    GLPoint3 northPole = new GLPoint3(0f, 1f, 0f);
                    GLPoint3 leftPoint = new GLPoint3(angleLongitude, angleLatitudeNext);
                    GLPoint3 rightPoint = new GLPoint3(angleLongitudeNext, angleLatitudeNext);
                    GLTriangle3 triangle3 = new GLTriangle3(northPole, leftPoint, rightPoint);
                    triangles.add(triangle3);

                    // 采样区域坐标
                    GLPoint2 northPoleBlock = new GLPoint2(texBlockX, texBlockY);
                    GLPoint2 leftPointBlock = new GLPoint2(texBlockX, texBlockYNext);
                    GLPoint2 rightPointBlock = new GLPoint2(texBlockXNext, texBlockYNext);
                    GLTriangle2 triangle2 = new GLTriangle2(northPoleBlock, leftPointBlock, rightPointBlock);
                    textureArray.add(triangle2);

                }

            } else if (i == (mLatitudeCount - 1)) {
                // 南极圈
                for (int j = 0; j < mBlockCount; j++) {
                    float angleLongitude = minAngle * j;
                    float angleLongitudeNext = minAngle * (j + 1);
                    float texBlockX = minTextureBlockX * j;
                    float texBlockXNext = minTextureBlockX * (j + 1);
                    // 构成南极圈的三角形顶点
                    GLPoint3 northPole = new GLPoint3(0f, -1f, 0f);
                    GLPoint3 leftPoint = new GLPoint3(angleLongitude, angleLatitude);
                    GLPoint3 rightPoint = new GLPoint3(angleLongitudeNext, angleLatitude);
                    GLTriangle3 triangle3 = new GLTriangle3(leftPoint, rightPoint, northPole);
                    triangles.add(triangle3);

                    // 采样区域坐标
                    GLPoint2 northPoleBlock = new GLPoint2(texBlockX, texBlockY);
                    GLPoint2 leftPointBlock = new GLPoint2(texBlockX, texBlockYNext);
                    GLPoint2 rightPointBlock = new GLPoint2(texBlockXNext, texBlockYNext);
                    GLTriangle2 triangle2 = new GLTriangle2(leftPointBlock, rightPointBlock, northPoleBlock);
                    textureArray.add(triangle2);

                }

            } else {
                // 其余全是四边形（俩三角形）
                for (int j = 0; j < mBlockCount; j++) {
                    float angleLongitude = minAngle * j;
                    float angleLongitudeNext = minAngle * (j + 1);
                    float texBlockX = minTextureBlockX * j;
                    float texBlockXNext = minTextureBlockX * (j + 1);
                    // 四个顶点
                    GLPoint3 leftTopPoint3 = new GLPoint3(angleLongitude, angleLatitude);
                    GLPoint3 leftBottomPoint3 = new GLPoint3(angleLongitude, angleLatitudeNext);
                    GLPoint3 rightTopPoint3 = new GLPoint3(angleLongitudeNext, angleLatitude);
                    GLPoint3 rightBottomPoint3 = new GLPoint3(angleLongitudeNext, angleLatitudeNext);

                    GLTriangle3 triangle3Left = new GLTriangle3(leftTopPoint3, leftBottomPoint3, rightBottomPoint3);
                    GLTriangle3 triangle3Right = new GLTriangle3(leftTopPoint3, rightBottomPoint3, rightTopPoint3);
                    triangles.add(triangle3Left);
                    triangles.add(triangle3Right);

                    // 采样区域
                    GLPoint2 leftTopBlock = new GLPoint2(texBlockX, texBlockY);
                    GLPoint2 leftBottomBlock = new GLPoint2(texBlockX, texBlockYNext);
                    GLPoint2 rightTopBlock = new GLPoint2(texBlockXNext, texBlockY);
                    GLPoint2 rightBottomBlock = new GLPoint2(texBlockXNext, texBlockYNext);
                    GLTriangle2 triangle2Left = new GLTriangle2(leftTopBlock, leftBottomBlock, rightBottomBlock);
                    GLTriangle2 triangle2Right = new GLTriangle2(leftTopBlock, rightBottomBlock, rightTopBlock);
                    textureArray.add(triangle2Left);
                    textureArray.add(triangle2Right);
                }
            }
        }

        // 将对象转为顶点数组(3个点 * 3个轴xyz、纹理是UV两个轴)
        mVertexArray = new float[triangles.size() * 9];
        mTextureArray = new float[textureArray.size() * 6];
        CCLog.i("mVertexArray : " + mVertexArray.length + " , mTextureArray : " + mTextureArray.length);
        for (int i = 0; i < triangles.size(); i++) {
            GLTriangle3 triangle3 = triangles.get(i);
            GLTriangle2 triangle2 = textureArray.get(i);
            int startIndexVertex = i * 9;
            int startIndexTexture = i * 6;
            mVertexArray[startIndexVertex] = (float) (triangle3.A.x * mRadius);
            mVertexArray[startIndexVertex + 1] = (float) (triangle3.A.y * mRadius);
            mVertexArray[startIndexVertex + 2] = (float) (triangle3.A.z * mRadius);
            mVertexArray[startIndexVertex + 3] = (float) (triangle3.B.x * mRadius);
            mVertexArray[startIndexVertex + 4] = (float) (triangle3.B.y * mRadius);
            mVertexArray[startIndexVertex + 5] = (float) (triangle3.B.z * mRadius);
            mVertexArray[startIndexVertex + 6] = (float) (triangle3.C.x * mRadius);
            mVertexArray[startIndexVertex + 7] = (float) (triangle3.C.y * mRadius);
            mVertexArray[startIndexVertex + 8] = (float) (triangle3.C.z * mRadius);

            mTextureArray[startIndexTexture] = triangle2.A.x;
            mTextureArray[startIndexTexture + 1] = triangle2.A.y;
            mTextureArray[startIndexTexture + 2] = triangle2.B.x;
            mTextureArray[startIndexTexture + 3] = triangle2.B.y;
            mTextureArray[startIndexTexture + 4] = triangle2.C.x;
            mTextureArray[startIndexTexture + 5] = triangle2.C.y;
        }
    }

}
