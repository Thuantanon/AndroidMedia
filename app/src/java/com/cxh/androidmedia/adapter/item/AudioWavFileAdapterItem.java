package com.cxh.androidmedia.adapter.item;

import android.view.View;
import android.widget.Button;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.beans.AudioFileEntity;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.IAdapterViewItem;
import com.cxh.androidmedia.utils.FileUtil;

/**
 * Created by Cxh
 * Time : 2018-09-26  11:18
 * Desc :
 */
public class AudioWavFileAdapterItem implements IAdapterViewItem<Object> {

    private Button mBtnPlay;
    private Button mBtnDelete;
    private Button mBtnMkwav;

    private MultiTypeRvAdapter.AudioCallback mCallback;

    public AudioWavFileAdapterItem(MultiTypeRvAdapter.AudioCallback callback) {
        mCallback = callback;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_audio_file;
    }

    @Override
    public void onBindView(CommonBaseRVHolder<Object> holder) {
        mBtnPlay = holder.findViewById(R.id.btn_play);
        mBtnDelete = holder.findViewById(R.id.btn_delete);
        mBtnMkwav = holder.findViewById(R.id.btn_mkwav);
    }

    @Override
    public void onBindData(CommonBaseRVHolder<Object> holder, Object data, int position) {
        final AudioFileEntity entity = (AudioFileEntity) data;

        holder.setText(R.id.file_name, entity.getAudioAbsolutePath());
        holder.setText(R.id.file_size, FileUtil.getFileSize(entity.getAudioAbsolutePath()));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mBtnPlay && null != mCallback) {
                    mCallback.playWav(entity);
                }

                if (v == mBtnDelete && null != mCallback) {
                    mCallback.delete(entity);
                }
            }
        };

        mBtnPlay.setOnClickListener(onClickListener);
        mBtnDelete.setOnClickListener(onClickListener);
        mBtnMkwav.setVisibility(View.GONE);
    }

}
