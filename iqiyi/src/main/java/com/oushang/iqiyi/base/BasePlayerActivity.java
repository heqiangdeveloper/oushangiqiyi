package com.oushang.iqiyi.base;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;
import com.oushang.lib_base.net.state.NetworkObserver;
import com.oushang.lib_base.net.state.NetworkStateReceiver;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.interfaces.MyAccountManager;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2021/8/20 18:54
 * @Since: 1.0
 */
public abstract class BasePlayerActivity<P extends BasePresenter> extends BaseActivityMVP<P> implements EventBusHelper.EventListener {
    private static final String TAG = BasePlayerActivity.class.getSimpleName();
    private EventBusHelper mEventBusHelper;

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    protected PlayManager mPlayManager; //播放管理

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    protected PlayListManager mPlayListManager; //播放列表管理

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_ACCOUNT_MANAGER)
    protected MyAccountManager mMyAccountManager;//账号管理

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:" + this);
    }

    @Override
    protected void initView() {
        super.initView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StatusBarUtil.hideStatusBar(this);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        Log.d(TAG, "initListener");
        //播放列表管理服务关联生命周期
        getLifecycle().addObserver(mPlayListManager);
        //播放管理服务关联生命周期
        getLifecycle().addObserver(mPlayManager);
        //注册网络状态监听
        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:" + this);
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        StatusBarUtil.hideStatusBar(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (mEventBusHelper != null) {
            mEventBusHelper.unRegister();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        NetworkStateReceiver.getInstance().unRegisterObserver(networkObserver);//取消网络监听
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToastNew(R.layout.toast_custom_layout, R.id.toast_content, msg);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {

    }

    //网络监听
    NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            Log.d(TAG, "onNetworkChanged:" + isConnected);
            BasePlayerActivity.this.onNetworkChanged(isConnected);
        }
    };

    protected abstract void onNetworkChanged(boolean isConnected);
}
