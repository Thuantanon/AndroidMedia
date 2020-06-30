package com.cxh.androidmedia.adapter.item;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.IAdapterViewItem;

/**
 * Created by Cxh
 * Time : 2018-09-20  15:45
 * Desc :
 */
public class StringAdapterItem implements IAdapterViewItem<Object> {

    @Override
    public int getLayoutRes() {
        return R.layout.item_string;
    }

    @Override
    public void onBindView(CommonBaseRVHolder holder) {

    }

    @Override
    public void onBindData(CommonBaseRVHolder holder, Object data, int position) {
        holder.setText(R.id.content, data.toString());
    }
}
