package com.cxh.androidmedia.render.beauty;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

import java.nio.IntBuffer;

/**
 * Created by Cxh
 * Time : 2020-06-08  23:54
 * Desc :
 */
public class DeformCanvasDrawable extends BaseDrawable {

    private BitmapTexture mBgTexture;
    private BitmapTexture mWaterTexture;

    private int mGLProgram;
    private int mMatrixHandler;
    private int mVertexHandler;
    private int mTextureHandler;
    private int mWhiteScaleHandler;

    private int mCurrentAngle;
    private float mWhiteScale;
    private float mSizeScale;

    public DeformCanvasDrawable() {

        mGLProgram = GLES30.glCreateProgram();

        String vertexCode = FileUtil.readRenderScriptFromAssets(AMApp.get(), "glsl/render2/render_vertex_image.glsl");
        String fragmentCode = FileUtil.readRenderScriptFromAssets(AMApp.get(), "glsl/render2/render_fragment_image.glsl");
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexCode);
        int fragShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentCode);

        GLES30.glAttachShader(mGLProgram, vertexShader);
        GLES30.glAttachShader(mGLProgram, fragShader);
        GLES30.glLinkProgram(mGLProgram);
        GLES30.glValidateProgram(mGLProgram);
        printLog(mGLProgram);

        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragShader);

        mBgTexture = OpenGLUtils.loadTexture(AMApp.get(), R.drawable.beauty5);
    }

    @Override
    public void draw(float[] matrix, int width, int height) {
        mBgTexture.calculateScale(width, height);

        GLES30.glUseProgram(mGLProgram);
        mMatrixHandler = GLES30.glGetUniformLocation(mGLProgram, "uMatrix");
        float[] combineMatrix = setRotateM(matrix, mCurrentAngle);
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, combineMatrix, 0);


        float[] vertexArray = getPositionArray(mBgTexture.mVertexScaleX, mBgTexture.mVertexScaleY);
        mVertexHandler = GLES30.glGetAttribLocation(mGLProgram, "vertexPosition");
        GLES30.glVertexAttribPointer(mVertexHandler, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(vertexArray));
        GLES30.glEnableVertexAttribArray(mVertexHandler);

        float[] texArray = {0, 0, 1f, 0, 1f, 1f, 0, 1f};
        mTextureHandler = GLES30.glGetAttribLocation(mGLProgram, "textureCoord");
        GLES30.glVertexAttribPointer(mTextureHandler, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(texArray));
        GLES30.glEnableVertexAttribArray(mTextureHandler);

        float[] sclaeArray = {mWhiteScale};
        mWhiteScaleHandler = GLES30.glGetAttribLocation(mGLProgram, "whiteScale");
        GLES30.glVertexAttribPointer(mWhiteScaleHandler, 1, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(sclaeArray));
        GLES30.glEnableVertexAttribArray(mWhiteScaleHandler);

        short[] vertexIndex = {0, 1, 2, 0, 2, 3};
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBgTexture.mTextureId);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, vertexIndex.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(vertexIndex));

        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mVertexHandler);
        GLES30.glDisableVertexAttribArray(mTextureHandler);
        GLES30.glDisableVertexAttribArray(mWhiteScaleHandler);

    }

    private float[] getPositionArray(float x, float y) {
        float sizeScale = Math.min(1.0f - mSizeScale, 1.0f);
        x *= sizeScale;
        y *= sizeScale;
        return new float[]{
                -x, y, 0,
                x, y, 0,
                x, -y, 0,
                -x, -y, 0
        };
    }

    private float[] setRotateM(float[] matrix, int rotateAngle) {
        float[] viewMatrix = new float[16];
        float[] targetMatrix = new float[16];
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setRotateM(viewMatrix, 0, rotateAngle, 0f, 0f, 1f);
        Matrix.multiplyMM(targetMatrix, 0, viewMatrix, 0, matrix, 0);
        return targetMatrix;
    }

    /**
     * 采用glReadPixels获取屏幕数据
     *
     * @return
     */
    public int[] getImagePixelData(int x, int y, int width, int height) {
        IntBuffer byteBuffer = IntBuffer.allocate(width * height);
        GLES30.glReadPixels(x, y, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_INT, byteBuffer);
        return byteBuffer.array();
    }

    public void setWhiteScale(float whiteScale) {
        mWhiteScale = whiteScale;
    }

    public void setCurrentAngle(int currentAngle) {
        mCurrentAngle = currentAngle;
    }

    public void setSizeScale(float sizeScale) {
        mSizeScale = sizeScale;
    }
}
