package com.cxh.androidmedia;

import android.Manifest;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxh.androidmedia.activity.AudioRecordActivity;
import com.cxh.androidmedia.activity.GLRender1Activity;
import com.cxh.androidmedia.activity.GLRender2Activity;
import com.cxh.androidmedia.activity.GLRender3Activity;
import com.cxh.androidmedia.activity.GLRender4Activity;
import com.cxh.androidmedia.activity.GLRender5Activity;
import com.cxh.androidmedia.activity.MediaCodec1Activity;
import com.cxh.androidmedia.activity.MediaCodec2Activity;
import com.cxh.androidmedia.activity.MediaCodec3Activity;
import com.cxh.androidmedia.activity.MediaCodec4Activity;
import com.cxh.androidmedia.activity.MediaFFmpegActivity;
import com.cxh.androidmedia.activity.MediaIjkPlayerActivity;
import com.cxh.androidmedia.activity.MediaLibrtmpActivity;
import com.cxh.androidmedia.activity.MediaMainActivity;
import com.cxh.androidmedia.activity.MediaProtocolActivity;
import com.cxh.androidmedia.activity.MediaX264Activity;
import com.cxh.androidmedia.activity.ShowImageActivity;
import com.cxh.androidmedia.activity.VideoRecorderActivity;
import com.cxh.androidmedia.adapter.MultiTypeRvAdapter;
import com.cxh.androidmedia.base.BaseActivity;
import com.cxh.androidmedia.beans.ActivityBean;
import com.cxh.androidmedia.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private MultiTypeRvAdapter mRvAdapter;
    private Disposable mDisposable;


    @Override
    protected int getLayoutRes() {
        return R.layout.view_recylcerview;
    }

    @Override
    protected void init() {
        mRvAdapter = new MultiTypeRvAdapter(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mRvAdapter);

        List<Object> beans = new ArrayList<>();
        beans.add(new ActivityBean("多种图片显示方式", ShowImageActivity.class));
        beans.add(new ActivityBean("OpenGL ES基础绘制", GLRender1Activity.class));
        beans.add(new ActivityBean("OpenGL ES裁剪、旋转、水印、滤镜", GLRender2Activity.class));
        beans.add(new ActivityBean("OpenGL ES、OpenCV实现高级美颜", GLRender3Activity.class));
        beans.add(new ActivityBean("OpenGL ES的VBO、ABO、FBO高级特性", GLRender4Activity.class));
        beans.add(new ActivityBean("音频录制，播放，wav读写", AudioRecordActivity.class));
        beans.add(new ActivityBean("视频预览，获取NV21数据", VideoRecorderActivity.class));
        beans.add(new ActivityBean("使用SurfaceView预览Camera", GLRender5Activity.class));
        beans.add(new ActivityBean("MediaCodec解析、封装Mp4文件", MediaCodec1Activity.class));
        beans.add(new ActivityBean("MediaCodec硬编、硬解AAC和H264", MediaCodec2Activity.class));
        beans.add(new ActivityBean("音视频采集、编码、封装Mp4文件", MediaCodec3Activity.class));
        beans.add(new ActivityBean("Mp4文件解析、解码、播放、渲染", MediaCodec4Activity.class));
        beans.add(new ActivityBean("网络协议rtmp、封包格式FLV、MP4等", MediaProtocolActivity.class));
        beans.add(new ActivityBean("学习开源项目ijkplayer", MediaIjkPlayerActivity.class));
        beans.add(new ActivityBean("移植ffmpeg，实现简易播放器", MediaFFmpegActivity.class));
        beans.add(new ActivityBean("移植x264，实现H264软编码", MediaX264Activity.class));
        beans.add(new ActivityBean("移植librtmp，实现rtmp推流功能", MediaLibrtmpActivity.class));
        beans.add(new ActivityBean("做一款短视频APP，仿抖音", MediaMainActivity.class));

        mRvAdapter.setList(beans);

        requestPermission();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mDisposable){
            mDisposable.dispose();
        }
    }
}
