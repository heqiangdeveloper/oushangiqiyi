package com.oushang.iqiyi.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.utils.ThemeContentObserver;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;

/**
 * @Author: zeelang
 * @Description: 视频播放fragment基类
 * @Time: 2021/8/26 16:40
 * @Since: 1.0
 */
public abstract class BasePlayFragment <P extends BasePresenter> extends BaseFragmentMVP<P> implements EventBusHelper.EventListener, ThemeManager.OnThemeChangeListener {
    private static final String TAG = BasePlayFragment.class.getSimpleName();
    private EventBusHelper mEventBusHelper;
    private ThemeContentObserver mThemeContentObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ThemeManager.getInstance().registerThemeChangeListener(this);
        mThemeContentObserver = new ThemeContentObserver(context, new Handler());
        ThemeContentObserver.register(getContext(), mThemeContentObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBusHelper != null) { //有可能未调用onStart,如onCreate之后finish，不会走onStart,此时mEventBusHelper未赋值
            mEventBusHelper.unRegister();
        }
        ThemeContentObserver.unRegister(getContext(), mThemeContentObserver);
        ThemeManager.getInstance().unRegisterThemeChangeListener(this);
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        Log.d(TAG, "onThemeChanged:" + themeMode);
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {

    }
}
