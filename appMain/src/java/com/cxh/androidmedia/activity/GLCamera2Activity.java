package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.os.Message;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.presenter.Camera2PreviewPresenter;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : 使用OpenGL ES预览Camera2
 */
public class GLCamera2Activity extends BaseActivity {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;

    private Camera2PreviewPresenter mPreviewPresenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_5;
    }

    @Override
    protected void init() {
        mPreviewPresenter = new Camera2PreviewPresenter(this);
        mPreviewPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
    }
}
