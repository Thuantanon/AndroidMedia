package com.cxh.androidmedia.activity.opengles;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render_new.render.FrustumGLRender;

import butterknife.BindView;

public class GLFrustumActivity extends BaseActivity {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;

    private FrustumGLRender mDrawRender;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_6;
    }

    @Override
    protected void init() {
        mDrawRender = new FrustumGLRender();
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setEGLContextClientVersion(3);
        mSurfaceView.setRenderer(mDrawRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDrawRender.onTouchEvent(event);
                return true;
            }
        });
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
}
