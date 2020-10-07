package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.adapter.FilterListRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.common.CommonBaseRVHolder;
import com.cxh.androidmedia.common.CommonBaseRvAdapter;
import com.cxh.androidmedia.render_old.BaseFboDrawable;
import com.cxh.androidmedia.render_old.IDrawableProviders;
import com.cxh.androidmedia.render_old.PipelineDrawRender;
import com.cxh.androidmedia.render_old.bean.FilterBean;
import com.cxh.androidmedia.render_old.beauty.FBOFeatureDrawable;
import com.cxh.androidmedia.render_old.beauty.VAOFeatureDrawable;
import com.cxh.androidmedia.render_old.beauty.VBOFeatureDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2020-06-01  19:24
 * Desc : VBO、VAO、PBO、RBO、FBO等高级特性，离屏渲染
 */
public class GLFeatureActivity extends BaseActivity implements IDrawableProviders {

    @BindView(R.id.gl_surfaceview)
    GLSurfaceView mSurfaceView;
    @BindView(R.id.rv_render_list)
    RecyclerView mRvRenderList;

    private PipelineDrawRender mDrawRender;
    private FilterListRvAdapter mAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_4;
    }

    @Override
    protected void init() {

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setEGLContextClientVersion(2);
        mDrawRender = new PipelineDrawRender(this);
        mSurfaceView.setRenderer(mDrawRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mAdapter = new FilterListRvAdapter(mContext);
        mRvRenderList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mRvRenderList.setAdapter(mAdapter);

        List<FilterBean> mFilters = new ArrayList<>();
        mFilters.add(new FilterBean(0, "VBO绘制"));
        mFilters.add(new FilterBean(1, "VAO绘制"));
        mFilters.add(new FilterBean(2, "FBO绘制"));
        mAdapter.setList(mFilters);

        mAdapter.setOnItemClickListener(new CommonBaseRvAdapter.OnItemClickListener<FilterBean>() {
            @Override
            public void onItemClick(CommonBaseRVHolder holder, FilterBean data, int position) {
                mAdapter.setCurrentFilter(data.getFilterId());
                if(null != mDrawRender){
                    mDrawRender.setCurrentDrawableIndex(position);
                }
            }
        });
    }

    @Override
    public List<BaseFboDrawable> getDrawables(int w, int h) {
        List<BaseFboDrawable> drawables = new ArrayList<>();
        drawables.add(new VBOFeatureDrawable());
        drawables.add(new VAOFeatureDrawable());
        drawables.add(new FBOFeatureDrawable(w, h));
        return drawables;
    }

}
