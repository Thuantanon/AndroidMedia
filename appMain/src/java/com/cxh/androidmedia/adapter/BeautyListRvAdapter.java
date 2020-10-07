package com.cxh.androidmedia.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.common.IAdapterViewItem;
import com.cxh.androidmedia.render_old.bean.BeautyBean;

/**
 * Created by Cxh
 * Time : 2020-05-27  12:55
 * Desc :
 */
public class BeautyListRvAdapter extends CommonBaseRvAdapter<BeautyBean> {

    private int mCurrentBeauty;

    public BeautyListRvAdapter(Context context) {
        super(context);
    }

    public int getCurrentBeauty() {
        return mCurrentBeauty;
    }

    public void setCurrentBeauty(int currentBeauty) {
        mCurrentBeauty = currentBeauty;
        notifyDataSetChanged();
    }

    public String getCurrentKey(){
        return getList().get(mCurrentBeauty).getBeautyType();
    }

    public String getCurrentName(){
        return getList().get(mCurrentBeauty).getBeautyName();
    }

    @Override
    protected IAdapterViewItem<BeautyBean> getAdaperItem(int position) {

        return new IAdapterViewItem<BeautyBean>() {

            @Override
            public int getLayoutRes() {
                return R.layout.item_recycler_filter_checkbtn;
            }

            @Override
            public void onBindView(CommonBaseRVHolder<BeautyBean> holder) {

            }

            @Override
            public void onBindData(CommonBaseRVHolder<BeautyBean> holder, BeautyBean data, int position) {
                TextView textView = holder.findViewById(R.id.btn_filtername);
                textView.setText(data.getBeautyName());
                if(mCurrentBeauty == position) {
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
