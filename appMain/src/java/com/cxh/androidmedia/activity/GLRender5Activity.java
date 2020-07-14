package com.cxh.androidmedia.activity;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : 使用GLSurfaceView预览Camera
 */
public class GLRender5Activity extends BaseActivity {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_5;
    }

    @Override
    protected void init() {



    }


}
