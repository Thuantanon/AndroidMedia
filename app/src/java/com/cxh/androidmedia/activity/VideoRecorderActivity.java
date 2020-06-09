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
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2019-02-27  00:20
 * Desc :
 */
public class VideoRecorderActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final int INTERNAL_REFRESH_FOCUS = 0;


    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.tv_switch)
    TextView mTvSwitch;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mCameraCount;
    private boolean mSurfaceActive;
    private boolean mFaceCamera;


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
        switch (message.what){
            case INTERNAL_REFRESH_FOCUS:
                if(mSurfaceActive && null != mCamera){
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            CCLog.i(" 对焦：" + success);
                            getHandler().removeMessages(INTERNAL_REFRESH_FOCUS);
                            getHandler().sendEmptyMessage(INTERNAL_REFRESH_FOCUS);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera(holder);
        mSurfaceActive = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        releaseCamera();
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
    }

    private void releaseCamera() {
        CCLog.i(Thread.currentThread().toString());

        if (null != mCamera) {
            getHandler().removeMessages(INTERNAL_REFRESH_FOCUS);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCamera(SurfaceHolder holder) {
        getHandler().removeMessages(INTERNAL_REFRESH_FOCUS);
        try {
            if (mFaceCamera && mCameraCount > 1) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                mFaceCamera = true;
            } else {
                mCamera = Camera.open();
                mFaceCamera = false;
            }

            if (null != mCamera) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewFrameRate(30);
                // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                Camera.Size previewSize = parameters.getPreviewSize();
                adjustSurfaceSize(previewSize);

                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewCallback(this);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

                getHandler().sendEmptyMessage(INTERNAL_REFRESH_FOCUS);

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
}
