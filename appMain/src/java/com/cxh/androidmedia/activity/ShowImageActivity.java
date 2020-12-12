package com.cxh.androidmedia.activity;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;

/**
 * Created by Cxh
 * Time : 2018-09-20  16:08
 * Desc : 各种方式显示图片
 */
public class ShowImageActivity extends BaseActivity {

    /**
     * 图像常见格式说明：
     *
     * YUV格式：
     * Y亮度，UV颜色度，YUV444、YUV422、YUV420
     * YUV444：每一个Y对应一组UV
     * YUV422：每两个Y对应一组UV
     * YUV420：每四个Y对应一组UV
     *
     * YUV420：（Android采集格式，这里不讨论其他格式）
     *
     * YUV420p的存储顺序：
     * YU12（I420）：YYYY、YYYY、UUUU、VVVV
     * YV12：YYYY、YYYY、VVVV、UUUU
     *
     * YUV420sp的存储顺序：
     * NV21：YYYY、YYYY、VUVU、VUVU
     * NV12：YYYY、YYYY、UVUV、UVUV
     *
     * YUV和RGB转换：
     * Y      =  (0.257 * R) + (0.504 * G) + (0.098 * B) + 16
     * Cr = V =  (0.439 * R) - (0.368 * G) - (0.071 * B) + 128
     * Cb = U = -(0.148 * R) - (0.291 * G) + (0.439 * B) + 128
     *
     * B = 1.164(Y - 16) + 2.018(U - 128)
     * G = 1.164(Y - 16) - 0.813(V - 128) - 0.391(U - 128)
     * R = 1.164(Y - 16) + 1.596(V - 128)
     *
     */

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_image;
    }

    @Override
    protected void init() {


    }
}
