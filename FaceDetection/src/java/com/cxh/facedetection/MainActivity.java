package com.cxh.facedetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.zeusee.main.hyperlandmark.jni.Face;
import com.zeusee.main.hyperlandmark.jni.FaceTracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    void InitModelFiles() {

        String assetPath = "ZeuseesFaceTracking";
        String sdcardPath = Environment.getExternalStorageDirectory()
                + File.separator + assetPath;
        FileUtil.copyFilesFromAssets(this, assetPath, sdcardPath);

    }


    private String[] denied;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public static int height = 480;
    public static int width = 640;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (PermissionChecker.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                    list.add(permissions[i]);
                }
            }
            if (list.size() != 0) {
                denied = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    denied[i] = list.get(i);
                }
                ActivityCompat.requestPermissions(this, denied, 5);
            } else {
                init();
            }
        } else {
            init();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5) {
            boolean isDenied = false;
            for (int i = 0; i < denied.length; i++) {
                String permission = denied[i];
                for (int j = 0; j < permissions.length; j++) {
                    if (permissions[j].equals(permission)) {
                        if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                            isDenied = true;
                            break;
                        }
                    }
                }
            }
            if (isDenied) {
                Toast.makeText(this, "请开启权限", Toast.LENGTH_SHORT).show();
            } else {
                init();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private byte[] mNv21Data;
    private CameraOverlap cameraOverlap;
    private final Object lockObj = new Object();

    private SurfaceView mSurfaceView;

    private EGLUtils mEglUtils;
    private GLFramebuffer mFramebuffer;
    private GLFrame mFrame;
    private GLPoints mPoints;
    private GLBitmap mBitmap;

    private SeekBar seekBarA;
    private SeekBar seekBarB;
    private SeekBar seekBarC;

    private void init() {
        InitModelFiles();

        FaceTracking.getInstance().FaceTrackingInit(FileUtil.modelPath + "/models", height, width);

        cameraOverlap = new CameraOverlap(this);
        mNv21Data = new byte[CameraOverlap.PREVIEW_WIDTH * CameraOverlap.PREVIEW_HEIGHT * 2];
        mFramebuffer = new GLFramebuffer();
        mFrame = new GLFrame();
        mPoints = new GLPoints();
        mBitmap = new GLBitmap(this, 0);
        mHandlerThread = new HandlerThread("DrawFacePointsThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        cameraOverlap.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, Camera camera) {

                final Camera.Size previewSize = camera.getParameters().getPreviewSize();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils == null) {
                            return;
                        }
                        mFrame.setS(seekBarA.getProgress() / 100.0f);
                        mFrame.setH(seekBarB.getProgress() / 360.0f);
                        mFrame.setL(seekBarC.getProgress() / 100.0f - 1);


                        long start = System.currentTimeMillis();

                        FaceTracking.getInstance().Update(data, previewSize.height, previewSize.width);


                        Log.e("TAG", "====用时=====" + (System.currentTimeMillis() - start));

                        boolean rotate270 = cameraOverlap.getOrientation() == 270;

                        List<Face> faceActions = FaceTracking.getInstance().getTrackingInfo();
                        float[] p = null;
                        float[] points = null;
                        for (Face r : faceActions) {
                            points = new float[106 * 2];
                            for (int i = 0; i < 106; i++) {
                                int x;
                                if (rotate270) {
                                    x = r.landmarks[i * 2] * CameraOverlap.SCALLE_FACTOR;
                                } else {
                                    x = CameraOverlap.PREVIEW_HEIGHT - r.landmarks[i * 2];
                                }
                                int y = r.landmarks[i * 2 + 1] * CameraOverlap.SCALLE_FACTOR;
                                points[i * 2] = view2openglX(x, CameraOverlap.PREVIEW_HEIGHT);
                                points[i * 2 + 1] = view2openglY(y, CameraOverlap.PREVIEW_WIDTH);
                                if (i == 70) {
                                    p = new float[8];
                                    p[0] = view2openglX(x + 20, CameraOverlap.PREVIEW_HEIGHT);
                                    p[1] = view2openglY(y - 20, CameraOverlap.PREVIEW_WIDTH);
                                    p[2] = view2openglX(x - 20, CameraOverlap.PREVIEW_HEIGHT);
                                    p[3] = view2openglY(y - 20, CameraOverlap.PREVIEW_WIDTH);
                                    p[4] = view2openglX(x + 20, CameraOverlap.PREVIEW_HEIGHT);
                                    p[5] = view2openglY(y + 20, CameraOverlap.PREVIEW_WIDTH);
                                    p[6] = view2openglX(x - 20, CameraOverlap.PREVIEW_HEIGHT);
                                    p[7] = view2openglY(y + 20, CameraOverlap.PREVIEW_WIDTH);
                                }
                            }
                            if (p != null) {
                                break;
                            }
                        }
                        int tid = 0;
                        if (p != null) {
                            mBitmap.setPoints(p);
                            tid = mBitmap.drawFrame();
                        }
                        mFrame.drawFrame(tid, mFramebuffer.drawFrameBuffer(), mFramebuffer.getMatrix());
                        if (points != null) {
                            mPoints.setPoints(points);
                            mPoints.drawPoints();
                        }
                        mEglUtils.swap();

                    }
                });
            }
        });
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {
                Log.d("=============", "surfaceChanged");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils != null) {
                            mEglUtils.release();
                        }
                        mEglUtils = new EGLUtils();
                        mEglUtils.initEGL(holder.getSurface());
                        mFramebuffer.initFramebuffer();
                        mFrame.initFrame();
                        mFrame.setSize(width, height, CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                        mPoints.initPoints();
                        mBitmap.initFrame(CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                        cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                    }
                });

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraOverlap.release();
                        mFramebuffer.release();
                        mFrame.release();
                        mPoints.release();
                        mBitmap.release();
                        if (mEglUtils != null) {
                            mEglUtils.release();
                            mEglUtils = null;
                        }
                    }
                });

            }
        });
        if (mSurfaceView.getHolder().getSurface() != null && mSurfaceView.getWidth() > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglUtils != null) {
                        mEglUtils.release();
                    }
                    mEglUtils = new EGLUtils();
                    mEglUtils.initEGL(mSurfaceView.getHolder().getSurface());
                    mFramebuffer.initFramebuffer();
                    mFrame.initFrame();
                    mFrame.setSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                    mPoints.initPoints();
                    mBitmap.initFrame(CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                    cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                }
            });
        }
    }

    private float view2openglX(int x, int width) {
        float centerX = width / 2.0f;
        float t = x - centerX;
        return t / centerX;
    }

    private float view2openglY(int y, int height) {
        float centerY = height / 2.0f;
        float s = centerY - y;
        return s / centerY;
    }
}
