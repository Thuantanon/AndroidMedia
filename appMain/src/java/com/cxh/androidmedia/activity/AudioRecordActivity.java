package com.cxh.androidmedia.activity;

import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.beans.AudioFileEntity;
import com.cxh.androidmedia.common.CommonPagerAdapter;
import com.cxh.androidmedia.manager.MediaPlayManager;
import com.cxh.androidmedia.manager.RecorderManager;
import com.cxh.androidmedia.utils.AsyncTask;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ToastUtil;
import com.cxh.androidmedia.utils.WAVUtil;
import com.cxh.mp3lame.LameEngine;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2018-09-23  15:49
 * Desc : 音频的录制，播放，读写wav
 * <p>
 * 如果要实现音乐播放的暂停、继续功能，可以分别存储多个pcm文件，等录音完毕后再合并到一个文件。
 */
public class AudioRecordActivity extends BaseActivity implements MultiTypeRvAdapter.AudioCallback {

    private static final int MSG_PLAY_WAV = 1;


    @BindView(R.id.start_record)
    Button mBtnStartRecord;
    @BindView(R.id.finish_record)
    Button mBtnFinishRecord;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tv_audio_state)
    TextView mTvAudioState;
    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    private RecorderManager mRecorderManager;
    private MultiTypeRvAdapter mPcmAdapter;
    private MultiTypeRvAdapter mWavAdapter;
    private MultiTypeRvAdapter mMp3Adapter;

    private SoundPool mSoundPool;
    private int mCurrentSoundId;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_audio_record;
    }

    @Override
    protected void init() {
        mRecorderManager = new RecorderManager(this);
        mRecorderManager.setTvAudioState(mTvAudioState);

        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .build();

        List<String> titles = new ArrayList<>();
        List<View> pages = new ArrayList<>();

        RecyclerView pcmView = new RecyclerView(mContext);
        pcmView.setLayoutManager(new LinearLayoutManager(mContext));
        mPcmAdapter = new MultiTypeRvAdapter(mContext);
        mPcmAdapter.setAudioCallback(this);
        pcmView.setAdapter(mPcmAdapter);

        RecyclerView wavView = new RecyclerView(mContext);
        wavView.setLayoutManager(new LinearLayoutManager(mContext));
        mWavAdapter = new MultiTypeRvAdapter(mContext);
        mWavAdapter.setAudioCallback(this);
        wavView.setAdapter(mWavAdapter);

        RecyclerView mp3View = new RecyclerView(mContext);
        mp3View.setLayoutManager(new LinearLayoutManager(mContext));
        mMp3Adapter = new MultiTypeRvAdapter(mContext);
        mMp3Adapter.setAudioCallback(this);
        mp3View.setAdapter(mMp3Adapter);

        titles.add("pcm文件列表");
        titles.add("wav文件列表");
        titles.add("mp3文件列表");
        pages.add(pcmView);
        pages.add(wavView);
        pages.add(mp3View);

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(pages, titles);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        loadFiles();
    }

    @Override
    @OnClick({R.id.start_record, R.id.finish_record})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.start_record:
                mRecorderManager.startRecord();
                break;
            case R.id.finish_record:
                mRecorderManager.finishRecord();
                loadFiles();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecorderManager.finishRecord();
        mRecorderManager.finishPlayPCM();
        if (null != mSoundPool) {
            if (mCurrentSoundId > 0) {
                mSoundPool.unload(mCurrentSoundId);
            }
            mSoundPool.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorderManager.release();
        mRecorderManager = null;
    }

    private void loadFiles() {
        List<Object> pcmList = new ArrayList<>();
        List<Object> wavList = new ArrayList<>();
        List<Object> mp3List = new ArrayList<>();

        File rootPath = new File(FileUtil.PATH_AUDIO_PCM);
        if (rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();
            if (null != files) {
                for (File f : files) {
                    pcmList.add(new AudioFileEntity(f.getAbsolutePath(), f.getName()));
                }
            }
        }

        rootPath = new File(FileUtil.PATH_AUDIO_WAV);
        if (rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();
            if (null != files) {
                for (File f : files) {
                    wavList.add(new AudioFileEntity(f.getAbsolutePath(), f.getName(), AudioFileEntity.AUDIO_TYPE_WAV));
                }
            }
        }

        rootPath = new File(FileUtil.PATH_AUDIO_MP3);
        if (rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();
            if (null != files) {
                for (File f : files) {
                    mp3List.add(new AudioFileEntity(f.getAbsolutePath(), f.getName(), AudioFileEntity.AUDIO_TYPE_MP3));
                }
            }
        }

        mPcmAdapter.setList(pcmList);
        mWavAdapter.setList(wavList);
        mMp3Adapter.setList(mp3List);
    }

    @Override
    public void playPCM(AudioFileEntity entity) {
        if (null != mRecorderManager) {
            mRecorderManager.playPCMFile(entity.getAudioAbsolutePath());
        }
    }

    @Override
    public void playWav(AudioFileEntity entity) {
        // 读取文件并播放，（播放方式很多种，这里省略播放步骤）
        WAVUtil.readWavFile(entity.getAudioAbsolutePath());
        if (null != mRecorderManager) {
            if (mCurrentSoundId > 0) {
                mSoundPool.unload(mCurrentSoundId);
            }
            mCurrentSoundId = mSoundPool.load(entity.getAudioAbsolutePath(), 1);
            // 上面的_load方法是异步的，因此立即播放会失败
            getHandler().removeMessages(MSG_PLAY_WAV);
            getHandler().sendEmptyMessageDelayed(MSG_PLAY_WAV, 1000);
        }
    }

    @Override
    public void playMp3(AudioFileEntity entity) {
        MediaPlayManager.playMp3(mContext, entity.getAudioAbsolutePath());
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case MSG_PLAY_WAV:
                if (mCurrentSoundId > 0) {
                    mSoundPool.play(mCurrentSoundId, 1, 1, 1, 0, 1);
                }
                break;
        }
    }

    @Override
    public void delete(AudioFileEntity entity) {
        File file = new File(entity.getAudioAbsolutePath());
        if (file.exists()) {
            boolean del = file.delete();
            if (del) {
                ToastUtil.show(mContext, "删除成功");
                loadFiles();
            } else {
                ToastUtil.show(mContext, "删除失败");
            }
        } else {
            ToastUtil.show(mContext, "文件不存在");
        }
    }

    @Override
    public void makeWav(AudioFileEntity entity) {

        String filePath = entity.getAudioAbsolutePath();
        final File pcmFile = new File(filePath);
        if (pcmFile.exists()) {
            final File wavFile = new File(FileUtil.PATH_AUDIO_WAV, pcmFile.getName().replace(".pcm", ".wav"));
            new AsyncTask<Boolean>() {

                @Override
                protected Boolean doWork() throws Exception {
                    byte[] header = WAVUtil.getWavHeader(pcmFile.length(), RecorderManager.SIMPLE_RATE_IN_HZ, 1, (byte) 16);
                    return WAVUtil.pcmToWav(pcmFile, wavFile, header);
                }

                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        loadFiles();
                        showToast("文件已保存到：" + wavFile.getAbsolutePath());
                    } else {
                        showToast("制作失败");
                    }
                }

                @Override
                protected void onFailed(Exception e) {
                    showToast("制作失败 ： " + e.toString());
                }
            }.execute();
        }
    }

    @Override
    public void makeMp3(AudioFileEntity entity) {
        String filePath = entity.getAudioAbsolutePath();
        final File pcmFile = new File(filePath);
        if (pcmFile.exists()) {
            final File mp3File = new File(FileUtil.PATH_AUDIO_MP3, pcmFile.getName().replace(".pcm", ".mp3"));
            new AsyncTask<Boolean>() {

                @Override
                public void onStart() {
                    super.onStart();
                    showProgressDialog("正在制作mp3，请稍等...");
                }

                @Override
                protected Boolean doWork() throws Exception {
                    LameEngine.native_Init(RecorderManager.SIMPLE_RATE_IN_HZ, LameEngine.CHANNEL_MONO, LameEngine.BIT_RATE_16,
                            LameEngine.QUALITY_HIGH);
                    boolean result = LameEngine.native_Encoder(pcmFile.getAbsolutePath(), mp3File.getAbsolutePath());
                    LameEngine.native_Release();

                    return result;
                }

                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        loadFiles();
                        showToast("文件已保存到：" + mp3File.getAbsolutePath());
                    } else {
                        showToast("制作mp3失败");
                    }
                }

                @Override
                protected void onFailed(Exception e) {
                    showToast("制作mp3失败 ： " + e.toString());
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    closeProgressDialog();
                }

            }.execute();
        }
    }

    private void showToast(String message) {
        ToastUtil.show(mContext, message);
    }

    // MediaRecorder录制音频基本使用
    private void userMediaRecorder() {

        // start
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 必须在设置编码格式之前设置
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(FileUtil.PATH_AUDIO + File.separator + "test.amr");
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // stop
        mediaRecorder.stop();
        mediaRecorder.release();
    }

    // MediaRecorder录制视频基本使用
    private void userVideoRecorder() {
        // start
        MediaRecorder mediaRecorder = new MediaRecorder();
        // 设置视频源
        // mediaRecorder.setInputSurface(Surface);
        // mediaRecorder.setCamera(Camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // mediaRecorder.setAudioChannels(1);
        // mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile("path");
        // 预览
        // mediaRecorder.setPreviewDisplay(surface);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // stop
        mediaRecorder.stop();
        mediaRecorder.release();
    }
}
