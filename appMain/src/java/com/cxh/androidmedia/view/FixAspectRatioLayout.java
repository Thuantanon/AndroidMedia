package com.cxh.androidmedia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Cxh
 * Time : 2020-09-17  21:09
 * Desc :
 */
public class FixAspectRatioLayout extends ViewGroup {

    private int mAspectRatioWidth = 480;
    private int mAspectRatioHeight = 640;

    public FixAspectRatioLayout(@NonNull Context context) {
        super(context);
    }

    public FixAspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixAspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewCount = getChildCount();
        for (int i = 0; i < viewCount; i++) {
            getChildAt(i).layout(l, t, r, b);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = width * mAspectRatioHeight / mAspectRatioWidth;
        setMeasuredDimension(width, height);
    }
}
