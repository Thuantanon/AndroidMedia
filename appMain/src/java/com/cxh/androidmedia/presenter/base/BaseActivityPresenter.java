package com.cxh.androidmedia.presenter.base;

import android.app.Activity;
import android.os.Message;

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

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    public void handleMainMessage(Message message){

    }
}
