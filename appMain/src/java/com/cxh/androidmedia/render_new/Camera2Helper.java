package com.cxh.androidmedia.render_new;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.StringUtil;
import com.cxh.androidmedia.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Cxh
 * Time : 2020-09-13  10:55
 * Desc :
 */
public class Camera2Helper implements Handler.Callback, ImageReader.OnImageAvailableListener {
    public static final String TAG = "Camera2Helper";

    public static final int MSG_OPEN_CAMERA = 1;
    public static final int MSG_CREATE_SESSION = 2;
    public static final int MSG_START_PREVIEW = 3;
    public static final int MSG_START_RECORD = 4;
    public static final int MSG_STOP_RECORD = 5;
    public static final int MSG_CAPTURE_PIC = 6;
    public static final int MSG_CLOSE_CAMERA = 7;
    public static final int MSG_CLOSE_SESSION = 8;

    private Context mContext;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private Handler mCameraControlHandler;
    private Handler mCaptureHandler;
    private ConditionVariable mCameraCloseLock = new ConditionVariable();
    private Size[] mPreviewSize;
    private Size mPictureSize;
    private String mCameraId;

    private MediaRecorder mMediaRecorder;
    private String mVideoPath;
    private ImageReader mCaptureReader;

    private Camera2Callback mCamera2Callback;
    private List<Surface> mPreviewSurfaceList;
    private boolean mVideoRecording;

