package com.cxh.androidmedia.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.ToastUtil;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Cxh
 * Time : 2018/7/13  上午11:30
 * Desc :
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    protected BaseActivity mContext;
    protected MyHander mHander;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentExtra(getIntent());
        mContext = this;
        mHander = new MyHander(this);
        int layout = getLayoutRes();
        if (layout > 0) {
            setContentView(getLayoutRes());
            mUnbinder = ButterKnife.bind(this);
        }
        init();
        initActionBar();
    }

    protected static class MyHander extends Handler {

        private WeakReference<BaseActivity> mActivityRef;

        public MyHander(BaseActivity baseActivity) {
            mActivityRef = new WeakReference<>(baseActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = mActivityRef.get();
            if (null == activity) {
                return;
            }

            activity.handleMessage(msg);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent inte = getIntent();
        if (null != inte) {
            getIntentExtra(inte);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
        if (null != mHander) {
            mHander.removeCallbacksAndMessages(null);
            mHander = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract @LayoutRes
    int getLayoutRes();

    protected abstract void init();

    public void getIntentExtra(Intent intent) {
        String title = getIntent().getStringExtra("title");
        setTitle(title);
    }


    public void onViewClick(View view) {

    }

    public Handler getHandler() {
        return mHander;
    }

    protected void handleMessage(Message message) {

    }

    protected void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if (null != mActionBar) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * 状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                getResources().getDisplayMetrics());
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setStatusHeight(View view) {
        int status_height = getStatusBarHeight();
        if (status_height > 0 && null != view) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = status_height;
            view.setLayoutParams(params);
        }
    }

    public void setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            if (null != window) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }


    public void errorFinish(Throwable t, String message) {
        if (null != t) {
            t.printStackTrace();
            message += " , " + t.toString();
        }
        ToastUtil.show(mContext, message);
        CCLog.i(message);
    }

    public void showProgressDialog(String message) {
        closeProgressDialog();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void closeProgressDialog() {
        if(null != mProgressDialog) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
