package com.cxh.androidmedia.render_old;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cxh
 * Time : 2020-08-31  23:49
 * Desc : 由于本人也是从小白到入门，所以一开始的封装很烂，凑合啦。。
 */
public class PipelineDrawRender extends BaseGlRender {

    private IDrawableProviders mPipelineProviders;
    private List<BaseFboDrawable> mFboDrawables;
    private int mCurrentDrawableIndex;

    public PipelineDrawRender(IDrawableProviders pipelineProviders) {
        mPipelineProviders = pipelineProviders;
    }

    public void setCurrentDrawableIndex(int currentDrawableIndex) {
        mCurrentDrawableIndex = currentDrawableIndex;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        if (null != mPipelineProviders) {
            mFboDrawables = mPipelineProviders.getDrawables(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (null != mFboDrawables && mCurrentDrawableIndex >= 0 && mCurrentDrawableIndex < mFboDrawables.size()) {
            mFboDrawables.get(mCurrentDrawableIndex).draw(null, mSurfaceWidth, mSurfaceHeight);
        }
    }
}
