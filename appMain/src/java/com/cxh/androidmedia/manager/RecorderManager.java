package com.cxh.androidmedia.manager;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ThreadPoolManager;
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Cxh
 * Time : 2018-09-23  17:41
 * Desc :
 */
public class RecorderManager {

    /**
     * 录音
     * 音频参数概念：
     *
     * 采样率：
     * 对声音源进行采集，一秒钟内采样的次数
     *
     * 采样位数/位宽：
     * 数字信号是用0和1来表示的。位宽值越大，表示精度越高。
     *
     * 声道：
     * 通常语音只用一个声道。而对于音乐来说，既可以是单声道（mono），也可以是双声道（即左声道右声道，叫立体声stereo），还可以是多声道，叫环绕立体声。
     *
     * 码率：
     * 码率 = 采样频率 * 采样位数 * 声道个数。即每秒要录制的资源大小,理论上码率和质量成正比。
     *
     * 常用音频格式：
     * WAV 格式：音质高 无损格式 体积较大。
     * AAC（Advanced Audio Coding） 格式：相对于 mp3，AAC 格式的音质更佳，文件更小，有损压缩。
     * 一般苹果或者Android SDK4.1.2（API 16）及以上版本支持播放，性价比高，常用在直播、语音通话等音频传输场合。
     *
     *
     */
    // 音频采集的输入源
    public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    // 采样率，44100是科学家根据奈葵斯特采样定理得出的一个人能接受最佳的采样频率值
    public static final int SIMPLE_RATE_IN_HZ = 44100;
    // 声道数，常用值有 CHANNEL_CONFIGURATION_MONO(单声道) 和 CHANNEL_CONFIGURATION_STEREO(双声道)
    public static final int CHANNEL_TYPE = AudioFormat.CHANNEL_IN_MONO;
    // 采样格式，ENCODING_PCM_16BIT可以保证兼容大部分Andorid手机
    public static final byte AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区大小，最好使用默认方式
    public static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SIMPLE_RATE_IN_HZ,
            CHANNEL_TYPE, AUDIO_FORMAT);
    // MODE_STREAM 和 MODE_STATIC， 前者适用于长时间的音乐播放，内存占用较大，后者适用于急促的游戏音乐，内存占用较小
    private static final int AUDIO_MODE = AudioTrack.MODE_STREAM;
    // 流类型，比如系统铃声、媒体声（音乐）、电话、警告音等
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;


    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    private BaseActivity mActivity;
    private TextView mTvAudioState;

    private PCMDataCallback mCallback;
    private boolean mIsRecording;
    private boolean mIsPlaying;

    public RecorderManager(BaseActivity activity) {
        mActivity = activity;

        mAudioRecord = new AudioRecord(AUDIO_SOURCE, SIMPLE_RATE_IN_HZ, CHANNEL_TYPE,
                AUDIO_FORMAT, BUFFER_SIZE);
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            ToastUtil.show(mActivity, "麦克风打开失败");
        }

        if (Build.VERSION.SDK_INT >= 23) {
            mAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setLegacyStreamType(STREAM_TYPE)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    )
                    .setAudioFormat(
                            new AudioFormat.Builder()
                                    .setEncoding(AUDIO_FORMAT)
                                    .setSampleRate(SIMPLE_RATE_IN_HZ)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build()
                    )
                    .setTransferMode(AUDIO_MODE)
                    .setBufferSizeInBytes(BUFFER_SIZE)
                    .build();
        } else {
            mAudioTrack = new AudioTrack(STREAM_TYPE, SIMPLE_RATE_IN_HZ, CHANNEL_TYPE, AUDIO_FORMAT, BUFFER_SIZE, AUDIO_MODE);
        }
    }

    /**
     *   录制的暂停，恢复，实现方案：
     *   生成多个pcm文件，最后合并成一个文件
     */
    public synchronized void startRecord() {
        if (mIsRecording) {
            ToastUtil.show(mActivity, "正在录音...");
            return;
        }

        mIsRecording = true;
        mAudioRecord.startRecording();
        mTvAudioState.setText("正在录音...");
        Runnable recordTask = new Runnable() {
            @Override
            public void run() {
                File file = new File(FileUtil.getRandomPCMFile());
                FileOutputStream fileOutputStream = null;
                BufferedOutputStream bufferedOutputStream = null;
                DataOutputStream dataOutputStream = null;

                try {
                    boolean createNewFile = file.createNewFile();
                    if (createNewFile) {
                        fileOutputStream = new FileOutputStream(file);
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        dataOutputStream = new DataOutputStream(bufferedOutputStream);

                        int read;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while (mIsRecording) {
                            read = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                            if (read > 0) {
                                dataOutputStream.write(buffer, 0, read);
                                if (null != mCallback) {
                                    mCallback.onPCMDataAvailable(buffer, read);
                                }
                            }
                        }
                    }
                    finishRecord("录制结束");
                } catch (Exception e) {
                    CCLog.i(e.toString());
                    finishRecord("音频录制异常");
                } finally {
                    FileUtil.tryClose(fileOutputStream);
                    FileUtil.tryClose(bufferedOutputStream);
                    FileUtil.tryClose(dataOutputStream);
                }
            }
        };

        ThreadPoolManager.getInstance().execute(recordTask);
    }


    public synchronized void playPCMFile(String pcmFile) {
        if (TextUtils.isEmpty(pcmFile)) {
            return;
        }
        final File file = new File(pcmFile);
        if (! file.exists() || !file.isFile()) {
            return;
        }

        // 结束上次播放
        finishPlayPCM();

        mIsPlaying = true;
        ToastUtil.show(mActivity, "开始播放...");
        Runnable playTask = new Runnable() {
            @Override
            public void run() {
                FileInputStream fileInputStream = null;
                DataInputStream dataInputStream = null;

                try {
                    fileInputStream = new FileInputStream(file);
                    dataInputStream = new DataInputStream(fileInputStream);

                    mAudioTrack.play();

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while (mIsPlaying && (len = dataInputStream.read(buffer)) > 0) {
                        mAudioTrack.write(buffer, 0, len);
                    }

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(mActivity, "播放完毕...");
                        }
                    });

                } catch (Exception e) {
                    CCLog.i(e.toString());
                } finally {
                    FileUtil.tryClose(dataInputStream);
                    FileUtil.tryClose(fileInputStream);
                }
            }
        };

        ThreadPoolManager.getInstance().execute(playTask);
    }


    public synchronized void playWavFile(String wavFile){

        if (TextUtils.isEmpty(wavFile)) {
            return;
        }
        final File file = new File(wavFile);
        if (! file.exists() || !file.isFile()) {
            return;
        }

        // 结束上次播放


    }

    public synchronized void finishPlayPCM() {
        if (!mIsPlaying) {
            return;
        }

        try {
            mIsPlaying = false;
            mAudioTrack.stop();

        } catch (Exception e) {
            CCLog.i(e.toString());
        }
    }

    public void finishRecord() {
        this.finishRecord("");
    }

    public synchronized void finishRecord(String message) {
        if (!mIsRecording) {
            return;
        }

        try {
            mAudioRecord.stop();
            mIsRecording = false;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvAudioState.setText(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void release() {
        finishRecord();
        if (null != mAudioRecord) {
            mIsRecording = false;
            mAudioRecord.release();
            mAudioRecord = null;
        }

        finishPlayPCM();
        if (null != mAudioTrack) {
            mIsPlaying = false;
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public void setTvAudioState(TextView tvAudioState) {
        mTvAudioState = tvAudioState;
    }

    public void setCallback(PCMDataCallback callback) {
        mCallback = callback;
    }

    public interface PCMDataCallback {
        void onPCMDataAvailable(byte[] data, int read);
    }
}
