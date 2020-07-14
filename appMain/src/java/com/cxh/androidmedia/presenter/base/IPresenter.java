package com.cxh.androidmedia.presenter.base;

import androidx.annotation.NonNull;

/**
 * Created by Cxh
 * Time : 2020-07-10  10:44
 * Desc :
 */
public abstract class IPresenter<T> {

    protected T mTarget;

    public IPresenter(@NonNull T target) {
        mTarget = target;
    }

    public T getTarget() {
        return mTarget;
    }

    public abstract void onCreate();

    public abstract void onStart();

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onDestroy();

}
