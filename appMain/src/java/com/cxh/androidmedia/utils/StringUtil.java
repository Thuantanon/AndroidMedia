package com.cxh.androidmedia.utils;

/**
 * Created by Cxh
 * Time : 2018-10-09  11:35
 * Desc :
 */
public class StringUtil {

    public static void logByte(byte[] bytes) {
        if (null == bytes || bytes.length <= 0) {
            CCLog.i("[]");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < bytes.length; i++) {
            sb.append(bytes[i]);
        }
        sb.append("]");
        CCLog.i(sb.toString());
    }

}
