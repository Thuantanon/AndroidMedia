package com.cxh.androidmedia.adapter.item;

import android.view.View;
import android.widget.Button;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.beans.MediaFileWrapper;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.IAdapterViewItem;
import com.cxh.androidmedia.utils.FileUtil;

import java.io.File;

/**
 * Created by Cxh
 * Time : 2021/7/18  22:33
 * Desc :
 */
public class VideoFileItem implements IAdapterViewItem<Object> {

    private Button mBtnPlay;
    private Button mBtnDelete;
    private Button mBtnMuxer;

    private MultiTypeRvAdapter.VideoCallback mCallback;
    private boolean mVideoDecode;

    public VideoFileItem(MultiTypeRvAdapter.VideoCallback videoCallback, boolean videoDecode) {
        mCallback = videoCallback;
        mVideoDecode = videoDecode;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_video_file;
    }

    @Override
    public void onBindView(CommonBaseRVHolder<Object> holder) {
        mBtnPlay = holder.findViewById(R.id.btn_play);
        mBtnDelete = holder.findViewById(R.id.btn_delete);
        mBtnMuxer = holder.findViewById(R.id.btn_muxer);
    }

    @Override
    public void onBindData(CommonBaseRVHolder<Object> holder, Object data, int position) {
        MediaFileWrapper wrapper = (MediaFileWrapper) data;

        holder.setText(R.id.file_name, wrapper.getFile().getAbsolutePath());
        holder.setText(R.id.file_size, FileUtil.getFileSize(wrapper.getFile()));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mCallback) {
                    return;
                }

                if (v == mBtnPlay) {
                    mCallback.play(wrapper.getFile());
                }

                if (v == mBtnDelete) {
                    mCallback.delete(wrapper.getFile());
                }

                if (v == mBtnMuxer) {
                    mCallback.selected(wrapper.getFile());
                }
            }
        };

        mBtnPlay.setOnClickListener(onClickListener);
        mBtnDelete.setOnClickListener(onClickListener);
        mBtnMuxer.setOnClickListener(onClickListener);

        if (wrapper.getFile().getAbsolutePath().endsWith("mp4")) {
            mBtnMuxer.setVisibility(View.GONE);
            mBtnPlay.setVisibility(View.VISIBLE);
        } else {
            mBtnMuxer.setVisibility(mVideoDecode ? View.VISIBLE : View.GONE);
            mBtnPlay.setVisibility(View.GONE);
        }
    }
}
