package com.cxh.androidmedia.utils;

/**
 * Created by Cxh
 * Time : 2020-07-14  10:14
 * Desc :
 */
public class YUVUtils {

    /**
     * YUV420p和RGB之间相互转换
     *
     * Y     = 0.257R + 0.504G + 0.098B + 16
     * Cr    = 0.439R – 0.368G – 0.071B + 128
     * Cb    = –0.148R – 0.291G + 0.439B + 128
     *
     * R     = 1.164(Y – 16) + 1.596(Cr – 128)
     * G     = 1.164(Y – 16) – 0.813(Cr – 128) – 0.391(Cb – 128)
     * B     = 1.164(Y – 16) + 2.018(Cb – 128)
     *
     */


}
