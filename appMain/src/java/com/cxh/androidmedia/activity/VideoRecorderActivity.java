package com.cxh.androidmedia.activity;

import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2019-02-27  00:20
 * Desc :
 */
public class VideoRecorderActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final int MSG_REFRESH_FOCUS = 0;
    public static final int MSG_TAKE_PHOTO = 1;


    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.tv_switch)
    TextView mTvSwitch;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mCameraCount;
    private boolean mSurfaceActive;
    private boolean mFaceCamera;

    private boolean mTakePhoto;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_video_recorde;
    }

    @Override
    protected void init() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ToastUtil.show(mContext, "该设备不支持相机");
            finish();
        }

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mCameraCount = Camera.getNumberOfCameras();
        CCLog.i(" 支持的相机数量 ： " + mCameraCount);
        // 没有前置摄像头
        if (mCameraCount < 2) {
            mTvSwitch.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        if (null != mSurfaceHolder) {
            mSurfaceHolder.removeCallback(this);
        }
    }

    @Override
    @OnClick({R.id.tv_capture, R.id.tv_video_start, R.id.tv_video_end, R.id.tv_switch})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.tv_capture: {
                mTakePhoto = true;
            }
            break;
            case R.id.tv_video_start: {

            }
            break;
            case R.id.tv_video_end: {

            }
            break;
            case R.id.tv_switch: {
                switchCamera();
            }
            break;
        }
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case MSG_REFRESH_FOCUS:
                if (mSurfaceActive && null != mCamera) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            // CCLog.i(" 对焦：" + success);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceActive = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceActive = false;
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // 这里的data就是NV21的图像数据
        // CCLog.i("data : " + data.length);

        if (mTakePhoto) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            takePhoto(data, size.width, size.height);
            mTakePhoto = false;
        }
    }

    private void releaseCamera() {
        CCLog.i(Thread.currentThread().toString());

        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCamera(SurfaceHolder holder) {
        try {
            if (mFaceCamera && mCameraCount > 1) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                mFaceCamera = true;
            } else {
                mCamera = Camera.open();
                mFaceCamera = false;
            }

            if (null != mCamera) {
                // 不是所有机器都支持
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                // parameters.setPreviewFormat(getSupportFormat(parameters.getSupportedPreviewFormats()));

                // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                parameters.setFocusMode(getSurppotFocusMode(parameters.getSupportedFocusModes()));

                // 预览帧率调整
                parameters.setPreviewFrameRate(getFitFrameRate(parameters.getSupportedPreviewFrameRates()));
                // 预览大小调整
                Camera.Size previewSize = getFitPreviewSize(parameters.getSupportedPreviewSizes());
                if (null != previewSize) {
                    CCLog.i("get fit size : " + previewSize.width + " , " + previewSize.height);
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                } else {
                    adjustSurfaceSize(parameters.getPreviewSize());
                    ToastUtil.show(mContext, "没有合适的预览尺寸");
                    CCLog.i("no fit yuv size, user default size. " + parameters.getPreviewSize().width + " , " + parameters.getPreviewSize().height);
                }

                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewCallback(this);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

            } else {
                ToastUtil.show(mContext, "相机开启失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.show(mContext, "相机开启失败：" + e.toString());
        }
    }

    private void switchCamera() {
        if (null != mCamera && mSurfaceActive && mCameraCount > 1) {
            releaseCamera();

            mFaceCamera = !mFaceCamera;
            initCamera(mSurfaceHolder);
        }
    }


    private void adjustSurfaceSize(Camera.Size size) {
        float previewScale = (float) size.height / (float) size.width;
        int exceptWidth = (int) (mSurfaceView.getHeight() * previewScale);
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        params.height = mSurfaceView.getHeight();
        params.width = exceptWidth;
        mSurfaceView.setLayoutParams(params);
    }

    private Camera.Size getFitPreviewSize(List<Camera.Size> previewSizes) {
        if (null != previewSizes && !previewSizes.isEmpty()) {
            Camera.Size fitSize = previewSizes.get(0);
            float surfaceScale = mSurfaceView.getMeasuredWidth() * 1f / mSurfaceView.getMeasuredHeight();
            float defaultScale = fitSize.height * 1f / fitSize.width;
            float defaultFlag = Math.abs(surfaceScale - defaultScale);

            for (int i = 1; i < previewSizes.size(); i++) {
                Camera.Size size = previewSizes.get(i);
                float currScale = size.height * 1f / size.width;
                float currFlag = Math.abs(surfaceScale - currScale);
                if (currFlag < defaultFlag) {
                    defaultFlag = currFlag;
                    fitSize = size;
                }
            }
            CCLog.i(" width : " + fitSize.width + " , height : " + fitSize.height + " , needScale : " + surfaceScale + " , currScale : " + defaultScale);
            return fitSize;
        }

        return null;
    }

    private int getFitFrameRate(List<Integer> frameRates) {
        int frameRate = 15;
        for (int i : frameRates) {
            CCLog.i("support frame rate : " + i);
            frameRate = i;
        }
        return frameRate;
    }

    private String getSurppotFocusMode(List<String> focusModes) {
        for (String str : focusModes) {
            CCLog.i("get support focus modes : " + str);
        }

        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            return Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        }
        return focusModes.get(0);
    }

    private Integer getSupportFormat(List<Integer> formats) {
        for (Integer integer : formats) {
            CCLog.i("get support focus formats : " + integer);
        }
        return formats.get(0);
    }


    private void takePhoto(byte[] data, int width, int height) {

        StringBuilder sb = new StringBuilder(FileUtil.PATH_IMAGE_PHOTO);
        sb.append(File.separator);
        sb.append("take_photo_");
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");

        boolean result = FileUtil.saveNV21ToStorage(data, width, height, sb.toString(), mFaceCamera);
        if (result) {
            sb.insert(0, "图片已保存至：");
            ToastUtil.show(mContext, sb.toString());
        }
    }
}
