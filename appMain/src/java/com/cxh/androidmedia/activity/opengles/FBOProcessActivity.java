package com.cxh.androidmedia.activity.opengles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render_new.EGLHelper;
import com.cxh.androidmedia.render_new.FBOImageProcessor;
import com.cxh.androidmedia.utils.CCLog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Cxh
 * Time : 5/23/21  9:21 PM
 * Desc :
 */
public class FBOProcessActivity extends BaseActivity implements FBOImageProcessor.IBitmapCallback {

    private static long mSwapTime = 0L;

    @BindView(R.id.iv_fbo_image)
    ImageView mIvFBOImage;

    private Handler mFBOHandler;
    private FBORenderTask mRenderTask;
    private ImageReader mImageReader;
    private Bitmap mBitmap;
    private int mDrawWidth;
    private int mDrawHeight;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_fbo_process_image;
    }

    @Override
    protected void init() {
        HandlerThread handlerThread = new HandlerThread("FBO_Render_Thread");
        handlerThread.start();
        mFBOHandler = new Handler(handlerThread.getLooper());

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mylove);
        mDrawWidth = mBitmap.getWidth();
        mDrawHeight = mBitmap.getHeight();
        mImageReader = ImageReader.newInstance(mDrawWidth, mDrawHeight, PixelFormat.RGBA_8888, 3);
        final Rect srcRect = new Rect(0, 0, mDrawWidth, mDrawHeight);
        final RectF dstRect = new RectF(0, 0, mDrawWidth, mDrawHeight);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();

                long costTime = System.currentTimeMillis() - mSwapTime;
                CCLog.i("onImageAvailable, costTime: " + costTime + " , imageWidth: " + image.getWidth() + " , imageHeight: " + image.getHeight());

                int width = image.getWidth();
                int height = image.getHeight();
                Image.Plane plane = image.getPlanes()[0];
                int pixelStride = plane.getPixelStride();
                int rowStride = plane.getRowStride();
                int rowPadding = rowStride - pixelStride * width;

                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(plane.getBuffer());
                final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                canvas.drawBitmap(bitmap, srcRect, dstRect, null);
                if (null != mIvFBOImage) {
                    mIvFBOImage.post(new Runnable() {
                        @Override
                        public void run() {
                            mIvFBOImage.setImageBitmap(bmp);
                        }
                    });
                }

                image.close();
            }
        }, mFBOHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mFBOHandler) {
            mFBOHandler.getLooper().quitSafely();
        }

        if (null != mImageReader) {
            mImageReader.close();
        }
    }

    @Override
    @OnClick({R.id.btn_process, R.id.btn_recover})
    public void onViewClick(View view) {
        super.onViewClick(view);

        switch (view.getId()) {
            case R.id.btn_process: {
                if (null == mRenderTask) {
                    mRenderTask = new FBORenderTask(this, mImageReader);
                }
                mFBOHandler.post(mRenderTask);
            }

            break;

            case R.id.btn_recover: {
                mIvFBOImage.setImageResource(R.drawable.mylove);
            }

            break;
        }
    }

    @Override
    public void onBitmapReceived(Bitmap bitmap) {
        if (null == bitmap) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIvFBOImage.setImageBitmap(bitmap);
            }
        });
    }

    private static final class FBORenderTask implements Runnable {
        private FBOImageProcessor.IBitmapCallback mBitmapCallback;
        private ImageReader mImageReader;

        public FBORenderTask(FBOImageProcessor.IBitmapCallback bitmapCallback, ImageReader reader) {
            mBitmapCallback = bitmapCallback;
            mImageReader = reader;
        }

        @Override
        public void run() {
            Bitmap bitmap = BitmapFactory.decodeResource(AMApp.get().getResources(), R.drawable.mylove);

            EGLHelper eglHelper = new EGLHelper();
            eglHelper.initEGL(mImageReader.getSurface());

            // 后台绘制
            FBOImageProcessor processor = new FBOImageProcessor();
            processor.prepareDraw(bitmap);
            // processor.draw(mBitmapCallback, false);
            // 传null使用ImageReader
            processor.draw(null, false);

            eglHelper.swapBuffers();
            mSwapTime = System.currentTimeMillis();

            processor.release();
            // 释放EGL环境
            eglHelper.release();
        }
    }
}
