package com.cxh.androidmedia.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.cxh.androidmedia.R;

/**
 * Created by Cxh
 * Time : 2018-09-23  09:52
 * Desc :  Show a Bitmap
 */
public class BitmapView extends View {

    private Drawable mDrawable;

    public BitmapView(Context context) {
        this(context, null);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BitmapView);
        mDrawable = ta.getDrawable(R.styleable.BitmapView_bv_drawable);
        if(null != mDrawable){
            setBackground(mDrawable);
        }
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制Bitmap
        // canvas.drawBitmap(....);
    }

}
