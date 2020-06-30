package com.cxh.androidmedia.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.utils.DimenUtil;

/**
 * Created by Cxh
 * Time : 2020-06-18  00:24
 * Desc :
 */
public class ShotcutFrameLayout extends FrameLayout {

    private Paint mPaint;

    private boolean mOpenShotcut;
    private float mPressX;
    private float mPressY;
    private float mTouchX;
    private float mTouchY;
    private RectF mRectShot;

    public ShotcutFrameLayout(Context context) {
        this(context, null);
    }

    public ShotcutFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShotcutFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.colorBlue));
        mPaint.setStrokeWidth(DimenUtil.dp2Px(context, 3));
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOpenShotcut) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mPressX = event.getX();
                    mPressY = event.getY();
                }
                return true;
                case MotionEvent.ACTION_MOVE: {
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    invalidateView();
                }
                return true;
                case MotionEvent.ACTION_UP: {
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    invalidateView();
                    updateShotState();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPressX < 0 || mPressY < 0 || mTouchX < 0 || mTouchY < 0 || mPressX == mTouchX || mPressY == mTouchY) {
            return;
        }

        if (!mOpenShotcut) {
            return;
        }

        canvas.drawRect(mPressX, mPressY, mTouchX, mTouchY, mPaint);
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    private void updateShotState() {
        mRectShot = new RectF();
        if (mPressX < 0 || mPressY < 0 || mTouchX < 0 || mTouchY < 0 || mPressX == mTouchX || mPressY == mTouchY) {
            return;
        }

        mRectShot.left = Math.min(mPressX, mTouchX);
        mRectShot.top = Math.min(mPressY, mTouchY);
        mRectShot.right = Math.max(mPressX, mTouchX);
        mRectShot.bottom = Math.max(mPressY, mTouchY);
    }

    public void setOpenShotcut(boolean openShotcut) {
        mRectShot = null;
        mOpenShotcut = openShotcut;
        mPressX = 0;
        mPressY = 0;
        mTouchX = getMeasuredWidth();
        mTouchY = getMeasuredHeight();
        invalidateView();
    }

    public boolean isShotcutEnable() {
        if(null != mRectShot && (mRectShot.left == mRectShot.right || mRectShot.top == mRectShot.bottom)){
            return false;
        }
        return true;
    }

    public RectF getShotcutRectF() {
        return mRectShot;
    }
}
