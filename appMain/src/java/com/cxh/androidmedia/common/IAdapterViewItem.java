package com.cxh.androidmedia.common;

import androidx.annotation.LayoutRes;

/**
 * Created by Cxh
 * Time : 2018/5/11  上午10:35
 * Desc :
 */
public interface IAdapterViewItem<T> {

    @LayoutRes int getLayoutRes();

    void onBindView(CommonBaseRVHolder<T> holder);

    void onBindData(CommonBaseRVHolder<T> holder, T data, int position);

}
