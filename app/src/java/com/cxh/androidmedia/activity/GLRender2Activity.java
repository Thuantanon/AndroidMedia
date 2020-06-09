package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.ClassicDrawRender;
import com.cxh.androidmedia.render.IDrawableProvider;
import com.cxh.androidmedia.render.beauty.DeformCanvasDrawable;
import com.cxh.androidmedia.utils.CCLog;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : OpenGL ES实现简单图片处理
 */
public class GLRender2Activity extends BaseActivity implements IDrawableProvider {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;
    @BindView(R.id.seekbar_white)
    SeekBar mSeekBar;
    @BindView(R.id.tv_white_progress)
    TextView mTvWhiteProgress;

    private ClassicDrawRender mDrawRender;
    private DeformCanvasDrawable mDeformDrawable;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_2;
    }

    @Override
    protected void init() {

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mDrawRender = new ClassicDrawRender(this);
        mSurfaceView.setRenderer(mDrawRender);
        // 手动重绘
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = progress * 1f / mSeekBar.getMax();
                mDeformDrawable.setWhiteScale(scale);
                mTvWhiteProgress.setText(getString(R.string.white_percent, progress));
                mSurfaceView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public BaseDrawable getDrawable() {
        mDeformDrawable = new DeformCanvasDrawable();
        return mDeformDrawable;
    }


}
