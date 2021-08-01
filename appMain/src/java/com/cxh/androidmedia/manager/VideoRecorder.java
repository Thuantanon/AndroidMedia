package com.cxh.androidmedia.manager;

import android.media.MediaCodec;
import android.view.Surface;

import com.cxh.androidmedia.utils.CCLog;

/**
 * Created by Cxh
 * Time : 2021/8/1  19:59
 * Desc :
 */
public class VideoRecorder {

    private Surface mRecordSurface;


    public void start() {

        MediaCodec.createPersistentInputSurface();
    }

    public void stop() {

    }

    private void printFrameInfo(MediaCodec.BufferInfo bufferInfo) {
        switch (bufferInfo.flags) {
            case MediaCodec.BUFFER_FLAG_CODEC_CONFIG:
                CCLog.i("BUFFER_FLAG_CODEC_CONFIG");
                break;
        }
    }
}
