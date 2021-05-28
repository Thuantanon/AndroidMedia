package com.cxh.androidmedia.render_new;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES30;

import com.cxh.androidmedia.jni.OpenGLHelper;
import com.cxh.androidmedia.render_old.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.OpenGLUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Cxh
 * Time : 5/23/21  9:30 PM
 * Desc :
 */
public class FBOImageProcessor {

    private static final String VERTEX_SHADER_FBO = "" +
            "attribute vec4 a_VertexCoord; \n" +
            "attribute vec2 a_TextureCoord; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "gl_Position = a_VertexCoord; \n" +
            "v_TextureCoord = a_TextureCoord; \n" +
            "}";

    private static final String FRAG_SHADER_FBO = "" +
            "precision highp float; \n" +
            "uniform sampler2D mTextureUnit; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "vec4 color = texture2D(mTextureUnit, v_TextureCoord); \n" +
            "color = vec4(1.0 - color.a, 1.0 - color.g, 1.0 - color.b, color.a); \n" +
            "gl_FragColor = color; \n" +
            "} ";

    private static float[] VERTEX_ARRAY = {
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
    };

    private static float[] TEXTURE_ARRAY = {
            0, 0,
            1f, 0,
            1f, 1f,
            0, 1f
    };

    private static short[] VERTEX_INDEX = {
            0, 1, 2,
            0, 2, 3
    };

    private int mFrameBufferId = 0;
    private BitmapTexture mBitmapTexture;
    private BitmapTexture mFBOTexture;
    private int[] mPBOIds = new int[1];
    private int mFrameWidth;
    private int mFrameHeight;

    private int mProgramId;
    private int mVertexLoc;
    private int mTextureLoc;

    public void prepareDraw(Bitmap bitmap) {
        mFrameWidth = bitmap.getWidth();
        mFrameHeight = bitmap.getHeight();
        mBitmapTexture = OpenGLUtils.loadTexture(bitmap);
        mFBOTexture = OpenGLUtils.createEmptyTexture(mFrameWidth, mFrameHeight);
        mFrameBufferId = OpenGLUtils.createFBO(mFBOTexture.mTextureId);

        GLES30.glGenBuffers(1, mPBOIds, 0);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPBOIds[0]);
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, mFrameWidth * mFrameHeight * 4, null, GLES30.GL_STATIC_READ);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);

        mProgramId = OpenGLUtils.loadProgram(VERTEX_SHADER_FBO, FRAG_SHADER_FBO);
        mVertexLoc = GLES30.glGetAttribLocation(mProgramId, "a_VertexCoord");
        mTextureLoc = GLES30.glGetAttribLocation(mProgramId, "a_TextureCoord");
    }

    public void draw(IBitmapCallback bitmapCallback, boolean needFBO) {
        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight);
        if (needFBO) {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId);
        }

        GLES30.glUseProgram(mProgramId);
        GLES30.glVertexAttribPointer(mVertexLoc, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(VERTEX_ARRAY));
        GLES30.glEnableVertexAttribArray(mVertexLoc);

        GLES30.glVertexAttribPointer(mTextureLoc, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(TEXTURE_ARRAY));
        GLES30.glEnableVertexAttribArray(mTextureLoc);

        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBitmapTexture.mTextureId);
        // 绘制三角形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, VERTEX_ARRAY.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(VERTEX_INDEX));
        //  解绑纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        if (null != bitmapCallback) {
            // Java方式glReadPixels
            copyPixelsToBitmap(bitmapCallback);
            // Java方式PBO
            // copyPixelsByPBO(bitmapCallback);

            // C++方式glReadPixels
            // copyPixelsToBitmapByNative(bitmapCallback);
        }

        GLES30.glDisableVertexAttribArray(mVertexLoc);
        GLES30.glDisableVertexAttribArray(mTextureLoc);
        GLES30.glUseProgram(0);
        if (needFBO) {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        }

        OpenGLUtils.checkGLError();
    }

    public void release() {
        GLES30.glDeleteFramebuffers(1, new int[]{mFrameBufferId}, 0);
        GLES30.glDeleteBuffers(1, mPBOIds, 0);
        mBitmapTexture.release();
        mFBOTexture.release();
    }


    private void copyPixelsToBitmap(IBitmapCallback bitmapCallback) {
        int bufferSize = mFrameWidth * mFrameHeight * 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        long startTime = System.currentTimeMillis();
        // 读取像素
        GLES30.glReadPixels(0, 0, mFrameWidth, mFrameHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer);

        long timeSpend = System.currentTimeMillis() - startTime;
        CCLog.i("glReadPixels, time: " + timeSpend);

        Bitmap srcBmp = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
        srcBmp.copyPixelsFromBuffer(byteBuffer);

        Matrix matrix = new Matrix();
        matrix.setScale(1f, -1f);
        Bitmap bitmap = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, false);
        srcBmp.recycle();
        if (null != bitmapCallback) {
            bitmapCallback.onBitmapReceived(bitmap);
        }
    }

    private void copyPixelsByPBO(IBitmapCallback bitmapCallback) {
        // 内存映射方式，异步读取, 可交叉使用两个PBO提升性能
        int bufferSize = mFrameWidth * mFrameHeight * 4;
        long startTime = System.currentTimeMillis();

        // 内存映射方式，异步读取
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPBOIds[0]);
        GLES30.glReadPixels(0, 0, mFrameWidth, mFrameHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, 0);
        // 等待读取完成
        Buffer buffer = GLES30.glMapBufferRange(GLES30.GL_PIXEL_PACK_BUFFER, 0, bufferSize, GLES30.GL_MAP_READ_BIT);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);

        long timeSpend = System.currentTimeMillis() - startTime;
        CCLog.i("glMapBufferRange, time: " + timeSpend);

        Bitmap srcBmp = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
        srcBmp.copyPixelsFromBuffer(buffer);

        Matrix matrix = new Matrix();
        matrix.setScale(1f, -1f);
        Bitmap bitmap = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, false);
        srcBmp.recycle();
        if (null != bitmapCallback) {
            bitmapCallback.onBitmapReceived(bitmap);
        }
    }

    private void copyPixelsToBitmapByNative(IBitmapCallback bitmapCallback) {
        long startTime = System.currentTimeMillis();

        byte[] buffer = OpenGLHelper.native_readPixels(mFrameWidth, mFrameHeight);

        long timeSpend = System.currentTimeMillis() - startTime;
        CCLog.i("native_readPixels, time: " + timeSpend + " , buffer: " + buffer.length);

        Bitmap srcBmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

//        Matrix matrix = new Matrix();
//        matrix.setScale(1f, -1f);
//        Bitmap bitmap = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, false);
//        srcBmp.recycle();
//        if (null != bitmapCallback) {
//            bitmapCallback.onBitmapReceived(bitmap);
//        }
    }


    public interface IBitmapCallback {
        void onBitmapReceived(Bitmap bitmap);
    }
}
