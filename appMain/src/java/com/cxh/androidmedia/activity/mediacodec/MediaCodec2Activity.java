package com.cxh.androidmedia.activity.mediacodec;

import android.content.Intent;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.beans.MediaFileWrapper;
import com.cxh.androidmedia.manager.MediaPlayManager;
import com.cxh.androidmedia.manager.VideoParseManager;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ToastUtil;
import com.cxh.mp3lame.LameEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-05-28  00:17
 * Desc : MediaCodec实现音频AAC、视频H264的编解码
 */
public class MediaCodec2Activity extends BaseActivity {

    private static final int REQ_CODE_PLAY_AUDIO = 0x1;
    private static final int REQ_CODE_PLAY_VIDEO = 0x2;
    private static final int REQ_CODE_GET_MP3 = 0x3;

    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;

    private MediaPlayManager mMediaPlayManager;
    private Handler mDecodeHandler;

    private Uri mVideoUri;
    private boolean mSurfaceCreated;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_mediacodec_2;
    }

    @Override
    protected void init() {
        mMediaPlayManager = new MediaPlayManager();

        HandlerThread handlerThread = new HandlerThread("DecodeThread");
        handlerThread.start();
        mDecodeHandler = new Handler(handlerThread.getLooper());

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceCreated = true;
                if (null != mVideoUri) {
                    playVideoBySurface(mVideoUri, holder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceCreated = false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoUri = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mMediaPlayManager) {
            mMediaPlayManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mMediaPlayManager) {
            mMediaPlayManager.destroy();
            mMediaPlayManager = null;
        }

        if (null != mDecodeHandler) {
            mDecodeHandler.getLooper().quitSafely();
            mDecodeHandler = null;
        }
    }

    @Override
    @OnClick({R.id.btn_get_mp3, R.id.btn_play, R.id.btn_play_audio})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.btn_play_audio: {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_CODE_PLAY_AUDIO);
            }
            break;
            case R.id.btn_get_mp3: {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_CODE_GET_MP3);
            }
            break;
            case R.id.btn_play: {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_CODE_PLAY_VIDEO);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || RESULT_OK != resultCode) {
            return;
        }

        Uri dataUri = data.getData();
        boolean convertMp3 = false;
        switch (requestCode) {
            case REQ_CODE_GET_MP3:
                convertMp3 = true;
            case REQ_CODE_PLAY_AUDIO: {
                getAudioFromVideo(dataUri, convertMp3);
            }
            break;
            case REQ_CODE_PLAY_VIDEO: {
                mVideoUri = dataUri;
            }
            break;
        }
    }

    private void getAudioFromVideo(Uri uri, boolean convertMp3) {
        showProgressDialog("正在解析...");

        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                String fileName = FileUtil.getTimeFormat();
                String pcmFilePath = FileUtil.PATH_AUDIO_PCM + File.separator + fileName + ".pcm";
                String mp3FilePath = FileUtil.PATH_AUDIO_MP3 + File.separator + fileName + ".mp3";
                MediaFormat mediaFormat = VideoParseManager.parseAudioFromVideoUri(mContext, uri, pcmFilePath);
                // 生成mp3
                if (null != mediaFormat) {
                    int sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    int bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);

                    CCLog.i("getAudioFromVideo, encode, sampleRate: " + sampleRate + " , channelCount: " + channelCount + " , bitRate: "
                            + bitRate + " , convertMp3: " + convertMp3);

                    if (convertMp3) {
                        LameEngine.native_Init(sampleRate, channelCount, LameEngine.BIT_RATE_16, LameEngine.QUALITY_HIGH);
                        boolean result = LameEngine.native_Encoder(pcmFilePath, mp3FilePath);
                        LameEngine.native_Release();

                        CCLog.i("getAudioFromVideo, result: " + result);
                        if (result) {
                            toastMessage("Mp3文件已保存至: " + mp3FilePath);
                        }
                    }

                    mMediaPlayManager.playPcm(pcmFilePath, sampleRate, channelCount);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                    }
                });
            }
        });
    }

    private void playVideoBySurface(Uri videoUri, Surface previewSurface) {
        VideoParseManager.playVideoFromFile(
                mContext,
                videoUri,
                previewSurface,
                mDecodeHandler,
                new VideoParseManager.DecodingProvider() {
                    @Override
                    public boolean needPrevent() {
                        return !mSurfaceCreated;
                    }
                }
        );
    }

    private void toastMessage(String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(mContext, message);
            }
        };

        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            runOnUiThread(runnable);
        }
    }
}
