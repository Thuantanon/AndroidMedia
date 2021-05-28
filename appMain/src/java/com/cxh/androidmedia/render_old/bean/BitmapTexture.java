package com.cxh.androidmedia.render_old.bean;

import android.opengl.GLES30;

import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2019-06-01  17:33
 * Desc :
 */
public class BitmapTexture {

    public int mTextureId = OpenGLUtils.NO_TEXTURE;
    public int mBitmapWidth;
    public int mBitmapHeight;
    // x,y方向的缩放
    public float mVertexScaleX;
    public float mVertexScaleY;

    /**
     * 计算适配缩放比例
     *
     * @param width  渲染表面宽度
     * @param height 渲染表面高度
     */
    public void calculateScale(float width, float height) {
        if (mBitmapWidth <= 0 || mBitmapHeight <= 0) {
            CCLog.i(" calculateScale : " + mBitmapWidth + "," + mBitmapHeight);
            return;
        }
        // 竖屏
        float bitmapRate = mBitmapWidth / mBitmapHeight;
        if (width <= height) {
            mVertexScaleX = 1f;
            mVertexScaleY = 1f / bitmapRate;
        } else {
            mVertexScaleX = 1f / bitmapRate;
            mVertexScaleY = 1f;
        }
    }

    public void release() {
        if (OpenGLUtils.NO_TEXTURE != mTextureId) {
            GLES30.glDeleteTextures(1, new int[]{mTextureId}, 0);
            mTextureId = OpenGLUtils.NO_TEXTURE;
        }
    }
}
