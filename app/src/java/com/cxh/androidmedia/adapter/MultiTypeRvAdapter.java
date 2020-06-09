package com.cxh.androidmedia.adapter;

import android.content.Context;

import com.cxh.androidmedia.adapter.item.AcitivityAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioPCMFileAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioWavFileAdapterItem;
import com.cxh.androidmedia.adapter.item.StringAdapterItem;
import com.cxh.androidmedia.beans.ActivityBean;
import com.cxh.androidmedia.beans.AudioFileEntity;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.common.IAdapterViewItem;

/**
 * Created by Cxh
 * Time : 2018-09-20  15:41
 * Desc :
 */
public class MultiTypeRvAdapter extends CommonBaseRvAdapter<Object> {

    private static final int V_TYPE_STRING = 0;
    private static final int V_TYPE_ACTIVITY = 1;
    private static final int V_TYPE_AUDIO_PCM = 2;
    private static final int V_TYPE_AUDIO_WAV = 3;

    private AudioCallback mAudioCallback;

    public void setAudioCallback(AudioCallback audioCallback) {
        mAudioCallback = audioCallback;
    }

    public MultiTypeRvAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = getList().get(position);
        if (object instanceof String) {
            return V_TYPE_STRING;
        } else if (object instanceof ActivityBean) {
            return V_TYPE_ACTIVITY;
        } else if (object instanceof AudioFileEntity) {
            AudioFileEntity entity = (AudioFileEntity) object;
            if(entity.getAudioType() == AudioFileEntity.AUDIO_TYPE_PCM) {
                return V_TYPE_AUDIO_PCM;
            }else if(entity.getAudioType() == AudioFileEntity.AUDIO_TYPE_WAV){
                return V_TYPE_AUDIO_WAV;
            }
        }
        return 0;
    }

    @Override
    protected IAdapterViewItem<Object> getAdaperItem(int viewType) {
        if (viewType == V_TYPE_STRING) {
            return new StringAdapterItem();
        } else if (viewType == V_TYPE_ACTIVITY) {
            return new AcitivityAdapterItem(mContext);
        } else if (viewType == V_TYPE_AUDIO_PCM) {
            return new AudioPCMFileAdapterItem(mAudioCallback);
        }else if(viewType == V_TYPE_AUDIO_WAV){
            return new AudioWavFileAdapterItem(mAudioCallback);
        }
        return new StringAdapterItem();
    }


    public interface AudioCallback {
        void playPCM(AudioFileEntity entity);
        void playWav(AudioFileEntity entity);
        void delete(AudioFileEntity entity);
        void makeWav(AudioFileEntity entity);
    }

}
