package com.cxh.androidmedia.common;

import java.util.List;

/**
 * Created by Cxh
 * Time : 2018/5/11  上午10:16
 * Desc :
 */
public interface ICommonAdapter<T> {

    void setList(List<T> datas);

    void addList(List<T> datas);

    void remove(int position);

    void removeAll();

    List<T> getList();
}
