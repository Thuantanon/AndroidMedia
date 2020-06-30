package com.cxh.androidmedia.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Cxh
 * Time : 2018-09-23  17:07
 * Desc :
 */
public class ToastUtil {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
