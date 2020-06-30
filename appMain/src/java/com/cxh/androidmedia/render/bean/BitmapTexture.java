package com.cxh.androidmedia.render.bean;

import com.cxh.androidmedia.utils.CCLog;

/**
 * Created by Cxh
 * Time : 2019-06-01  17:33
 * Desc :
 */
public class BitmapTexture {

    public int mTextureId;
    public float mBitmapWidth;
    public float mBitmapHeight;
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
}
