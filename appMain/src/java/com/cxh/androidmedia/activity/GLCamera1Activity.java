package com.cxh.androidmedia.activity;

import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.dialog.BeautySetDialog;
import com.cxh.androidmedia.render_new.Camera1Helper;
import com.cxh.androidmedia.render_new.EGLHelper;
import com.cxh.androidmedia.render_new.render.SurfaceViewRender;
import com.cxh.androidmedia.render_old.bean.BeautyBean;
import com.cxh.androidmedia.render_old.bean.BeautyParams;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.zeusee.main.hyperlandmark.jni.Face;
import com.zeusee.main.hyperlandmark.jni.FaceTracking;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:25
 * Desc : OpenGL ES、OpenCV实现高级美颜
 */
public class GLCamera1Activity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback,
        BeautySetDialog.OnBeautyChangedCallback {

    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.btn_beauty_set)
    TextView mBtnBeauty;

    private Camera1Helper mCamera1Helper;
    private EGLHelper mEGLHelper;
    private SurfaceViewRender mSurfaceViewRender;
    private BeautySetDialog mBeautySetDialog;

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_3;
    }

    @Override
    protected void init() {
        initFaceModels();

        mCamera1Helper = new Camera1Helper();
        mHandlerThread = new HandlerThread("PreviewHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    @OnClick({R.id.btn_beauty_set})
    public void onViewClick(View view) {
        super.onViewClick(view);
        if (R.id.btn_beauty_set == view.getId()) {

            if (null == mBeautySetDialog) {
                mBeautySetDialog = new BeautySetDialog(mContext);
                mBeautySetDialog.setBeautyCallback(this);
                mBeautySetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mBtnBeauty.setVisibility(View.VISIBLE);
                    }
                });
            }

            mBtnBeauty.setVisibility(View.INVISIBLE);
            mBeautySetDialog.show();
        }
    }

    @Override
    public void onBeautyChanged(BeautyBean data) {
        if (null == mSurfaceViewRender) {
            return;
        }

        switch (data.getBeautyType()) {
            case BeautyParams.BEAUTY_TYPE_WHITE:
                mSurfaceViewRender.getInputWhiteFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_BLUR:
                mSurfaceViewRender.getBlurFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_BLUSH:
                mSurfaceViewRender.getRuddyLeftFilter().setScale(data.getBeautyScale());
                mSurfaceViewRender.getRuddyRightFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_EYE_SHADOW:
//                mSurfaceViewRender.get().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_BIG_EYES:
                mSurfaceViewRender.getLeftEyeFilter().setScale(data.getBeautyScale());
                mSurfaceViewRender.getRightEyeFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_SMALL_NOSE:
                mSurfaceViewRender.getNoseFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_SMALL_MOUTH:
                mSurfaceViewRender.getMouthFilter().setScale(data.getBeautyScale());
                break;
            case BeautyParams.BEAUTY_TYPE_THIN_FACE:

                break;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == mCamera1Helper) {
                    mCamera1Helper = new Camera1Helper();
                }

                if (null == mSurfaceViewRender) {
                    mSurfaceViewRender = new SurfaceViewRender();
                }

                if (null == mEGLHelper) {
                    mEGLHelper = new EGLHelper();
                }
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mEGLHelper.initEGL(holder.getSurface());
                mSurfaceViewRender.onSurfaceChanged(mEGLHelper.getGL(), width, height);
                mCamera1Helper.setPreviewCallback(GLCamera1Activity.this);
                mCamera1Helper.openCamera(mSurfaceViewRender.getSurfaceTexture());
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCamera1Helper.closeCamera();
                mSurfaceViewRender.release();
                if (null != mEGLHelper) {
                    mEGLHelper.release();
                }
            }
        });
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (null == mEGLHelper) {
                    return;
                }

                float[] points = assemblePreview(data);
                // 设置美颜相关参数
                mSurfaceViewRender.setFacePoint(points);

                mSurfaceViewRender.onDrawFrame(mEGLHelper.getGL());
                mEGLHelper.swap();
            }
        });
    }

    private float[] assemblePreview(byte[] data) {
        long start = System.currentTimeMillis();
        FaceTracking.getInstance().Update(data, Camera1Helper.PREVIEW_HEIGHT, Camera1Helper.PREVIEW_WIDTH);
//        CCLog.i(" ====用时===== " + (System.currentTimeMillis() - start));

        boolean rotate270 = mCamera1Helper.getCameraInfo().orientation == 270;
        List<Face> faceActions = FaceTracking.getInstance().getTrackingInfo();
        float[] points = null;
        for (Face face : faceActions) {
            points = new float[106 * 2];
            for (int i = 0; i < 106; i++) {
                int x;
                if (rotate270) {
                    x = face.landmarks[i * 2] * Camera1Helper.SCALE_FACTOR;
                } else {
                    x = Camera1Helper.PREVIEW_HEIGHT - face.landmarks[i * 2];
                }
                int y = face.landmarks[i * 2 + 1] * Camera1Helper.SCALE_FACTOR;
                points[i * 2] = revertToGLX(x, Camera1Helper.PREVIEW_HEIGHT);
                points[i * 2 + 1] = revertToGLY(y, Camera1Helper.PREVIEW_WIDTH);
            }
        }

        return points;
    }

    private void initFaceModels() {
        String assetPath = FileUtil.PATH_FACE;
        String modelsPath = FileUtil.PATH_FACE_MODELS;
        FileUtil.copyFilesFromAssets(mContext, assetPath, modelsPath);
        FaceTracking.getInstance().FaceTrackingInit(FileUtil.PATH_FACE_MODELS + "/models", Camera1Helper.PREVIEW_HEIGHT,
                Camera1Helper.PREVIEW_WIDTH);
    }

    private float revertToGLX(int x, int width) {
        return x * 1f / width;
    }

    private float revertToGLY(int y, int height) {
        return y * 1f / height;
    }

}
