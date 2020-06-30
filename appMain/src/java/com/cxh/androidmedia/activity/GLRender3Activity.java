package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.BeautyListRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.ClassicDrawRender;
import com.cxh.androidmedia.render.IDrawableProvider;
import com.cxh.androidmedia.render.bean.BeautyBean;
import com.cxh.androidmedia.render.bean.BeautyParams;
import com.cxh.androidmedia.render.beauty.HighBeautyDrawable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:25
 * Desc : OpenGL ES、OpenCV实现高级美颜
 */
public class GLRender3Activity extends BaseActivity implements IDrawableProvider {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;
    @BindView(R.id.ll_operator_panel)
    LinearLayout mLlOperatorPanel;
    @BindView(R.id.iv_check_updown)
    CheckedTextView mIvCheckUpdown;
    @BindView(R.id.seekbar_beauty)
    SeekBar mSeekBarBeauty;
    @BindView(R.id.tv_beauty_progress)
    TextView mTvBeautyProgress;
    @BindView(R.id.rv_beauty_list)
    RecyclerView mRvBeautyList;


    private ClassicDrawRender mDrawRender;
    private HighBeautyDrawable mBeautyDrawable;
    private BeautyListRvAdapter mAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_3;
    }

    @Override
    protected void init() {

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mDrawRender = new ClassicDrawRender(this);
        mSurfaceView.setRenderer(mDrawRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mAdapter = new BeautyListRvAdapter(mContext);
        mRvBeautyList.setLayoutManager(new GridLayoutManager(mContext, 5));
        mRvBeautyList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonBaseRvAdapter.OnItemClickListener<BeautyBean>() {
            @Override
            public void onItemClick(CommonBaseRVHolder holder, BeautyBean data, int position) {
                mAdapter.setCurrentBeauty(position);
                if (BeautyParams.BEAUTY_TYPE_RESET.equals(data.getBeautyType())) {
                    BeautyParams.reset();
                    mSeekBarBeauty.setEnabled(false);
                } else {

                    int progress = (int) (data.getBeautyScale() * 100);
                    mTvBeautyProgress.setText(getString(R.string.beauty_scale, data.getBeautyName(), progress));
                    mTvBeautyProgress.setVisibility(View.VISIBLE);
                    mSeekBarBeauty.setProgress(progress);
                    mSeekBarBeauty.setEnabled(true);
                }
                mSurfaceView.requestRender();
            }
        });
        mAdapter.setList(BeautyParams.getBeautyBeans());

        mSeekBarBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = progress / 100f;
                BeautyParams.setParams(mAdapter.getCurrentKey(), scale);
                mTvBeautyProgress.setText(getString(R.string.beauty_scale, mAdapter.getCurrentName(), progress));
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
    public BaseDrawable getDrawable() {
        mBeautyDrawable = new HighBeautyDrawable();
        return mBeautyDrawable;
    }

    @Override
    @OnClick({R.id.iv_check_updown})
    public void onViewClick(View view) {
        super.onViewClick(view);
        if (R.id.iv_check_updown == view.getId()) {
            mIvCheckUpdown.toggle();
            setPannelHidden(!mIvCheckUpdown.isChecked());
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
            bottomMargin = mIvCheckUpdown.getMeasuredHeight() - mLlOperatorPanel.getMeasuredHeight();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLlOperatorPanel.getLayoutParams();
        layoutParams.bottomMargin = bottomMargin;
        mLlOperatorPanel.setLayoutParams(layoutParams);
    }
}
