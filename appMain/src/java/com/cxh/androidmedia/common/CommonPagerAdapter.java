package com.cxh.androidmedia.common;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by Cxh
 * Time : 2018/5/10  下午7:04
 * Desc :
 */
public class CommonPagerAdapter extends PagerAdapter {

    private List<View> mPageViews;
    private List<String> titles;

    public CommonPagerAdapter(List<View> pageViews) {
        this(pageViews, null);
    }

    public CommonPagerAdapter(List<View> pageViews, List<String> titles) {
        mPageViews = pageViews;
        this.titles = titles;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mPageViews.get(position);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_UNCHANGED;
    }

    @Override
    public int getCount() {
        if(null == mPageViews){
            return 0;
        }
        return mPageViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    /** create by cxh
     *  time : 2018/5/10 下午7:13
     *  desc : 配合Tab使用
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(null != titles && titles.size() > position){
            return titles.get(position);
        }
        return "";
    }
}
