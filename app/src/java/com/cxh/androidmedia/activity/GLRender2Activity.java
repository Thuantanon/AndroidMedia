package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.ClassicDrawRender;
import com.cxh.androidmedia.render.IDrawableProvider;
import com.cxh.androidmedia.render.beauty.DeformCanvasDrawable;
import com.cxh.androidmedia.utils.FileUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : OpenGL ES实现简单图片处理
 */
public class GLRender2Activity extends BaseActivity implements IDrawableProvider, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;
    @BindView(R.id.iv_check_updown)
    CheckedTextView mIvCheckUpdown;
    @BindView(R.id.ll_operator_panel)
    LinearLayout mLlOperatorPanel;
    @BindView(R.id.seekbar_white)
    SeekBar mSBWhite;
    @BindView(R.id.tv_white_progress)
    TextView mTvWhiteProgress;
    @BindView(R.id.seekbar_scale)
    SeekBar mSBScale;
    @BindView(R.id.tv_scale_progress)
    TextView mTvScaleProgress;
    @BindView(R.id.seekbar_rotate)
    SeekBar mSBRotate;
    @BindView(R.id.tv_rotate_progress)
    TextView mTvRotateProgress;

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

        mSBWhite.setOnSeekBarChangeListener(this);
        mSBScale.setOnSeekBarChangeListener(this);
        mSBRotate.setOnSeekBarChangeListener(this);
    }

    @Override
    public BaseDrawable getDrawable() {
        mDeformDrawable = new DeformCanvasDrawable();
        return mDeformDrawable;
    }


    @Override
    @OnClick({R.id.iv_check_updown, R.id.btn_save})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.iv_check_updown: {
                mIvCheckUpdown.toggle();
                setPannelHidden(!mIvCheckUpdown.isChecked());
            }
            break;
            case R.id.btn_save: {
                int width = mDrawRender.getSurfaceWidth();
                int height = mDrawRender.getSurfaceHeight();
                int[] data = mDeformDrawable.getImagePixelData(0,0, width, height);
                String filePath = FileUtil.PATH_IMAGE + File.separator + "render2_" + System.currentTimeMillis() + ".jpg";
                FileUtil.saveBitmapToStorage(data, width, height, filePath);
            }
            break;
        }
    }

    /**
     * 隐藏操作面板
     *
     * @param hide
     */
    private void setPannelHidden(boolean hide) {
        int bottomMargin = 0;
        if (hide) {
            bottomMargin = 0 - mLlOperatorPanel.getMeasuredHeight() * 2 / 3;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLlOperatorPanel.getLayoutParams();
        layoutParams.bottomMargin = bottomMargin;
        mLlOperatorPanel.setLayoutParams(layoutParams);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float scale = progress * 1f / seekBar.getMax();
        if (seekBar == mSBWhite) {

            mDeformDrawable.setWhiteScale(scale);
            mTvWhiteProgress.setText(getString(R.string.white_light, progress));
            mSurfaceView.requestRender();
        } else if (seekBar == mSBScale) {

            mDeformDrawable.setSizeScale(scale);
            mTvScaleProgress.setText(getString(R.string.white_scale, progress));
            mSurfaceView.requestRender();
        } else if ( seekBar == mSBRotate) {

            mDeformDrawable.setCurrentAngle(progress);
            mTvRotateProgress.setText(getString(R.string.white_rotate, progress));
            mSurfaceView.requestRender();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
