package com.cxh.androidmedia.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.utils.CCLog;


/**
 * Created by Cxh
 * Time : 2018-09-23  10:03
 * Desc :
 */
public class BitmapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private RenderThread mRenderThread;
    private Bitmap mBitmapImage;

    private boolean mThreadRunning;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public BitmapSurfaceView(Context context) {
        this(context, null);
    }

    public BitmapSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mBitmapImage) {
            mBitmapImage.recycle();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // CCLog.i("surfaceCreated ....");
        mThreadRunning = true;
        mRenderThread = new RenderThread();
        mRenderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // CCLog.i("surfaceDestroyed ....");
        mThreadRunning = false;
        mRenderThread.interrupt();
    }

    public class RenderThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (mThreadRunning) {
                Canvas mCanvas = null;
                long startTime = System.currentTimeMillis();

                try {
                    mCanvas = mSurfaceHolder.lockCanvas();
                    clearColor(mCanvas);
                    drawCanvas(mCanvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != mCanvas) {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                }

                long endTime = System.currentTimeMillis();

                // CCLog.i(" spend time : " + (endTime - startTime));
            }
        }
    }

    private void clearColor(Canvas canvas) {
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
    }

    private void drawCanvas(Canvas canvas) {
        if (null == mBitmapImage) {
            Bitmap sourceImage = BitmapFactory.decodeResource(getResources(), R.drawable.mylove);
            mBitmapImage = createBitmap(sourceImage, mSurfaceWidth, mSurfaceHeight);
        }
        canvas.drawBitmap(mBitmapImage, 0, 0, null);
    }

    private Bitmap createBitmap(Bitmap source, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, null, new RectF(0, 0, width, height), null);
        return bitmap;
    }

}
