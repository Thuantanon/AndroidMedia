package com.cxh.androidmedia.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.BeautyListRvAdapter;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.render_old.bean.BeautyBean;
import com.cxh.androidmedia.render_old.bean.BeautyParams;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cxh
 * Time : 2020-09-17  21:40
 * Desc :
 */
public class BeautySetDialog extends Dialog {

    @BindView(R.id.iv_title)
    TextView mTvTitle;
    @BindView(R.id.seekbar_beauty)
    SeekBar mSeekBarBeauty;
    @BindView(R.id.tv_beauty_progress)
    TextView mTvBeautyProgress;
    @BindView(R.id.rv_beauty_list)
    RecyclerView mRvBeautyList;

    private BeautyListRvAdapter mAdapter;
    private OnBeautyChangedCallback mBeautyCallback;

    public BeautySetDialog(@NonNull Context context) {
        super(context, R.style.CommonBottomSheet);
        setContentView(R.layout.dilaog_beauty_set_layout);
        ButterKnife.bind(this);

        Window window = getWindow();
        if (null != window) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        mAdapter = new BeautyListRvAdapter(context);
        mRvBeautyList.setLayoutManager(new GridLayoutManager(context, 5));
        mRvBeautyList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonBaseRvAdapter.OnItemClickListener<BeautyBean>() {
            @Override
            public void onItemClick(CommonBaseRVHolder holder, BeautyBean data, int position) {
                mAdapter.setCurrentBeauty(position);
                if (BeautyParams.BEAUTY_TYPE_RESET.equals(data.getBeautyType())) {
                    BeautyParams.reset();
                    mSeekBarBeauty.setEnabled(false);
                    mTvTitle.setText("");
                } else {

                    int progress = (int) (data.getBeautyScale() * 100);
                    mTvBeautyProgress.setText(progress + "%");
                    mTvTitle.setText(data.getBeautyName());
                    mTvBeautyProgress.setVisibility(View.VISIBLE);
                    mSeekBarBeauty.setProgress(progress);
                    mSeekBarBeauty.setEnabled(true);
                }

                if (null != mBeautyCallback) {
                    mBeautyCallback.onBeautyChanged(data);
                }
            }
        });
        mAdapter.setList(BeautyParams.getBeautyBeans());

        mSeekBarBeauty.setEnabled(false);
        mSeekBarBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = progress / 100f;
                BeautyParams.setParams(mAdapter.getCurrentKey(), scale);
                mTvBeautyProgress.setText(progress + "%");
                mTvTitle.setText(mAdapter.getCurrentName());
                if (null != mBeautyCallback) {
                    mBeautyCallback.onBeautyChanged(mAdapter.getList().get(mAdapter.getCurrentBeauty()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setBeautyCallback(OnBeautyChangedCallback beautyCallback) {
        mBeautyCallback = beautyCallback;
    }

    public interface OnBeautyChangedCallback {
        void onBeautyChanged(BeautyBean data);
    }
}
