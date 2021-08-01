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
import com.cxh.androidmedia.beans.MediaFileWrapper;
import com.cxh.androidmedia.manager.VideoParseManager;
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
                    // parseH264File(h264File);
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
                for (File f : files) {
                    mFileList.add(new MediaFileWrapper(f, MediaFileWrapper.TYPE_VIDEO));
                }
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
                showProgressDialog("视频解析中...");
            }
        });

        Uri uri = intent.getData();
        if (null != uri) {
            VideoParseManager.parseAndMuxer(mContext, uri, FileUtil.PATH_VIDEO_CACHE);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadVideoFiles();
                closeProgressDialog();
            }
        });
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
}
