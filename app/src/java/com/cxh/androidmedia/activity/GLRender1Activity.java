package com.cxh.androidmedia.activity;

import android.opengl.GLSurfaceView;
import android.widget.ImageView;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.render.BaseGlRender;
import com.cxh.androidmedia.render.shapes.CubeDrawable;
import com.cxh.androidmedia.render.shapes.EarthDrawable;
import com.cxh.androidmedia.render.filters.FilterGrayDrawable;
import com.cxh.androidmedia.render.shapes.ImageDrawable;
import com.cxh.androidmedia.render.shapes.PointDrawable;
import com.cxh.androidmedia.render.shapes.RectDrawable;
import com.cxh.androidmedia.render.shapes.RoundDrawable;
import com.cxh.androidmedia.render.shapes.TriangleDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;

/**
 * Created by Cxh
 * Time : 2019-03-05  23:50
 * Desc :  相关文档，参见Note.java
 */
public class GLRender1Activity extends BaseActivity {

    @BindView(R.id.gl_surface_view)
    GLSurfaceView mSurfaceView;
    @BindView(R.id.tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.iv_image)
    ImageView mIvImage;

    private StandardRender mGlRender;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_gl_render_1;
    }

    @Override
    protected void init() {

        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mGlRender = new StandardRender();
        mSurfaceView.setRenderer(mGlRender);

        // 调用requestRender()时再刷新
//        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        mSurfaceView.requestRender();
        // 自动重绘，60帧
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mTabLayout.addTab(mTabLayout.newTab().setText("绘制点"));
        mTabLayout.addTab(mTabLayout.newTab().setText("三角形"));
        mTabLayout.addTab(mTabLayout.newTab().setText("正方形"));
        mTabLayout.addTab(mTabLayout.newTab().setText("圆形"));
        mTabLayout.addTab(mTabLayout.newTab().setText("正方体"));
        mTabLayout.addTab(mTabLayout.newTab().setText("图片"));
        mTabLayout.addTab(mTabLayout.newTab().setText("地球仪"));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = mTabLayout.getSelectedTabPosition();
                mGlRender.changeShape(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }


    /**
     * 画一个点
     */
    private static class StandardRender extends BaseGlRender {

        private final List<BaseDrawable> shapes = new ArrayList<>();
        private int mCurrentShape;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            super.onSurfaceCreated(gl, config);

            shapes.clear();
            shapes.add(new PointDrawable());
            shapes.add(new TriangleDrawable());
            shapes.add(new RectDrawable());
            shapes.add(new RoundDrawable());
            shapes.add(new CubeDrawable());
            shapes.add(new ImageDrawable());
            shapes.add(new EarthDrawable());
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            super.onSurfaceChanged(gl, width, height);

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            super.onDrawFrame(gl);

            // 画点
            if (mCurrentShape < shapes.size()) {
                shapes.get(mCurrentShape).draw(mProjectionMatrix, mSurfaceWidth, mSurfaceHeight);
            }
        }

        public void changeShape(int shape) {
            mCurrentShape = shape % shapes.size();
        }

    }

}
