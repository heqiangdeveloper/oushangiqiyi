package com.oushang.iqiyi.interceptor;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.dialog.LoadingDialog;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.MediatorServiceFactory;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

/**
 * @Author: zeelang
 * @Description: sdk初始化拦截器，优先级 2
 * @Time: 2022/1/5 20:03
 * @Since: 1.0
 */
@Deprecated
//@Interceptor(priority = 2, name = "SdkInit")
public class SdkInitInterceptor implements IInterceptor {
    private static final String TAG = SdkInitInterceptor.class.getSimpleName();
    private Context mContext;
    private LoadingDialog mLoadingDialog;

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        Log.d(TAG, "SdkInitInterceptor:" + path);
        switch (path) {
            case Constant.PATH_ACTIVITY_PLAYER: //播放activity
            case Constant.PATH_ACTIVITY_ACCOUNT: //账号activity
                routeActivity(postcard, callback);
                break;
            default:
                callback.onContinue(postcard);//继续页面间的跳转
                break;
        }
    }

    @Override
    public void init(Context context) {
        Log.d(TAG, "init");
        this.mContext = context;
//        this.mLoadingDialog = new LoadingDialog(mContext);
    }

    private void routeActivity(Postcard postcard,InterceptorCallback callback) {
        Log.d(TAG, "routeActivity");
        try {
            if(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()) {
                Log.d(TAG, "sdk init is success");
                callback.onContinue(postcard); //继续页面间的跳转
            } else {
                Log.d(TAG, "sdk init is failure");
                HandlerUtils.postDelayOnMainThread(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content,"sdk尚未在初始化中！请稍等...");
                        mLoadingDialog = new LoadingDialog(mContext);
                        mLoadingDialog.show();
                    }
                }, 0);
                checkSdkInit(postcard, callback);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void checkSdkInit(Postcard postcard,InterceptorCallback callback) {
        RxUtils.newInstance().executeTimeOut(0, 15, 0, 500, aLong -> {
            Log.d(TAG, "long:" + aLong);
            boolean isInit = false;
            try {
                isInit = MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized();
                Log.d(TAG, "SDK is init:" + isInit);
            } catch (IllegalAccessException | InstantiationException e) {
                isInit = false;
            }
            return Observable.just(isInit); //是否被始化完成
        }, isInit -> isInit, isInit -> {
            Log.d(TAG, "sdk is init success:" + isInit);
            if (isInit) { //如果已初始化完成
                callback.onContinue(postcard); //继续页面间的跳转
                if(mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
