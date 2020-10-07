package com.cxh.androidmedia.presenter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.activity.GLCamera2Activity;
import com.cxh.androidmedia.presenter.base.BaseActivityPresenter;
import com.cxh.androidmedia.render_new.Camera2Helper;

/**
 * Created by Cxh
 * Time : 2020-07-10  10:44
 * Desc :
 */
public class Camera2PreviewPresenter extends BaseActivityPresenter<GLCamera2Activity> implements Handler.Callback {

    private Camera2Helper mCameraHelper;
    private HandlerThread mHandlerThread;
    private Handler mMainHandler;
    private Handler mHandler;

    public Camera2PreviewPresenter(@NonNull GLCamera2Activity target) {
        super(target);
        mMainHandler = target.getHandler();
    }

    @Override
    public void onCreate() {

        mHandlerThread = new HandlerThread("CameraThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mCameraHelper = new Camera2Helper(getTarget(), mHandler);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        // 子线程中处理camera调用

        return true;
    }

    @Override
    public void handleMainMessage(Message message) {
        // 主线程

    }

    @Override
    public void onDestroy() {


    }
}
