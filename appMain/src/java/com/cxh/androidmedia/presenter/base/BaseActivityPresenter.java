package com.cxh.androidmedia.presenter.base;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by Cxh
 * Time : 2020-07-10  11:00
 * Desc :
 */
public abstract class BaseActivityPresenter<T extends Activity> extends IPresenter<T> {

    public BaseActivityPresenter(@NonNull T target) {
        super(target);
    }
}
