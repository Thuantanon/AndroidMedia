package com.cxh.androidmedia.adapter.item;

import android.view.View;
import android.widget.Button;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.beans.AudioFileEntity;
import com.cxh.androidmedia.beans.MediaFileWrapper;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.IAdapterViewItem;
import com.cxh.androidmedia.utils.FileUtil;

/**
 * Created by Cxh
 * Time : 2018-09-26  11:18
 * Desc :
 */
public class AudioPlayAdapterItem implements IAdapterViewItem<Object> {

    private Button mBtnPlay;
    private Button mBtnDelete;
    private Button mBtnMkwav;
    private Button mBtnMp3;

    private MultiTypeRvAdapter.AudioPlayCallback mCallback;

    public AudioPlayAdapterItem(MultiTypeRvAdapter.AudioPlayCallback callback) {
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
        mBtnMp3 = holder.findViewById(R.id.btn_mkmp3);
    }

    @Override
    public void onBindData(CommonBaseRVHolder<Object> holder, Object data, int position) {
        final MediaFileWrapper wrapper = (MediaFileWrapper) data;

        holder.setText(R.id.file_name, wrapper.getFile().getAbsolutePath());
        holder.setText(R.id.file_size, FileUtil.getFileSize(wrapper.getFile().getAbsoluteFile()));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mBtnPlay && null != mCallback) {
                    mCallback.play(wrapper.getFile());
                }

                if (v == mBtnDelete && null != mCallback) {
                    mCallback.delete(wrapper.getFile());
                }
            }
        };

        mBtnPlay.setOnClickListener(onClickListener);
        mBtnDelete.setOnClickListener(onClickListener);
        mBtnMkwav.setVisibility(View.GONE);
        mBtnMp3.setVisibility(View.GONE);
    }

}
