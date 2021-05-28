package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.os.Message;
import android.view.View;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render_old.BaseDrawable;
import com.cxh.androidmedia.render_old.ClassicDrawRender;
import com.cxh.androidmedia.render_old.IDrawableProvider;
import com.cxh.androidmedia.render_old.test.TestColorDrawable;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : 使用OpenGL ES预览Camera2
 */
public class GLCamera2Activity extends BaseActivity implements IDrawableProvider {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;

//    private Camera2PreviewPresenter mPreviewPresenter;

    private ClassicDrawRender mDrawRender;
    private TestColorDrawable mColorDrawable;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_5;
    }

    @Override
    protected void init() {
//        mPreviewPresenter = new Camera2PreviewPresenter(this);
//        mPreviewPresenter.onCreate();
        mDrawRender = new ClassicDrawRender(this);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setEGLContextClientVersion(3);
        mSurfaceView.setRenderer(mDrawRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
    }

    @Override
    public BaseDrawable getDrawable() {
        mColorDrawable = new TestColorDrawable();
        return mColorDrawable;
    }

    @Override
    public void onViewClick(View view) {
        super.onViewClick(view);
        mColorDrawable.changeColor();
    }
}
