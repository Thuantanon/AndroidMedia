package com.cxh.androidmedia.render.beauty;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.activity.GLRender2Activity;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

import java.nio.ByteBuffer;

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
    private RectF mShotRect;

    private Handler mMainHandler;
    private boolean mIsSaveImage;

    public DeformCanvasDrawable(Handler handler) {
        mMainHandler = handler;

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

    public DeformCanvasDrawable() {
        this(new Handler(Looper.getMainLooper()));
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

        float[] scaleArray = {mWhiteScale};
        mWhiteScaleHandler = GLES30.glGetAttribLocation(mGLProgram, "whiteScale");
        GLES30.glVertexAttribPointer(mWhiteScaleHandler, 1, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(scaleArray));
        GLES30.glEnableVertexAttribArray(mWhiteScaleHandler);

        short[] vertexIndex = {0, 1, 2, 0, 2, 3};
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBgTexture.mTextureId);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, vertexIndex.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(vertexIndex));

        if (mIsSaveImage) {
            savePicture(width, height);
            mIsSaveImage = false;
        }
        GLES30.glDisableVertexAttribArray(mMatrixHandler);
        GLES30.glDisableVertexAttribArray(mVertexHandler);
        GLES30.glDisableVertexAttribArray(mTextureHandler);
        GLES30.glDisableVertexAttribArray(mWhiteScaleHandler);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

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


    public void setWhiteScale(float whiteScale) {
        mWhiteScale = whiteScale;
    }

    public void setCurrentAngle(int currentAngle) {
        mCurrentAngle = currentAngle;
    }

    public void setSizeScale(float sizeScale) {
        mSizeScale = sizeScale;
    }

    public void setSaveImage(boolean saveImage) {
        mIsSaveImage = saveImage;
    }

    public void setShotRect(RectF shotRect) {
        mShotRect = shotRect;
    }

    /**
     * 必须在glDrawArrays之前调用
     *
     * @param width
     * @param height
     */
    public void savePicture(int width, int height) {

        int realX = 0;
        int realY = 0;
        int realWidth = width;
        int realHeight = height;
        if (null != mShotRect) {
            // 这里坐标系y方向和View是反的
            realX = (int) mShotRect.left;
            realY = (int) (height - mShotRect.bottom);
            realWidth = (int) (mShotRect.right - mShotRect.left);
            realHeight = (int) (mShotRect.bottom - mShotRect.top);
        }
        ByteBuffer imageBuffer = ByteBuffer.allocate(realWidth * realHeight * 4);
        // 裁剪
        GLES30.glReadPixels(realX, realY, realWidth, realHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, imageBuffer);

        Bitmap bmpSource = Bitmap.createBitmap(realWidth, realHeight, Bitmap.Config.ARGB_8888);
        bmpSource.copyPixelsFromBuffer(imageBuffer);

        // 获取到的图像是上下镜像的，需要翻转
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setScale(1, -1);
        Bitmap bmpMirror = Bitmap.createBitmap(bmpSource, 0, 0, realWidth, realHeight, matrix, true);

        Bitmap bmpTarget = Bitmap.createBitmap(realWidth, realHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpTarget);
        Rect drawRect = new Rect(0, 0, realWidth, realHeight);
        canvas.drawBitmap(bmpMirror, drawRect, drawRect, null);
        bmpSource.recycle();
        bmpMirror.recycle();

        Message message = mMainHandler.obtainMessage();
        message.what = GLRender2Activity.MSG_SHOW_IMAGE_DIALOG;
        message.obj = bmpTarget;
        message.arg1 = realWidth;
        message.arg2 = realHeight;
        mMainHandler.sendMessage(message);
    }
}
