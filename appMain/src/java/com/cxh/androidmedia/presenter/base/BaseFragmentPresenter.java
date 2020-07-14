package com.cxh.androidmedia.presenter.base;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by Cxh
 * Time : 2020-07-10  10:53
 * Desc :
 */
public abstract class BaseFragmentPresenter<T extends Fragment> extends IPresenter<T> {

    private Activity mActivity;

    public BaseFragmentPresenter(@NonNull T target) {
        super(target);
    }

    @NonNull
    public Context getContext() {
        return mActivity;
    }

    public void onAttach(Activity activity) {
        mActivity = activity;
    }

    public void onDetach() {
        mActivity = null;
    }
}
