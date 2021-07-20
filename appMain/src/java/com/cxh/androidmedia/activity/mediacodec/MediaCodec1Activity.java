package com.cxh.androidmedia.activity.mediacodec;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-05-28  00:17
 * Desc : MediaCodec对Mp4文件简单解析封装
 */
public class MediaCodec1Activity extends BaseActivity implements MultiTypeRvAdapter.VideoCallback {

    private static final int REQ_CODE_VIDEO = 0x1;

    @BindView(R.id.video_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private MultiTypeRvAdapter mAdapter;
    private Handler mIOHandler;
    private List<Object> mFileList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_mediacodec_1;
    }

    @Override
    protected void init() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MultiTypeRvAdapter(mContext);
        mAdapter.setVideoCallback(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVideoFiles();
            }
        });

        HandlerThread handlerThread = new HandlerThread("IO transaction");
        handlerThread.start();
        mIOHandler = new Handler(handlerThread.getLooper());

        loadVideoFiles();
    }

    @Override
    @OnClick({R.id.btn_start})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.btn_start: {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_CODE_VIDEO);
            }

            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mIOHandler) {
            mIOHandler.getLooper().quitSafely();
            mIOHandler = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_VIDEO: {
                    mIOHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handleVideoResult(data);
                        }
                    });
                }
                break;
            }
        }
    }


    @Override
    public void delete(File f) {
        if (f.exists() && f.isFile()) {
            boolean result = f.delete();
            if (result) {
                ToastUtil.show(mContext, "已删除");
            }
        }

        loadVideoFiles();
    }

    @Override
    public void play(File f) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(mContext, getPackageName() + ".provider", f);
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);
    }

    @Override
    public void selected(File file) {
        mIOHandler.post(new Runnable() {
            @Override
            public void run() {
                File h264File = null;
                File aacFile = null;
                if (file.getAbsolutePath().endsWith("h264")) {
                    h264File = file;
                    aacFile = new File(file.getParentFile(), file.getName().replace("h264", "aac"));
                } else if (file.getAbsolutePath().endsWith("aac")) {
                    aacFile = file;
                    h264File = new File(file.getParentFile(), file.getName().replace("aac", "h264"));
                }

                if (null != h264File && h264File.exists() && null != aacFile && aacFile.exists()) {
                    parseH264File(h264File);
                } else {
                    CCLog.i("h264File: " + h264File + " , aacFile: " + aacFile);
                }
            }
        });
    }

    private void loadVideoFiles() {
        mFileList.clear();
        File rooPath = new File(FileUtil.PATH_VIDEO_CACHE);
        if (rooPath.exists() && rooPath.isDirectory()) {
            File[] files = rooPath.listFiles();
            if (null != files && files.length > 0) {
                mFileList.addAll(Arrays.asList(files));
            }
        }

        mAdapter.setList(mFileList);
        mRefreshLayout.setRefreshing(false);
    }

    private void handleVideoResult(@Nullable Intent intent) {
        if (null == intent) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
            }
        });

        Uri uri = intent.getData();
        if (null != uri) {
            parseVideo(uri);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeDialog();
            }
        });
    }

    private void parseVideo(final Uri videoUri) {
        CCLog.i("parseVideoInfo, videoPath : " + videoUri);

        MediaExtractor extractor = null;
        FileOutputStream outputStreamH264 = null;
        FileOutputStream outputStreamAAC = null;
        MediaMuxer mediaMuxer = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(mContext, videoUri, null);
            String fileName = FileUtil.getTimeFormat();

            // 视频混合
            String newVideoName = FileUtil.PATH_VIDEO_CACHE + File.separator + fileName + ".mp4";
            mediaMuxer = new MediaMuxer(newVideoName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;

            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

                ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
                extractor.selectTrack(i);
                if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mimeType)) {
                    String videoFileName = FileUtil.PATH_VIDEO_CACHE + File.separator + fileName + ".h264";
                    outputStreamH264 = new FileOutputStream(videoFileName);
                    videoTrackIndex = mediaMuxer.addTrack(mediaFormat);

                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        byte[] buffer = new byte[readByteCount];
                        byteBuffer.get(buffer);
                        outputStreamH264.write(buffer);
                        byteBuffer.clear();
                        extractor.advance();
                    }

                } else if (MediaFormat.MIMETYPE_AUDIO_AAC.equals(mimeType)) {
                    String videoFileName = FileUtil.PATH_VIDEO_CACHE + File.separator + fileName + ".aac";
                    outputStreamAAC = new FileOutputStream(videoFileName);
                    audioTrackIndex = mediaMuxer.addTrack(mediaFormat);

                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        byte[] buffer = new byte[readByteCount];
                        byteBuffer.get(buffer);
                        outputStreamAAC.write(buffer);
                        byteBuffer.clear();
                        extractor.advance();
                    }
                }

                extractor.unselectTrack(i);
            }

            CCLog.i("videoTrack: " + videoTrackIndex + " , audioTrack: " + audioTrackIndex);
            extractor.release();
            extractor = new MediaExtractor();
            extractor.setDataSource(mContext, videoUri, null);

            // 为了方便，直接在这里重新合成
            mediaMuxer.start();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
                extractor.selectTrack(i);

                CCLog.i("startMuxer, maxInputSize: " + maxInputSize);

                if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mimeType)) {
                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        bufferInfo.size = readByteCount;
                        bufferInfo.offset = 0;
                        bufferInfo.flags = extractor.getSampleFlags();
                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                        mediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, bufferInfo);

                        byteBuffer.clear();
                        extractor.advance();
                    }
                } else if (MediaFormat.MIMETYPE_AUDIO_AAC.equals(mimeType)) {
                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        bufferInfo.size = readByteCount;
                        bufferInfo.offset = 0;
                        bufferInfo.flags = extractor.getSampleFlags();
                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                        mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, bufferInfo);

                        byteBuffer.clear();
                        extractor.advance();
                    }
                }
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;

            toastMessage("已解析文件：" + extractor.getTrackCount(), true);
        } catch (IOException e) {
            e.printStackTrace();
            CCLog.i("parseVideoInfo, e:" + e.toString());
        } finally {
            FileUtil.tryClose(outputStreamH264);
            FileUtil.tryClose(outputStreamAAC);

            if (null != extractor) {
                extractor.release();
            }

            if (null != mediaMuxer) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
        }
    }


    private void parseH264File(File h264File) {
        MediaExtractor extractor = null;

        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(h264File.getAbsolutePath());

            int trackCount = extractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = extractor.getTrackFormat(i);

                CCLog.i("parseH264File, format: " + format);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CCLog.i("parseH264File, e: " + e.toString());
        } finally {
            if (null != extractor) {
                extractor.release();
                extractor = null;
            }
        }
    }

    private void toastMessage(String message, boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(mContext, message);

                if (refresh) {
                    loadVideoFiles();
                }
            }
        });
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("视频解析中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void closeDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
