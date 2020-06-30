package com.cxh.androidmedia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cxh
 * Time : 2019-02-28  18:11
 * Desc :
 */
public class FlowLayout extends ViewGroup {


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = width - getPaddingStart() - getPaddingEnd();

        int needWidth = 0;
        int needHeight = 0;
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == childCount - 1) {
                    needWidth = Math.max(currentLineWidth, width);
                    needHeight += currentLineHeight;
                }
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
            int childHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

            // 换行
            if (currentLineWidth + childWidth > realWidth) {
                needWidth = Math.max(currentLineWidth, needWidth);
                currentLineWidth = childWidth;
                needHeight += currentLineHeight;
                currentLineHeight = childHeight;
            } else {
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(childHeight, currentLineHeight);
            }
            // 最后一行
            if (i == childCount - 1) {
                needWidth = Math.max(currentLineWidth, width);
                needHeight += currentLineHeight;
            }
        }

        needWidth = widthMode == MeasureSpec.EXACTLY ? width : needWidth + getPaddingStart() + getPaddingEnd();
        needHeight = heightMode == MeasureSpec.EXACTLY ? height : needHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(needWidth, needHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int left = getPaddingStart();
        int top = getPaddingTop();
        int realWidth = width - left - top;

        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
            int childHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

            // 换行
            if (left + childWidth > realWidth) {
                left = getPaddingStart();
                top += lineHeight;
                lineHeight = childHeight;
            }

            // layout child
            int cl = left + mlp.leftMargin;
            int ct = top + mlp.topMargin;
            int cr = cl + child.getMeasuredWidth();
            int cb = ct + child.getMeasuredHeight();
            child.layout(cl, ct, cr, cb);

            left += childWidth;
            lineHeight = Math.max(childHeight, lineHeight);
        }
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
