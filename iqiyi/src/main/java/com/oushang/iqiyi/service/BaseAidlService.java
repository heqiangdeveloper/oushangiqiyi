package com.oushang.iqiyi.service;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.manager.AppManager;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.RxUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: aidl服务基础类
 * @Time: 2021/11/11 0011  18:07
 * @Since: 1.0
 */
public abstract class BaseAidlService extends Service {
    private static final String TAG = BaseAidlService.class.getSimpleName();
    protected RxUtils mRxUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        ARouter.getInstance().inject(this);
        mRxUtils = RxUtils.newInstance();
        mRxUtils.addDisposable(Observable.intervalRange(1, 30, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> Log.d(TAG, "long:" + aLong), throwable -> Log.d(TAG, Log.getStackTraceString(throwable)),() -> {
                    int count = MainApplication.getCount();
                    Log.d(TAG, "count:" + count);
                    if(count == 0) {
                        Log.d(TAG, "退出进程");
                        stopSelf();
                        AppManager.getAppManager().exitApp();
                    }
                } ));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;//防止重启
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(mRxUtils != null) {
            mRxUtils.unDisposable();
        }
        int count = MainApplication.getCount();
        boolean hasServiceRunning = AppUtils.hasServiceRunning(this.getApplicationContext());
        Log.d(TAG, "onDestroy：" + count + ",hasRunning service:" + hasServiceRunning);
        if (count == 0 && !hasServiceRunning) { // 已没有activity在前台和没有服务在运行
            Log.d(TAG, "退出进程");
            AppManager.getAppManager().exitApp();//退出进程，该触发方法是桌面小部件unbindservice后调用
        }
    }

}
