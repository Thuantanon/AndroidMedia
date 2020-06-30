package com.cxh.androidmedia.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cxh
 * Time : 2018/5/18  下午3:12
 * Desc : ListView，GridView的Adapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter implements ICommonAdapter<T> {

    private final List<T> mData = new ArrayList<>();

    protected Context mContext;
    protected LayoutInflater mInflater;

    public CommonAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < mData.size()){
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setList(List<T> datas) {
        if (null != datas && datas.size() > 0) {
            mData.clear();
            addList(datas);
        }
    }

    @Override
    public void addList(List<T> datas) {
        if (null != datas && datas.size() > 0) {
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(int position) {
        if (position < mData.size()) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<T> getList() {
        return mData;
    }

    /**
     * create by cxh
     * time : 2018/5/18 下午3:17
     * desc :
     */
    public void addItem(int index, T item) {
        mData.add(index, item);
        notifyDataSetChanged();
    }
}
