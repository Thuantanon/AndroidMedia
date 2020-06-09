package com.cxh.androidmedia.common;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Cxh
 * Time : 2018/5/10  下午7:14
 * Desc :
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mTitles;

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        this(fm, fragments, null);
    }

    @Override
    public Fragment getItem(int position) {
        if(null != mFragments && mFragments.size() > position) {
            return mFragments.get(position);
        }
        return null;
    }



    @Override
    public int getCount() {
        if(null != mFragments){
            return mFragments.size();
        }
        return 0;
    }

    /** create by cxh
     *  time : 2018/5/10 下午7:13
     *  desc : 配合Tab使用
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(null != mTitles && mTitles.size() > position){
            return mTitles.get(position);
        }
        return "";
    }
}
