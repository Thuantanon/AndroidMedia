package com.cxh.androidmedia.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.FilterListRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.ClassicDrawRender;
import com.cxh.androidmedia.render.IDrawableProvider;
import com.cxh.androidmedia.render.bean.FilterBean;
import com.cxh.androidmedia.render.beauty.DeformCanvasDrawable;
import com.cxh.androidmedia.utils.DimenUtil;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ToastUtil;
import com.cxh.androidmedia.view.ShotcutFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : OpenGL ES实现简单图片处理
 */
public class GLRender2Activity extends BaseActivity implements IDrawableProvider, SeekBar.OnSeekBarChangeListener {

    public static final int MSG_SHOW_IMAGE_DIALOG = 1;

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
    @BindView(R.id.ctv_open_watermark)
    CheckedTextView mCtvOpenWatermark;
    @BindView(R.id.ctv_open_shotcut)
    CheckedTextView mCtvShotcut;
    @BindView(R.id.fl_shotcut)
    ShotcutFrameLayout mFlShotcut;
    @BindView(R.id.rv_filter_list)
    RecyclerView mRvFilterList;


    private ClassicDrawRender mDrawRender;
    private DeformCanvasDrawable mDeformDrawable;
    private FilterListRvAdapter mAdapter;

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

        mAdapter = new FilterListRvAdapter(mContext);
        mRvFilterList.setLayoutManager(new GridLayoutManager(mContext, 5));
        mRvFilterList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonBaseRvAdapter.OnItemClickListener<FilterBean>() {
            @Override
            public void onItemClick(CommonBaseRVHolder holder, FilterBean data, int position) {
                mAdapter.setCurrentFilter(data.getFilterId());
                mDeformDrawable.setCurrentFilter(data.getFilterId());
                mSurfaceView.requestRender();
            }
        });

        List<FilterBean> mFilters = new ArrayList<>();
        mFilters.add(new FilterBean(0, "默认"));
        mFilters.add(new FilterBean(1, "黑白"));
        mFilters.add(new FilterBean(2, "暖男"));
        mFilters.add(new FilterBean(3, "高冷"));
        mFilters.add(new FilterBean(4, "反转"));
        mFilters.add(new FilterBean(5, "反转2"));
        mFilters.add(new FilterBean(6, "马赛克"));
        mFilters.add(new FilterBean(7, "马赛克2"));
        mAdapter.setList(mFilters);
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
        mDeformDrawable = new DeformCanvasDrawable(getHandler());
        return mDeformDrawable;
    }


    @Override
    @OnClick({R.id.iv_check_updown, R.id.ctv_open_watermark, R.id.ctv_open_shotcut, R.id.btn_save})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.iv_check_updown: {
                mIvCheckUpdown.toggle();
                setPannelHidden(!mIvCheckUpdown.isChecked());
            }
            break;
            case R.id.ctv_open_watermark: {
                mCtvOpenWatermark.toggle();
                mDeformDrawable.setDrawWater(mCtvOpenWatermark.isChecked());
                mSurfaceView.requestRender();
            }
            break;
            case R.id.ctv_open_shotcut: {
                mCtvShotcut.toggle();
                mFlShotcut.setOpenShotcut(mCtvShotcut.isChecked());
            }
            break;
            case R.id.btn_save: {
                mDeformDrawable.setSaveImage(true);
                if (mCtvShotcut.isChecked()) {
                    if (mFlShotcut.isShotcutEnable()) {
                        mDeformDrawable.setShotRect(mFlShotcut.getShotcutRectF());
                        mSurfaceView.requestRender();

                    } else {
                        ToastUtil.show(mContext, "触摸显示裁剪范围");
                    }
                } else {
                    mDeformDrawable.setShotRect(null);
                    mSurfaceView.requestRender();
                }

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
            bottomMargin = mIvCheckUpdown.getMeasuredHeight() - mLlOperatorPanel.getMeasuredHeight();
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
        } else if (seekBar == mSBRotate) {

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

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (MSG_SHOW_IMAGE_DIALOG == message.what) {
            Bitmap bitmap = (Bitmap) message.obj;
            int width = message.arg1;
            int height = message.arg2;
            showScreenshotImage(bitmap, width, height);
        }
    }


    private void showScreenshotImage(final Bitmap bitmap, final int width, final int height) {
        final View rootView = getLayoutInflater().inflate(R.layout.dialog_show_image_dilaog, null);
        ImageView ivPicture = rootView.findViewById(R.id.iv_image);
        int widthPixel = DimenUtil.dp2Px(mContext, 250);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivPicture.getLayoutParams();
        layoutParams.width = widthPixel;
        layoutParams.height = (int) (widthPixel * height * 1f / width);
        ivPicture.setLayoutParams(layoutParams);
        ivPicture.setImageBitmap(bitmap);

        Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("保存截图")
                .setView(rootView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb = new StringBuilder(FileUtil.PATH_IMAGE);
                        sb.append(File.separator);
                        sb.append("gles_screenshot_");
                        sb.append(System.currentTimeMillis());
                        sb.append(".jpg");

                        FileUtil.saveBitmapToStorage(bitmap, sb.toString());
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }
}
