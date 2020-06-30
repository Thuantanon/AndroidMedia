package com.cxh.androidmedia.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Cxh
 * Time : 2019-11-05  20:09
 * Desc : 参数转换工具类
 */
public class DimenUtil {

    public static int dp2Px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context
                .getResources().getDisplayMetrics());
    }

    public static int spToPx(Context context, int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context
                .getResources().getDisplayMetrics());
    }
}