    public Camera2Helper(Context context) {
        mContext = context;

        HandlerThread handlerThreadControl = new HandlerThread("CameraControl");
        handlerThreadControl.start();
        mCameraControlHandler = new Handler(handlerThreadControl.getLooper(), this);

        HandlerThread captureHandlerThread = new HandlerThread("CameraControl");
        captureHandlerThread.start();
        mCaptureHandler = new Handler(captureHandlerThread.getLooper(), this);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        CCLog.i(TAG, "handleMessage, msg: " + msg);

        switch (msg.what) {
            case MSG_OPEN_CAMERA: {
                boolean isFront = (boolean) msg.obj;
                doOpenCamera(isFront);
            }
            break;
            case MSG_CREATE_SESSION: {
                mVideoRecording = (boolean) msg.obj;
                doCreateCameraSession();
            }
            break;
            case MSG_START_PREVIEW: {
                doStartPreview();
            }
            break;
            case MSG_CLOSE_CAMERA: {
                doCloseCamera();
            }
            break;
            case MSG_CLOSE_SESSION: {
                doCloseSession();
            }
            break;
            case MSG_CAPTURE_PIC: {
                doCapturePicture();
            }
            break;
        }

        return true;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        CCLog.i("onImageAvailable, width: " + reader.getWidth() + " , height: " + reader.getHeight());
        Image image = reader.acquireLatestImage();
        if (null != image) {
            Image.Plane plane = image.getPlanes()[0];
            byte[] buffer = new byte[plane.getBuffer().remaining()];
            plane.getBuffer().get(buffer);

            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);

            Bitmap picBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();

            if (null != picBitmap) {
                mCamera2Callback.onPictureAvailable(picBitmap);
            }

            image.close();
        }
    }

    public void openCamera() {
        openCamera(false);
    }

    public void openCamera(boolean front) {
        mCameraControlHandler.removeMessages(MSG_OPEN_CAMERA);
        mCameraControlHandler.removeMessages(MSG_CREATE_SESSION);

        Message message = mCameraControlHandler.obtainMessage(MSG_OPEN_CAMERA);
        message.obj = front;
        mCameraControlHandler.sendMessage(message);
    }

    public void closeCamera() {
        mCameraControlHandler.removeCallbacksAndMessages(null);

        if (null != mCameraCaptureSession) {
            mCameraControlHandler.sendEmptyMessage(MSG_CLOSE_SESSION);
            mCameraControlHandler.sendEmptyMessage(MSG_CLOSE_CAMERA);

        } else if (null != mCameraDevice) {
            mCameraControlHandler.sendEmptyMessage(MSG_CLOSE_CAMERA);
        }
    }

    public void startPreview(boolean isRecord) {
        if (null == mCameraDevice) {
            CCLog.i("startPreview, return, null == mCameraDevice");
            return;
        }

        mVideoRecording = isRecord;

        Message message = mCameraControlHandler.obtainMessage(MSG_CREATE_SESSION);
        message.obj = isRecord;
        mCameraControlHandler.sendMessage(message);
    }

    public void takePicture() {
        if (null == mCameraDevice) {
            CCLog.i("takePicture, return, null == mCameraDevice");
            return;
        }

        mCameraControlHandler.sendEmptyMessage(MSG_CAPTURE_PIC);
    }


    /**
     * 配置相机参数
     */
    private void doOpenCamera(boolean isFront) {

        if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.show(mContext, "无相机权限，打开相机失败");
            return;
        }

        if (null != mCameraCaptureSession) {
            doCloseSession();
        }

        if (null != mCameraDevice) {
            doCloseCamera();
        }

        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (null == cameraManager) {
            CCLog.i(TAG, "doOpenCamera, return, cameraManager is null");
            return;
        }

        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 1 && isFront) {
                mCameraId = cameraIds[1];
            } else {
                mCameraId = cameraIds[0];
            }

            // 设置预览参数
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (null != configurationMap) {
                mPreviewSize = configurationMap.getOutputSizes(SurfaceTexture.class);
                if (null != mPreviewSize) {
                    CCLog.i(TAG, "doOpenCamera, sizes: " + StringUtil.toString(mPreviewSize));
                }

                mPictureSize = selectPictureSize(0.75f);
                mCaptureReader = ImageReader.newInstance(mPictureSize.getHeight(), mPictureSize.getWidth(), ImageFormat.JPEG, 1);
                mCaptureReader.setOnImageAvailableListener(this, mCameraControlHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();

            CCLog.i(TAG, "doOpenCamera, return, e: " + e.toString());
            return;
        }

        try {
            cameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    CCLog.i(TAG, "doOpenCamera, onOpened, cameraId: " + camera.getId());

                    mCameraDevice = camera;

                    startPreview(false);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    CCLog.i(TAG, "doOpenCamera, onDisconnected, cameraId: " + camera.getId());

                    mCameraCloseLock.open();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    CCLog.i(TAG, "doOpenCamera, onError, cameraId: " + camera.getId() + " , error: " + error);

                    mCameraCloseLock.open();
                }

                @Override
                public void onClosed(@NonNull CameraDevice camera) {
                    super.onClosed(camera);

                    mCameraCloseLock.open();
                }
            }, mCameraControlHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCloseCamera() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void doCreateCameraSession() {

        if (null == mCameraDevice) {
            CCLog.i(TAG, "doCreateCameraSession, return, mCameraDevice is null");
            return;
        }

        doCloseSession();

        try {
            mPreviewSurfaceList = mCamera2Callback.configSurfaceList();

            List<Surface> configSurfaceList = new ArrayList<>(mPreviewSurfaceList);
            configSurfaceList.add(mCaptureReader.getSurface());

            if (mVideoRecording) {
                configMediaRecorder();
                configSurfaceList.add(mMediaRecorder.getSurface());
            }

            mCameraDevice.createCaptureSession(configSurfaceList, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    CCLog.i(TAG, "doCreateCameraSession, onConfigured, session: " + session);

                    mCameraCaptureSession = session;

                    if (mVideoRecording) {
                        mMediaRecorder.start();
                    }

                    mCameraControlHandler.sendEmptyMessage(MSG_START_PREVIEW);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    CCLog.i(TAG, "doCreateCameraSession, onConfigureFailed, session: " + session);

                    closeCamera();
                }
            }, mCameraControlHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCloseSession() {
        CCLog.i(TAG, "doCloseSession");

        try {
            if (mVideoRecording) {
                doStopRecordVideo();
            }

            if (null != mCameraCaptureSession) {
                mCameraCaptureSession.stopRepeating();
                mCameraCaptureSession.abortCaptures();
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();

            CCLog.i(TAG, "doCloseSession, e: " + e.toString());
        }
    }

    private void doStartPreview() {
        CCLog.i("doStartPreview, mVideoRecording: " + mVideoRecording);
        if (null == mCameraCaptureSession) {
            return;
        }

        try {
            int template = mVideoRecording ? CameraDevice.TEMPLATE_RECORD : CameraDevice.TEMPLATE_PREVIEW;
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(template);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
            for (Surface surface : mPreviewSurfaceList) {
                builder.addTarget(surface);
            }

            if (mVideoRecording) {
                builder.addTarget(mMediaRecorder.getSurface());
            }

            mCameraCaptureSession.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    CCLog.i("doStartPreview, onCaptureFailed, failure: " + failure);
                }
            }, mCameraControlHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCapturePicture() {
        CCLog.i("doCapturePicture, mVideoRecording: " + mVideoRecording);
        if (null == mCameraCaptureSession) {
            return;
        }

        try {

            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
            builder.addTarget(mCaptureReader.getSurface());

            mCameraCaptureSession.capture(builder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    CCLog.i("doCapturePicture, onCaptureCompleted, result: " + result);
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    CCLog.i("doCapturePicture, onCaptureFailed, failure: " + failure);
                }
            }, mCaptureHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        CCLog.i(TAG, "release");
        mCameraCloseLock.block();

        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        if (null != mCameraControlHandler) {
            mCameraControlHandler.getLooper().quitSafely();
            mCameraControlHandler = null;
        }

        if (null != mCaptureHandler) {
            mCaptureHandler.getLooper().quitSafely();
            mCaptureHandler = null;
        }

        if (null != mCaptureReader) {
            mCaptureReader.close();
            mCaptureReader = null;
        }

        mContext = null;
    }

    private void configMediaRecorder() {

        try {
            if (null == mMediaRecorder) {
                mMediaRecorder = new MediaRecorder();
            }
            mMediaRecorder.reset();
            // start
            // 设置视频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOrientationHint(90);
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            mMediaRecorder.setProfile(profile);

            mVideoPath = mCamera2Callback.generateVideoPath();
            mMediaRecorder.setOutputFile(mVideoPath);
            // 预览
            // mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    CCLog.i("MediaRecorder.onInfo, what: " + what);
                }
            });

            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    CCLog.i("MediaRecorder.onError, what: " + what);
                    mVideoRecording = false;

                    startPreview(false);

                    mCamera2Callback.onRecordError();
                }
            });

            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            CCLog.i("configMediaRecorder, e: " + e.toString());
        }
    }

    private void doStopRecordVideo() {
        CCLog.i("doStopRecordVideo, path: " + mVideoPath);

        try {
            if (null != mMediaRecorder) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();

                if (!TextUtils.isEmpty(mVideoPath)) {
                    mCamera2Callback.onRecordFinish(mVideoPath);
                    mVideoPath = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Size selectPictureSize(float ratio) {
        for (Size size : mPreviewSize) {
            float rat = size.getHeight() / (float) size.getWidth();
            if (Math.abs(ratio - rat) <= 0.01) {
                return size;
            }
        }

        return new Size(1280, 960);
    }

    public void setCamera2Callback(Camera2Callback camera2Callback) {
        mCamera2Callback = camera2Callback;
    }

    public interface Camera2Callback {
        void onPreviewSizeChanged(int width, int height);

        void onPictureAvailable(Bitmap bitmap);

        List<Surface> configSurfaceList();

        String generateVideoPath();

        void onRecordError();

        void onRecordFinish(String videoPath);
    }
}
