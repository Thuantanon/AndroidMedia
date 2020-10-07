package com.cxh.androidmedia.render_new;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.utils.ToastUtil;

import java.util.Arrays;


/**
 * Created by Cxh
 * Time : 2020-09-13  10:55
 * Desc :
 */
public class Camera2Helper {

    public static final int MSG_OPEN_CAMERA_SUCCESS = 1;

    private Activity mActivity;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private Handler mHandler;

    public Camera2Helper(Activity activity, Handler handler) {
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        mHandler = handler;
    }

    /**
     * 配置相机参数
     */
    public void openCamera() {

        if (mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.show(mActivity, "无相机权限，打开相机失败");
            return;
        }

        try {
            mCameraManager.openCamera("", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCameraSession(Surface surface) {

        if (null == mCameraDevice) {
            return;
        }

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startRepeatCapture(){
        if(null == mCameraCaptureSession){
            return;
        }

    }

    public void release() {
        mActivity = null;
    }
}
