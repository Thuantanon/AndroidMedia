package com.cxh.androidmedia.activity.mediacodec;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.cxh.androidmedia.utils.MediaStoreUtil;
import com.cxh.androidmedia.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2019-02-27  00:20
 * Desc :  Camera API已经废弃，录制视屏后面由Camera2 API完成
 */
public class Camera1RecorderActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final int MSG_REFRESH_FOCUS = 0;
    public static final int MSG_TAKE_PHOTO = 1;


    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.tv_switch)
    TextView mTvSwitch;
    @BindView(R.id.tv_video_record)
    TextView mTvRecord;

    private Handler mControlHandler;
    private Handler mSaveHandler;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private String mVideoPath;
    private String mVideoFilename;

    private int mCameraCount;
    private boolean mSurfaceActive;
    private boolean mFaceCamera;

    private boolean mTakePhoto;
    private boolean mIsRecoding;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_camera_video_recorde;
    }

    @Override
    protected void init() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ToastUtil.show(mContext, "该设备不支持相机");
            finish();
        }

        HandlerThread handlerThread = new HandlerThread("Camera Hal Control");
        handlerThread.start();
        mControlHandler = new Handler(handlerThread.getLooper());

        HandlerThread saveHandlerThread = new HandlerThread("Save Media");
        saveHandlerThread.start();
        mSaveHandler = new Handler(saveHandlerThread.getLooper());

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

        if (null != mControlHandler) {
            mControlHandler.getLooper().quitSafely();
            mControlHandler = null;
        }

        if (null != mSaveHandler) {
            mSaveHandler.getLooper().quitSafely();
            mSaveHandler = null;
        }
    }

    @Override
    @OnClick({R.id.tv_capture, R.id.tv_video_record, R.id.tv_switch})
    public void onViewClick(View view) {
        super.onViewClick(view);
        switch (view.getId()) {
            case R.id.tv_capture: {
                mTakePhoto = true;

                mControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                });
            }
            break;
            case R.id.tv_video_record: {
                mSaveHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsRecoding) {
                            stopRecordVideo();
                        } else {
                            startRecordVideo();
                        }
                    }
                });
            }
            break;
            case R.id.tv_switch: {

                mControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        switchCamera();
                    }
                });
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

        mControlHandler.post(new Runnable() {
            @Override
            public void run() {
                initCamera(holder);
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceActive = false;

        if (mIsRecoding) {
            mSaveHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopRecordVideo();
                }
            });
        }

        mControlHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                releaseCamera();
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // 这里的data就是NV21的图像数据
        // CCLog.i("data : " + data.length);

        if (mTakePhoto) {
//            Camera.Size size = camera.getParameters().getPreviewSize();
//            takePhoto(data, size.width, size.height);
//            mTakePhoto = false;
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

    private void takePicture() {
        if (null != mCamera) {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    CCLog.i("onShutter");
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    CCLog.i("onPictureTaken, raw data received");
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    CCLog.i("onPictureTaken, jpeg data received");
                    mTakePhoto = false;
                    mCamera.startPreview();

                    mSaveHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            StringBuilder sb = new StringBuilder(FileUtil.PATH_IMAGE_PHOTO);
                            sb.append(File.separator);
                            sb.append("camera1_take_picture_");
                            sb.append(System.currentTimeMillis());
                            sb.append(".jpg");

                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Matrix matrix = new Matrix();
                            if (mFaceCamera) {
                                matrix.setRotate(270);
                            } else {
                                matrix.setRotate(90);
                            }
                            Bitmap targetBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                            bitmap.recycle();
                            FileUtil.saveBitmapToStorage(targetBmp, sb.toString());

                            ToastUtil.show(mContext, "图片已保存至：" + sb.toString());
                            MediaStoreUtil.saveImageToMediaStore(mContext, sb.toString(), sb.toString().replace("FileUtil.PATH_IMAGE_PHOTO", ""),
                                    "Camera1拍照");
                        }
                    });
                }
            });
        }
    }

    private void startRecordVideo() {
        mIsRecoding = true;
        mCamera.stopPreview();//暂停相机预览

        mVideoFilename = "camera1_video_recorder_" + System.currentTimeMillis() + ".mp4";
        mVideoPath = FileUtil.PATH_VIDEO + File.separator + mVideoFilename;

        try {
            mCamera.unlock();
            // start
            MediaRecorder mediaRecorder = new MediaRecorder();
            // 设置视频源
            // mediaRecorder.setInputSurface(Surface);
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 设置格式
            // Android2.2以上使用Profile
            // mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            // mediaRecorder.setAudioChannels(1);
            // mediaRecorder.setAudioSamplingRate(44100);
            // mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            // mediaRecorder.setVideoFrameRate(30);
            // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOrientationHint(90);
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            mediaRecorder.setProfile(profile);

            mediaRecorder.setOutputFile(mVideoPath);
            // 预览
            mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    CCLog.i("MediaRecorder.onInfo, what: " + what);
                }
            });
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    CCLog.i("MediaRecorder.onError, what: " + what);
                    stopRecordVideo();
                }
            });

            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mIsRecoding = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvRecord.setText("视频录制中...");
            }
        });
    }

    private void stopRecordVideo() {
        mIsRecoding = false;
        try {
            mCamera.lock();
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();

            if (null != mMediaRecorder) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }

            final File file = new File(mVideoPath);
            if (file.exists()) {
                MediaStoreUtil.saveVideoToMediaStore(mContext, mVideoPath, "Camera1录制");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (file.exists()) {
                        ToastUtil.show(mContext, "视频已保存至：" + mVideoPath);
                    } else {
                        ToastUtil.show(mContext, "录制失败");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvRecord.setText("点击录制视频");
            }
        });
    }
}
