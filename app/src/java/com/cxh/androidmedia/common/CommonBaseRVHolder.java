package com.cxh.androidmedia.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Cxh
 * Time : 2018/5/10  下午7:33
 * Desc :
 */
public class CommonBaseRVHolder<T> extends RecyclerView.ViewHolder {

    private IAdapterViewItem<T> mItem;
    protected Context mContext;
    private final SparseArray<View> mViewList = new SparseArray<>();

    public CommonBaseRVHolder(Context context, ViewGroup viewGroup, @NonNull IAdapterViewItem<T> item) {
        super(LayoutInflater.from(context).inflate(item.getLayoutRes(), viewGroup, false));
        mContext = context;
        mItem = item;
        mItem.onBindView(this);
    }

    /**
     * 添加一系列方法
     *
     * @return
     */
    public <T extends View> T findViewById(@IdRes int id) {
        View view = mViewList.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            mViewList.put(id, view);
        }
        return (T) view;
    }


    public void setVisibility(@IdRes int id, int visibility) {
        View view = findViewById(id);
        if (null != view) {
            view.setVisibility(visibility);
        }
    }


    public void setText(@IdRes int id, String text) {
        TextView mTextView = (TextView) findViewById(id);
        if (null != mTextView) {
            if (!TextUtils.isEmpty(text)) {
                mTextView.setText(text);
            } else {
                mTextView.setText("");
            }
        }
    }

    public void setImage(@IdRes int id, @DrawableRes int drawable) {
        ImageView imageView = (ImageView) findViewById(id);
        if (null != imageView) {
            imageView.setImageResource(drawable);
        }
    }


    public void loadImage(@IdRes int id, String url, int defaultUri) {
        ImageView imageView = (ImageView) findViewById(id);

        /**
         *  图片加载部分自己根据需要实现
         */
        if (null != imageView) {
//            Glide.with(mContext)
//                    .load(url)
//                    .centerCrop()
//                    .error(defaultUri)
//                    .into(imageView);
        }
    }


    public void loadImage(@IdRes int id, String url) {
        loadImage(id, url, 0);
    }


    public View getRootView() {
        return itemView;
    }

    public Context getContext() {
        return mContext;
    }

    public IAdapterViewItem<T> getItem() {
        return mItem;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        itemView.setOnLongClickListener(longClickListener);
    }
}
