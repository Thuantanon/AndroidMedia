package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.ClassicDrawRender;
import com.cxh.androidmedia.render.IDrawableProvider;
import com.cxh.androidmedia.render.beauty.HighFeatureDrawable;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : VBO、VAO、PBO、RBO、FBO等高级特性，离屏渲染
 */
public class GLRender4Activity extends BaseActivity implements IDrawableProvider {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;

    private ClassicDrawRender mDrawRender;
    private HighFeatureDrawable mFeatureDrawable;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_4;
    }

    @Override
    protected void init() {

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setEGLContextClientVersion(2);
        mDrawRender = new ClassicDrawRender(this);
        mSurfaceView.setRenderer(mDrawRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    @Override
    public BaseDrawable getDrawable() {
        mFeatureDrawable = new HighFeatureDrawable();
        return mFeatureDrawable;
    }
}
