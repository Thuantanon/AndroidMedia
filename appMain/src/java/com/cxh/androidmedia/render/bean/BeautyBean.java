package com.cxh.androidmedia.render.bean;

/**
 * Created by Cxh
 * Time : 2020-05-29  23:55
 * Desc :
 */
public class BeautyBean {

    private String mBeautyName;
    private String mBeautyType;
    private float mBeautyScale;

    public BeautyBean(String beautyName, String beautyType, float beautyScale) {
        mBeautyName = beautyName;
        mBeautyType = beautyType;
        mBeautyScale = beautyScale;
    }

    public String getBeautyType() {
        return mBeautyType;
    }

    public void setBeautyType(String beautyType) {
        mBeautyType = beautyType;
    }

    public float getBeautyScale() {
        return mBeautyScale;
    }

    public void setBeautyScale(float beautyScale) {
        mBeautyScale = beautyScale;
    }

    public String getBeautyName() {
        return mBeautyName;
    }

    public void setBeautyName(String beautyName) {
        mBeautyName = beautyName;
    }
}
