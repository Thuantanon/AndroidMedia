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

    public static <T> String toString(T[] arr) {
        if (null == arr || arr.length <= 0) {
            return "null";
        }

        StringBuilder sb = new StringBuilder()
                .append("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i].toString());
            if (i != arr.length - 1) {
                sb.append(", ");
            }
        }

        return sb.append("]").toString();
    }
}
