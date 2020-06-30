package com.cxh.androidmedia.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.common.IAdapterViewItem;
import com.cxh.androidmedia.render.bean.FilterBean;

/**
 * Created by Cxh
 * Time : 2020-05-27  12:55
 * Desc :
 */
public class FilterListRvAdapter extends CommonBaseRvAdapter<FilterBean> {

    private int mCurrentFilter;

    public int getCurrentFilter() {
        return mCurrentFilter;
    }

    public void setCurrentFilter(int currentFilter) {
        mCurrentFilter = currentFilter;
        notifyDataSetChanged();
    }

    public FilterListRvAdapter(Context context) {
        super(context);
    }

    @Override
    protected IAdapterViewItem<FilterBean> getAdaperItem(int position) {

        return new IAdapterViewItem<FilterBean>() {

            @Override
            public int getLayoutRes() {
                return R.layout.item_recycler_filter_checkbtn;
            }

            @Override
            public void onBindView(CommonBaseRVHolder<FilterBean> holder) {

            }

            @Override
            public void onBindData(CommonBaseRVHolder<FilterBean> holder, FilterBean data, int position) {
                TextView textView = holder.findViewById(R.id.btn_filtername);
                textView.setText(data.getFilterName());
                if(mCurrentFilter == data.getFilterId()) {
                    textView.setTextColor(holder.getColor(R.color.colorWhite));
                    textView.setBackgroundResource(R.drawable.shape_radio_button_sel);
                }else {
                    textView.setTextColor(holder.getColor(R.color.colorBlackC));
                    textView.setBackgroundResource(R.drawable.shape_radio_button_unsel);
                }
            }
        };
    }
}
