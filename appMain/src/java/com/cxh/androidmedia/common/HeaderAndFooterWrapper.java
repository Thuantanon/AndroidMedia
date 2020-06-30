package com.cxh.androidmedia.common;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by Cxh
 * Time : 2018/5/10  下午7:26
 * Desc : 在原有的adapter外面包裹一层，Thanks HongyangAndroid
 */
public class HeaderAndFooterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 10000000;
    private static final int BASE_ITEM_TYPE_FOOTER = 20000000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private RecyclerView.Adapter mContentAdapter;


    public HeaderAndFooterWrapper(@NonNull  RecyclerView.Adapter contentAdapter) {
        mContentAdapter = contentAdapter;
    }

    private RecyclerView.ViewHolder getViewHolder(View root){
        return new RecyclerView.ViewHolder(root) {

        };
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        if(null != mHeaderViews.get(viewType)){
            holder = getViewHolder(mHeaderViews.get(viewType));
        }else if(null != mFooterViews.get(viewType)){
            holder = getViewHolder(mFooterViews.get(viewType));
        }else{
            holder = mContentAdapter.onCreateViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(isHeaderPos(position)){
            return;
        }
        if(isFooterPos(position)){
            return;
        }
        mContentAdapter.onBindViewHolder(holder, getContentPostion(position));
    }


    @Override
    public int getItemViewType(int position) {
        if(isHeaderPos(position)){
            return mHeaderViews.keyAt(position);
        }else if(isFooterPos(position)){
            return mFooterViews.keyAt(position - getHeadersCount() - getContentCount());
        }
        return mContentAdapter.getItemViewType(getContentPostion(position));
    }


    @Override
    public int getItemCount() {
        return getContentCount() + getHeadersCount() + getFootersCount();
    }


    /**
     *  这个方法主要是防止当设置RecyclerView的布局方式为GridLayoutManager的时候
     *  Header和Footer被当作item进行布局
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        mContentAdapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            // 设置每一行的列数
            // 这个时候如果位置是Header或者Footer，就设置为1
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if(null != mHeaderViews.get(viewType)){
                        return gridLayoutManager.getSpanCount();
                    }else if(null != mFooterViews.get(viewType)){
                        return gridLayoutManager.getSpanCount();
                    }

                    if(null != spanSizeLookup){
                        return spanSizeLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }


    /**
     *  这个方法主要是为了解决另一个问题。StaggeredGridLayoutManager的适配问题
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        mContentAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if(isHeaderPos(position) || isFooterPos(position)){
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if(null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams){
                StaggeredGridLayoutManager.LayoutParams sp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sp.setFullSpan(true);
            }
        }
    }

    public void addHeaderView(View view){
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFooterView(View view){
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    private boolean isHeaderPos(int position){
        return position < getHeadersCount();
    }

    private boolean isFooterPos(int position){
        return position >= getHeadersCount() + getContentCount();
    }

    private int getContentPostion(int position){
        return position - getHeadersCount();
    }

    private int getContentCount(){
        return mContentAdapter.getItemCount();
    }

    private int getHeadersCount(){
        return mHeaderViews.size();
    }

    private int getFootersCount(){
        return mFooterViews.size();
    }

}
