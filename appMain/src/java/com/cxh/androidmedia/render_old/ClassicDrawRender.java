package com.cxh.androidmedia.render_old;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cxh
 * Time : 2020-06-07  15:46
 * Desc :
 */
public class ClassicDrawRender extends BaseGlRender {

    private IDrawableProvider mIDrawableProvider;
    private BaseDrawable mDrawable;

    public ClassicDrawRender(IDrawableProvider IDrawableProvider) {
        mIDrawableProvider = IDrawableProvider;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        if (null != mIDrawableProvider) {
            mDrawable = mIDrawableProvider.getDrawable();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (null != mDrawable) {
            mDrawable.draw(mProjectionMatrix, mSurfaceWidth, mSurfaceHeight);
        }
    }
}
