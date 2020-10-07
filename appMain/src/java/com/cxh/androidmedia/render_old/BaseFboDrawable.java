package com.cxh.androidmedia.render_old;

import android.opengl.Matrix;

/**
 * Created by Cxh
 * Time : 2020-08-31  23:53
 * Desc :  因为初学考虑不周全，懒得改既有代码了，凑合下。。。后面render_beauty重写
 */
public abstract class BaseFboDrawable extends BaseDrawable {

    private int mWidth;
    private int mHeight;

    @Override
    public void draw(float[] matrix, int width, int height) {
        // do nothing
        drawFBO(0, width, height);
    }

    protected float[] getAspectMatrix(float width, float height) {
        float ratio = width > height ? width / height : height / width;
        float[] matrix = new float[16];
        if (width > height) {
            Matrix.orthoM(matrix, 0, -ratio, ratio, -1, 1f, -1.f, 1f);
        } else {
            Matrix.orthoM(matrix, 0, -1, 1f, -ratio, ratio, -1.f, 1f);
        }
        return matrix;
    }

    // 本来想封装个流水线的，懒得写了就这样吧
    public abstract int drawFBO(int textureId, int width, int height);

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }
}
