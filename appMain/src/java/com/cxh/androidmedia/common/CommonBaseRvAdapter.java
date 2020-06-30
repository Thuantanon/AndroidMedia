package com.cxh.androidmedia.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cxh
 * Time : 2018/5/10  下午8:16
 * Desc :
 */
public abstract class CommonBaseRvAdapter<T> extends RecyclerView.Adapter<CommonBaseRVHolder<T>> implements
        ICommonAdapter<T> {


    private final List<T> mData = new ArrayList<>();
    private OnItemLongClickListener<T> mOnItemLongClickListener;
    private OnItemClickListener<T> mOnItemClickListener;
    // 修改可见性以便在子类中访问
    protected Context mContext;

    public CommonBaseRvAdapter(Context context) {
        mContext = context;
    }

    protected abstract IAdapterViewItem<T> getAdaperItem(int position);


    @NonNull
    @Override
    public CommonBaseRVHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommonBaseRVHolder<T>(mContext, parent, getAdaperItem(viewType));
    }

    @Override
    public void onBindViewHolder(final @NonNull CommonBaseRVHolder<T> holder, final int position) {
        final T data = mData.get(position);
        if (null != mOnItemClickListener) {
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder, data, position);
                }
            });
        }

        if (null != mOnItemLongClickListener) {
            holder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnItemLongClickListener.onItemLongClick(holder, data, position);
                }
            });
        }

        holder.getItem().onBindData(holder, data, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    // onCreateViewHolder中的viewType就等于position
    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void setList(List<T> datas) {
        mData.clear();
        mData.addAll(datas);
        notifyDataSetChanged();
    }


    @Override
    public void addList(List<T> datas) {
        if (null != datas) {
            mData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void insertToHeader(T data) {
        if (null != data) {
            mData.add(0, data);
            notifyDataSetChanged();
        }
    }

    @Override
    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        if (position < mData.size()) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public List<T> getList() {
        return mData;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener<T> {
        void onItemClick(CommonBaseRVHolder holder, T data, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * view has been clicked and held.
     */
    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(CommonBaseRVHolder holder, T data, int position);
    }
}
