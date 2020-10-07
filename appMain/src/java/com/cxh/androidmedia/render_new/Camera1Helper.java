package com.cxh.androidmedia.render_new;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.cxh.androidmedia.utils.CCLog;

/**
 * Created by Cxh
 * Time : 2020-09-17  00:59
 * Desc :
 */
public class Camera1Helper {

    public static final int PREVIEW_WIDTH = 640;
    public static final int PREVIEW_HEIGHT = 480;
    public static final int SCALE_FACTOR = 2;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private Camera.Parameters mParameters;
    private Camera.PreviewCallback mPreviewCallback;

    public Camera1Helper() {

    }

    public void openCamera(SurfaceTexture surfaceTexture) {
        closeCamera();

        mCameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            try {
                if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera = Camera.open(i);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                mCamera = null;
                continue;
            }
        }

        // 设置相机参数
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(mPreviewCallback);
            mParameters = mCamera.getParameters();
            mParameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            CCLog.i(" openCamera error : " + e.toString());
        }
    }

    public void closeCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera.CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }
}
