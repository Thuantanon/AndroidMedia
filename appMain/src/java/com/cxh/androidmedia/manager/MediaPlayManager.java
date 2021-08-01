package com.cxh.androidmedia.manager;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.core.content.FileProvider;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

/**
 * Created by Cxh
 * Time : 2021/7/18  17:41
 * Desc :
 */
public class MediaPlayManager {

    private AudioTrack mAudioTrack;
    private Handler mPlayHandler;

    private int mBufferSize;
    private boolean mIsPlaying;

    public MediaPlayManager() {
        HandlerThread handlerThread = new HandlerThread("PlayThread");
        handlerThread.start();
        mPlayHandler = new Handler(handlerThread.getLooper());
    }

    public void stop() {
        mIsPlaying = false;
        mPlayHandler.removeCallbacksAndMessages(null);
    }

    public void destroy() {
        if (null != mPlayHandler) {
            mPlayHandler.getLooper().quitSafely();
            mPlayHandler = null;
        }
    }

    public void playPcm(String pcmFile, int sampleRate, int channelCount) {
        if (mIsPlaying) {
            return;
        }

        mPlayHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsPlaying = true;
                int channelType = channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

                initAudioTrack(sampleRate, channelType, AudioFormat.ENCODING_PCM_16BIT);
                mAudioTrack.play();

                DataInputStream dataInputStream = null;
                try {
                    dataInputStream = new DataInputStream(new FileInputStream(pcmFile));
                    byte[] buffer = new byte[mBufferSize];
                    int len;
                    while (mIsPlaying && ((len = dataInputStream.read(buffer)) > 0)) {
                        mAudioTrack.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    CCLog.e("playPcm, e: " + e.toString());
                } finally {
                    mIsPlaying = false;
                    releaseAudioTrack();
                    FileUtil.tryClose(dataInputStream);
                }
            }
        });
    }

    public static void playMp3(Context context, String mp3FilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(mp3FilePath));
        intent.setDataAndType(uri, "audio/*");
        context.startActivity(intent);
    }

    private void initAudioTrack(int sampleRate, int channel, int format) {
        releaseAudioTrack();

        mBufferSize = AudioTrack.getMinBufferSize(sampleRate, channel, format);
        mAudioTrack = new AudioTrack.Builder()
                .setBufferSizeInBytes(mBufferSize)
                .setAudioFormat(
                        new AudioFormat.Builder()
                                .setSampleRate(sampleRate)
                                .setChannelMask(channel)
                                .setEncoding(format)
                                .build())
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build())
                .build();
    }

    private void releaseAudioTrack() {
        if (null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}
