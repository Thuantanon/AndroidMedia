package com.cxh.androidmedia.activity.mediacodec;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render_new.Camera2Helper;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.MediaStoreUtil;
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2021/5/31  00:36
 * Desc :
 */
public class Camera2RecorderActivity extends BaseActivity implements Camera2Helper.Camera2Callback {

    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.tv_switch)
    TextView mTvSwitch;
    @BindView(R.id.tv_video_record)
    TextView mTvRecord;
    @BindView(R.id.iv_picture)
    ImageView mIvPicture;

    private Camera2Helper mCameraHelper;
    private Handler mRecordHandler;
    private boolean mCameraFaceFront;
    private boolean mbIsVideoRecording;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_camera_video_recorde;
    }

    @Override
    protected void init() {
        HandlerThread handlerThread = new HandlerThread("RecordThread");
        handlerThread.start();
        mRecordHandler = new Handler(handlerThread.getLooper());

        mCameraHelper = new Camera2Helper(mContext);
        mCameraHelper.setCamera2Callback(this);

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                CCLog.i("surfaceCreated");
                mCameraHelper.openCamera(mCameraFaceFront);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                CCLog.i("surfaceChanged, width: " + width + " , height: " + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                CCLog.i("surfaceDestroyed");
                mCameraHelper.closeCamera();
            }
        });
    }

    @Override
    @OnClick({R.id.tv_capture, R.id.tv_video_record, R.id.tv_switch})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.tv_capture: {
                mCameraHelper.takePicture();
            }
            break;
            case R.id.tv_video_record: {
                if (!mbIsVideoRecording) {
                    mbIsVideoRecording = true;
                    mTvRecord.setText("视频录制中...");
                } else {
                    mbIsVideoRecording = false;
                    mTvRecord.setText("开始录制");
                }

                mCameraHelper.startPreview(mbIsVideoRecording);
            }
            break;
            case R.id.tv_switch: {
                mCameraFaceFront = !mCameraFaceFront;
                mCameraHelper.closeCamera();
                mCameraHelper.openCamera(mCameraFaceFront);
            }
            break;
        }
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mRecordHandler) {
            mRecordHandler.getLooper().quitSafely();
            mRecordHandler = null;
        }

        if (null != mCameraHelper) {
            mCameraHelper.release();
            mCameraHelper = null;
        }
    }

    @Override
    public void onPreviewSizeChanged(int width, int height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // changeSurfaceRatio(width, height);
            }
        });
    }

    @Override
    public List<Surface> configSurfaceList() {
        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(mSurfaceView.getHolder().getSurface());
        return surfaces;
    }

    @Override
    public String generateVideoPath() {
        String fileName = "Media_Recorder_" + FileUtil.getTimeFormat() + ".mp4";
        return FileUtil.PATH_VIDEO + File.separator + fileName;
    }

    @Override
    public void onRecordError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mbIsVideoRecording = false;
                mTvRecord.setText("开始录制");
            }
        });
    }

    @Override
    public void onRecordFinish(String videoPath) {
        MediaStoreUtil.saveVideoToMediaStore(mContext, videoPath, "测试");
    }

    @Override
    public void onPictureAvailable(Bitmap bitmap) {
        CCLog.i("onPictureAvailable, bitmap: " + bitmap);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mIvPicture.setImageBitmap(bitmap);

                String fileName = "camera2_take_picture_" + System.currentTimeMillis() + ".jpg";
                StringBuilder sb = new StringBuilder(FileUtil.PATH_IMAGE_PHOTO);
                sb.append(File.separator);
                sb.append(fileName);

                FileUtil.saveBitmapToStorage(bitmap, sb.toString());

                MediaStoreUtil.saveImageToMediaStore(mContext, sb.toString(), fileName, "Camera2拍照");

                ToastUtil.show(mContext, "照片已保存至：" + sb.toString());
            }
        });
    }

    private void changeSurfaceRatio(int width, int height) {
        float previewRatio = (float) width / (float) height;
        int changeHeight = (int) (mSurfaceView.getHeight() / previewRatio);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        params.width = mSurfaceView.getWidth();
        params.height = changeHeight;
        mSurfaceView.setLayoutParams(params);
    }

}
