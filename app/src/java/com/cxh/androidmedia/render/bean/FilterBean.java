package com.cxh.androidmedia.render.bean;

/**
 * Created by Cxh
 * Time : 2020-06-27  13:01
 * Desc :
 */
public class FilterBean {

    private int mFilterId;
    private String mFilterName;

    public FilterBean(int filterId, String filterName) {
        mFilterId = filterId;
        mFilterName = filterName;
    }

    public int getFilterId() {
        return mFilterId;
    }

    public void setFilterId(int filterId) {
        mFilterId = filterId;
    }

    public String getFilterName() {
        return mFilterName;
    }

    public void setFilterName(String filterName) {
        mFilterName = filterName;
    }
}
