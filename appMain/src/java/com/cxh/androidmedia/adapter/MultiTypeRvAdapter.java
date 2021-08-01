package com.cxh.androidmedia.adapter;

import android.content.Context;

import com.cxh.androidmedia.adapter.item.AcitivityAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioMp3FileAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioPCMFileAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioPlayAdapterItem;
import com.cxh.androidmedia.adapter.item.AudioWavFileAdapterItem;
import com.cxh.androidmedia.adapter.item.StringAdapterItem;
import com.cxh.androidmedia.adapter.item.VideoFileItem;
import com.cxh.androidmedia.beans.ActivityBean;
import com.cxh.androidmedia.beans.AudioFileEntity;
import com.cxh.androidmedia.beans.MediaFileWrapper;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.common.IAdapterViewItem;

import java.io.File;

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
    private static final int V_TYPE_AUDIO_MP3 = 4;
    private static final int V_TYPE_VIDEO = 5;
    private static final int V_TYPE_AUDIO_NEW = 6;

    private AudioCallback mAudioCallback;
    private VideoCallback mVideoCallback;
    private AudioPlayCallback mAudioPlayCallback;
    private boolean mVideoDecode;

    public void setAudioCallback(AudioCallback audioCallback) {
        mAudioCallback = audioCallback;
    }

    public void setVideoCallback(VideoCallback videoCallback) {
        mVideoCallback = videoCallback;
    }

    public void setAudioPlayCallback(AudioPlayCallback audioPlayCallback) {
        mAudioPlayCallback = audioPlayCallback;
    }

    public void setVideoDecode(boolean videoDecode) {
        mVideoDecode = videoDecode;
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
            if (entity.getAudioType() == AudioFileEntity.AUDIO_TYPE_PCM) {
                return V_TYPE_AUDIO_PCM;
            } else if (entity.getAudioType() == AudioFileEntity.AUDIO_TYPE_WAV) {
                return V_TYPE_AUDIO_WAV;
            } else if (entity.getAudioType() == AudioFileEntity.AUDIO_TYPE_MP3) {
                return V_TYPE_AUDIO_MP3;
            }
        } else if (object instanceof MediaFileWrapper) {
            MediaFileWrapper wrapper = (MediaFileWrapper) object;
            if (wrapper.getType() == MediaFileWrapper.TYPE_AUDIO) {
                return V_TYPE_AUDIO_NEW;
            } else {
                return V_TYPE_VIDEO;
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
        } else if (viewType == V_TYPE_AUDIO_WAV) {
            return new AudioWavFileAdapterItem(mAudioCallback);
        } else if (viewType == V_TYPE_AUDIO_MP3) {
            return new AudioMp3FileAdapterItem(mAudioCallback);
        } else if (viewType == V_TYPE_VIDEO) {
            return new VideoFileItem(mVideoCallback, mVideoDecode);
        } else if (viewType == V_TYPE_AUDIO_NEW) {
            return new AudioPlayAdapterItem(mAudioPlayCallback);
        }

        return new StringAdapterItem();
    }


    public interface AudioCallback {
        void playPCM(AudioFileEntity entity);

        void playWav(AudioFileEntity entity);

        void delete(AudioFileEntity entity);

        void makeWav(AudioFileEntity entity);

        void makeMp3(AudioFileEntity entity);

        void playMp3(AudioFileEntity entity);
    }

    public interface VideoCallback {
        void delete(File f);

        void play(File f);

        void selected(File file);
    }

    public interface AudioPlayCallback {
        void play(File file);

        void delete(File file);
    }
}
