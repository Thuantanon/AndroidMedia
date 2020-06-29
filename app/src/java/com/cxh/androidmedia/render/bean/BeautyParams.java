package com.cxh.androidmedia.render.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cxh
 * Time : 2020-05-29  23:27
 * Desc :
 */
public class BeautyParams {

    public static final String BEAUTY_TYPE_RESET = "reset";
    // 基础美颜，无需人脸检测，范围0f - 1f
    // 美白
    public static final String BEAUTY_TYPE_WHITE = "beauty_type_white";
    // 磨皮
    public static final String BEAUTY_TYPE_BLUR = "beauty_type_blur";

    // 高级美颜，需要人脸检测，范围0f - 1f
    // 大眼
    public static final String BEAUTY_TYPE_BIG_EYES = "beauty_type_big_eyes";
    // 瘦脸
    public static final String BEAUTY_TYPE_THIN_FACE = "beauty_type_thin_face";
    // 小嘴
    public static final String BEAUTY_TYPE_SMALL_MOUTH = "beauty_type_small_mouth";
    // 缩鼻子
    public static final String BEAUTY_TYPE_SMALL_NOSE = "beauty_type_small_nose";
    // 脸红
    public static final String BEAUTY_TYPE_BLUSH = "beauty_type_blush";

    // 贴纸


    public static Map<String, BeautyBean> BEAUTY_MAP;

    static {
        BEAUTY_MAP = new LinkedHashMap<String, BeautyBean>() {
            {
                put(BEAUTY_TYPE_WHITE, new BeautyBean("美白", BEAUTY_TYPE_WHITE, 0));
                put(BEAUTY_TYPE_BLUR, new BeautyBean("磨皮", BEAUTY_TYPE_BLUR, 0));
                put(BEAUTY_TYPE_BIG_EYES, new BeautyBean("大眼", BEAUTY_TYPE_BIG_EYES, 0));
                put(BEAUTY_TYPE_THIN_FACE, new BeautyBean("瘦脸", BEAUTY_TYPE_THIN_FACE, 0));
                put(BEAUTY_TYPE_SMALL_MOUTH, new BeautyBean("小嘴", BEAUTY_TYPE_SMALL_MOUTH, 0));
                put(BEAUTY_TYPE_SMALL_NOSE, new BeautyBean("小鼻子", BEAUTY_TYPE_SMALL_NOSE, 0));
                put(BEAUTY_TYPE_BLUSH, new BeautyBean("腮红", BEAUTY_TYPE_BLUSH, 0));
                put(BEAUTY_TYPE_RESET, new BeautyBean("重置", BEAUTY_TYPE_RESET, 0));
            }
        };
    }

    public static float getParams(String key) {
        if (!TextUtils.isEmpty(key) && BEAUTY_MAP.containsKey(key)) {
            BeautyBean paramBean = BEAUTY_MAP.get(key);
            if (null != paramBean) {
                return paramBean.getBeautyScale();
            }
        }
        return 0f;
    }

    public static void setParams(String key, float f) {
        if (!TextUtils.isEmpty(key) && BEAUTY_MAP.containsKey(key)) {
            BeautyBean paramBean = BEAUTY_MAP.get(key);
            if (null != paramBean) {
                paramBean.setBeautyScale(f);
            }
        }
    }

    public static void reset() {
        for (BeautyBean beautyBean : BEAUTY_MAP.values()) {
            beautyBean.setBeautyScale(0);
        }
    }

    public static List<BeautyBean> getBeautyBeans() {
        return new ArrayList<>(BEAUTY_MAP.values());
    }

}
