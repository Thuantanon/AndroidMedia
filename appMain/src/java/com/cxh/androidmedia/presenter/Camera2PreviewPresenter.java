package com.cxh.androidmedia.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.activity.GLRender5Activity;
import com.cxh.androidmedia.presenter.base.BaseActivityPresenter;
import com.cxh.androidmedia.utils.CCLog;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Created by Cxh
 * Time : 2020-07-10  10:44
 * Desc :
 */
public class Camera2PreviewPresenter extends BaseActivityPresenter<GLRender5Activity> {

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private ImageReader mImageReader;
    private Surface mSurface;

    private String[] mCameraIds;
    private boolean mDefaultCamera = true;

    public Camera2PreviewPresenter(@NonNull GLRender5Activity target) {
        super(target);
    }

    @Override
    public void onCreate() {

        // 获取Camera
        mCameraManager = (CameraManager) mTarget.getSystemService(Context.CAMERA_SERVICE);
        if (null == mCameraManager) {
            getTarget().errorFinish(null, "获取CameraManager失败");
        }

        // 获取相机id
        try {
            mCameraIds = mCameraManager.getCameraIdList();
            if (null == mCameraIds || mCameraIds.length <= 0) {
                getTarget().errorFinish(null, "没有支持的相机id");
            }

            // 打开相机
            mDefaultCamera = true;
            String defaultCameraId = mCameraIds[0];
            if (mTarget.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mCameraManager.openCamera(defaultCameraId, null, mTarget.getHandler());
            }
        } catch (Exception e) {
            getTarget().errorFinish(e, "相机打开失败");
        }


        // 获取Camera
        mCameraManager = (CameraManager) getTarget().getSystemService(Context.CAMERA_SERVICE);
        if (null == mCameraManager) {
            getTarget().errorFinish(null, "获取CameraManager失败");
        }

        mTextureView.setKeepScreenOn(true);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
                // 获取相机id
                try {
                    mCameraIds = mCameraManager.getCameraIdList();
                    if (mCameraIds.length <= 0) {
                        getTarget().errorFinish(null, "没有支持的相机id");
                    }

                    // 打开相机
                    String defaultCameraId = mCameraIds[0];
                    if (getTarget().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        mCameraManager.openCamera(defaultCameraId, new CameraDeviceCallback(Camera2PreviewPresenter.this),
                                getTarget().getHandler());
                    }
                } catch (Exception e) {
                    getTarget().errorFinish(e, "相机打开失败");
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = reader.acquireLatestImage();
                        byte[] yuvData = image.getPlanes()[0].getBuffer().array();
                        CCLog.i("yuvData : " + yuvData.length);
                    }
                }, getTarget().getHandler());
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }


    private static class CameraDeviceCallback extends CameraDevice.StateCallback {

        private WeakReference<Camera2PreviewPresenter> mGLRender5ActivityRef;

        public CameraDeviceCallback(@NonNull Camera2PreviewPresenter camera2PreviewPresenter) {
            mGLRender5ActivityRef = new WeakReference<>(camera2PreviewPresenter);
        }

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Camera2PreviewPresenter activity = mGLRender5ActivityRef.get();
            if (null == activity) {
                return;
            }

            activity.mCameraDevice = camera;
            try {
                camera.createCaptureSession(Arrays.asList(activity.mSurface), new CaptureSessionCallback(activity),
                        activity.getTarget().getHandler());
            } catch (Exception e) {
                activity.getTarget().errorFinish(e, "CameraDeviceCallback onOpened : ");
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Camera2PreviewPresenter activity = mGLRender5ActivityRef.get();
            if (null == activity) {
                return;
            }

            activity.getTarget().errorFinish(null, "CameraDeviceCallback onDisconnected : ");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Camera2PreviewPresenter activity = mGLRender5ActivityRef.get();
            if (null == activity) {
                return;
            }
            activity.getTarget().errorFinish(null, "CameraDeviceCallback onError : " + error);
        }
    }


    private static class CaptureSessionCallback extends CameraCaptureSession.StateCallback {

        private WeakReference<Camera2PreviewPresenter> mGLRender5ActivityRef;

        public CaptureSessionCallback(@NonNull Camera2PreviewPresenter glRender5Activity) {
            mGLRender5ActivityRef = new WeakReference<>(glRender5Activity);
        }

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Camera2PreviewPresenter activity = mGLRender5ActivityRef.get();
            if (null == activity) {
                return;
            }

            try {
                CaptureRequest.Builder requestBuilder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                requestBuilder.addTarget(activity.mSurface);
                requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                requestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE);
                CaptureRequest captureRequest = requestBuilder.build();

                session.setRepeatingRequest(captureRequest, new CaptureRequestListener(activity), activity.getTarget().getHandler());
            } catch (Exception e) {
                activity.getTarget().errorFinish(e, "CaptureSessionCallback onConfigured : ");
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Camera2PreviewPresenter activity = mGLRender5ActivityRef.get();
            if (null == activity) {
                return;
            }
            activity.getTarget().errorFinish(null, "CaptureSessionCallback onConfigureFailed : ");
        }
    }

    private static class CaptureRequestListener extends CameraCaptureSession.CaptureCallback {

        private WeakReference<Camera2PreviewPresenter> mGLRender5ActivityRef;

        public CaptureRequestListener(@NonNull Camera2PreviewPresenter glRender5Activity) {
            mGLRender5ActivityRef = new WeakReference<>(glRender5Activity);
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                        @NonNull CaptureResult captureResult) {
            super.onCaptureProgressed(session, request, captureResult);
        }

    }
}
