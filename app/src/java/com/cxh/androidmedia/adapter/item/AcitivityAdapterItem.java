package com.cxh.androidmedia.adapter.item;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.beans.ActivityBean;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.IAdapterViewItem;

/**
 * Created by Cxh
 * Time : 2018-09-20  15:45
 * Desc :
 */
public class AcitivityAdapterItem implements IAdapterViewItem<Object> {

    private Context mContext;

    public AcitivityAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_string;
    }

    @Override
    public void onBindView(CommonBaseRVHolder holder) {

    }

    @Override
    public void onBindData(CommonBaseRVHolder holder, Object data, int position) {
        final ActivityBean activityBean = (ActivityBean) data;
        holder.setText(R.id.content, activityBean.getTitle());
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, activityBean.getMClass());
                intent.putExtra("title", activityBean.getTitle());
                mContext.startActivity(intent);
            }
        });
    }
}
