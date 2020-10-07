package com.cxh.androidmedia.render_new.filter;

/**
 * Created by Cxh
 * Time : 2020-09-17  00:55
 * Desc :
 */
public abstract class BaseGLBeautyFilter {

    protected int mWidth;
    protected int mHeight;
    protected float mScale;

    public BaseGLBeautyFilter(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * @param textureId 输入纹理
     * @param width     宽高
     * @param height    宽高
     * @return 输出纹理
     */
    public abstract int draw(int textureId, int width, int height);

    public void release() {

    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getScale() {
        return mScale;
    }
}
