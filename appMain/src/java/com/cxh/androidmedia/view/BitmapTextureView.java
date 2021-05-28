package com.cxh.androidmedia.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.core.content.ContextCompat;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.utils.CCLog;

/**
 * Created by Cxh
 * Time : 2018-09-23  10:56
 * Desc :
 */
public class BitmapTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Bitmap mBitmapImage;

    private boolean mThreadRunning;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public BitmapTextureView(Context context) {
        this(context, null);
    }

    public BitmapTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        CCLog.i(" --- onSurfaceTextureAvailable");
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        drawContent();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        CCLog.i(" --- onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CCLog.i(" --- onSurfaceTextureDestroyed");
        mThreadRunning = false;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        CCLog.i(" --- onSurfaceTextureUpdated");
    }


    private Bitmap createBitmap(Bitmap source, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, null, new RectF(0, 0, width, height), null);
        return bitmap;
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
        // CCLog.i("drawCanvas ....");
    }


    private void drawContent(){

        Canvas mCanvas = null;
        long startTime = System.currentTimeMillis();

        try {
            mCanvas = lockCanvas();
            clearColor(mCanvas);
            drawCanvas(mCanvas);
        } catch (Exception e) {
            e.printStackTrace();
            CCLog.i(e.toString());
        } finally {
            unlockCanvasAndPost(mCanvas);
        }

        long endTime = System.currentTimeMillis();

        // CCLog.i(" spend time : " + (endTime - startTime));
    }

}
