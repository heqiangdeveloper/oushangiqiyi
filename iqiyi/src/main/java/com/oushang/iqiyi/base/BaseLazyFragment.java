package com.oushang.iqiyi.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.net.state.NetworkObserver;
import com.oushang.lib_base.net.state.NetworkStateReceiver;
import com.oushang.lib_base.utils.NetworkUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.voicebridge.VisibleDataProcessor;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 懒加载Fragment基类
 * @Time: 2021/8/3 17:36
 * @Since: 1.0
 */
public abstract class BaseLazyFragment<P extends BasePresenter> extends BaseFragmentMVP<P> implements EventBusHelper.EventListener {
    private static final String TAG = BaseLazyFragment.class.getSimpleName();
    private EventBusHelper mEventBusHelper;

    private boolean isLazyLoaded = false;

    public abstract void lazyInit();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        NetworkStateReceiver.getInstance().registerObserver(networkObserver); //监听网络状态
    }

    //网络状态监听
    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            onNetWorkChanged(isConnected);
        }
    };

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory");
        Glide2Utils.clearMemory(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!isLazyLoaded) {
            Log.e(TAG, "lazyInit");
            lazyInit();
            isLazyLoaded = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLazyLoaded = false;
    }

    @Override
    public void onDestroy() {
        NetworkStateReceiver.getInstance().unRegisterObserver(networkObserver); //取消网络监听
        if (mEventBusHelper != null) {
            mEventBusHelper.unRegister();
        }
        super.onDestroy();
    }

    public void loadFragmentTransaction(@IdRes int containerViewId, FragmentManager manager, Fragment fragment, boolean isAddBackStack) {
        if (manager != null && fragment != null) {
            List<Fragment> fragmentList = manager.getFragments();
            FragmentTransaction transaction = manager.beginTransaction();
            if (fragmentList.isEmpty()) {
                transaction
                        .add(containerViewId, fragment, fragment.getClass().getName());
            } else if (!fragmentList.contains(fragment) && manager.findFragmentByTag(fragment.getTag()) == null) {
                for (Fragment fg : fragmentList) {
                    if (fg.isAdded() && !fg.isHidden()) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);//不走onResume生命周期，也就是不显示
                    }
                }
                transaction.add(containerViewId, fragment, fragment.getClass().getName());
            } else {
                Log.d(TAG, "fragment list has exist:" + fragment.toString());
                for (Fragment fg : fragmentList) {
                    if (!fg.getTag().equals(fragment.getTag())) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);
                    }
                }
            }
            transaction.show(fragment);
            if (isAddBackStack) {
                transaction.addToBackStack(fragment.getTag());
            }
//            if (fragment.isAdded()) {
            transaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
//            }
            transaction.commit();

        }
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, msg);
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {

    }

    public void onNetWorkChanged(boolean isConnected) {

    }
}
