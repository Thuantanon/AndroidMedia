package com.cxh.androidmedia;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cxh.androidmedia.activity.AudioRecordActivity;
import com.cxh.androidmedia.activity.mediacodec.Camera1RecorderActivity;
import com.cxh.androidmedia.activity.mediacodec.Camera2RecorderActivity;
import com.cxh.androidmedia.activity.opengles.FBOProcessActivity;
import com.cxh.androidmedia.activity.opengles.GLCamera1Activity;
import com.cxh.androidmedia.activity.opengles.GLRenderActivity;
import com.cxh.androidmedia.activity.opengles.GLFilterActivity;
import com.cxh.androidmedia.activity.opengles.GLFeatureActivity;
import com.cxh.androidmedia.activity.opengles.GLCamera2Activity;
import com.cxh.androidmedia.activity.mediacodec.MediaCodec1Activity;
import com.cxh.androidmedia.activity.opengles.ShowImageActivity;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.beans.ActivityBean;
import com.cxh.androidmedia.common.CommonPagerAdapter;
import com.cxh.androidmedia.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.main_viewpager)
    ViewPager mViewPager;

    private MultiTypeRvAdapter mAdapter1;
    private MultiTypeRvAdapter mAdapter2;
    private Disposable mDisposable;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initPages();

        requestPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }

    private void requestPermission() {

        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };

        RxPermissions rxPermissions = new RxPermissions(mContext);
        mDisposable = rxPermissions.request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            ToastUtil.show(mContext, "请允许相关权限，否则将不能正常使用app！");
                        }
                    }
                });

    }

    private void initPages() {
        View view1 = LayoutInflater.from(this).inflate(R.layout.view_recylcerview, null);
        RecyclerView recyclerView1 = view1.findViewById(R.id.recyclerview);
        recyclerView1.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter1 = new MultiTypeRvAdapter(mContext);
        recyclerView1.setAdapter(mAdapter1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.view_recylcerview, null);
        RecyclerView recyclerView2 = view2.findViewById(R.id.recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter2 = new MultiTypeRvAdapter(mContext);
        recyclerView2.setAdapter(mAdapter2);

        List<View> pageViews = new ArrayList<>();
        pageViews.add(view1);
        pageViews.add(view2);
        List<String> pageTitles = new ArrayList<>();
        pageTitles.add("OpenGLES图像处理");
        pageTitles.add("Camera、音视频");

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(pageViews, pageTitles);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        addPages();

        mViewPager.setCurrentItem(1);
    }

    private void addPages() {
        List<Object> pages1 = new ArrayList<>();
        pages1.add(new ActivityBean("多种图片显示方式", ShowImageActivity.class));
        pages1.add(new ActivityBean("OpenGL ES基础绘制", GLRenderActivity.class));
        pages1.add(new ActivityBean("OpenGL ES裁剪、旋转、水印、滤镜", GLFilterActivity.class));
        pages1.add(new ActivityBean("OpenGL ES的VBO、ABO、FBO特性", GLFeatureActivity.class));
        pages1.add(new ActivityBean("OpenGL ES的FBO离屏渲染", FBOProcessActivity.class));
        pages1.add(new ActivityBean("OpenGL ES、SurfaceView美颜相机", GLCamera1Activity.class));
        pages1.add(new ActivityBean("OpenGL ES、Camera2美颜相机", GLCamera2Activity.class));
        mAdapter1.setList(pages1);

        List<Object> pages2 = new ArrayList<>();
        pages2.add(new ActivityBean("音频录制、播放，wav、mp3读写", AudioRecordActivity.class));
        pages2.add(new ActivityBean("Camera1拍照、VideoRecorder录像", Camera1RecorderActivity.class));
        pages2.add(new ActivityBean("Camera2拍照、VideoRecorder录像", Camera2RecorderActivity.class));
        pages2.add(new ActivityBean("MediaCodec解析、封装Mp4文件", MediaCodec1Activity.class));
//        pages2.add(new ActivityBean("MediaCodec硬编、硬解AAC和H264", MediaCodec2Activity.class));
//        pages2.add(new ActivityBean("音视频采集、编码、封装Mp4文件", MediaCodec3Activity.class));
//        pages2.add(new ActivityBean("Mp4文件解析、解码、播放、渲染", MediaCodec4Activity.class));
//        pages2.add(new ActivityBean("网络协议rtmp、封包格式FLV、MP4等", MediaProtocolActivity.class));
//        pages2.add(new ActivityBean("学习开源项目ijkplayer", MediaIjkPlayerActivity.class));
//        pages2.add(new ActivityBean("移植ffmpeg，实现简易播放器", MediaFFmpegActivity.class));
//        pages2.add(new ActivityBean("移植x264，实现H264软编码", MediaX264Activity.class));
//        pages2.add(new ActivityBean("移植librtmp，实现rtmp推流功能", MediaLibrtmpActivity.class));
//        pages2.add(new ActivityBean("做一款短视频APP，仿抖音", MediaMainActivity.class));
        mAdapter2.setList(pages2);
    }
}
