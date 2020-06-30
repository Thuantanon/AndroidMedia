package com.cxh.androidmedia.beans;

import com.cxh.androidmedia.base.BaseActivity;

import java.io.Serializable;

/**
 * Created by Cxh
 * Time : 2018/7/13  下午3:00
 * Desc :
 */
public class ActivityBean implements Serializable {

    private String mTitle;
    private Class<? extends BaseActivity> mClass;

    public ActivityBean(String title, Class<? extends BaseActivity> aClass) {
        mTitle = title;
        mClass = aClass;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Class<?> getMClass() {
        return mClass;
    }

    public void setClass(Class<? extends BaseActivity> aClass) {
        mClass = aClass;
    }
}
