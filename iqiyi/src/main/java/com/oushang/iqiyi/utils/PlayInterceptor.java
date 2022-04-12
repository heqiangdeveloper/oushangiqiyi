package com.oushang.iqiyi.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_common_component.authentication.RealNameAuthentication2;
import com.oushang.lib_service.MediatorServiceFactory;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/10/18 0018  16:29
 * @Since: 1.0
 */
@Deprecated
//@Interceptor(priority = 1, name = "PlayInterceptor")
public class PlayInterceptor implements IInterceptor {
    private static final String TAG = PlayInterceptor.class.getSimpleName();
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        postcard.setTimeout(2);

//        callback.onContinue(postcard);

        if (!RealNameAuthentication2.isAuthenticationed(MainApplication.getContext())) {
            Log.e(TAG, "no authentication");
            RealNameAuthentication2.launchCertifyGuidePage(MainApplication.getContext());
            return;
        } else {
            Log.d(TAG, "authentication is success!");
        }

        String path = postcard.getPath();
        switch (path) {
            case Constant.PATH_ACTIVITY_PLAYER:
                try {
                    if (!MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()) {
                        Log.d(TAG, "sdk正在初始化中...");
                        HandlerUtils.postDelayOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, "播放器正在初始化，请稍后重试!");
                            }
                        }, 0);
                    } else {
                        Log.d(TAG, "sdk已初始化成功");
                        callback.onContinue(postcard);
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    Log.e(TAG, "error:" + Log.getStackTraceString(e));
                }
                break;
            case Constant.PATH_ACTIVITY_ACCOUNT:
                //判断IqiyiSdk有没有初始化成功，每200ms轮训，直到aBoolean返回true,继续执行页面间的跳转onContinue
                    RxUtils.newInstance().executeUtil(0, 100, new Function<Long, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> apply(@NonNull Long aLong) throws Exception {
                            boolean isInit = false;
                            try {
                                isInit = MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized();
                                Log.d(TAG, "SDK is init:" + isInit);
                            } catch (IllegalAccessException | InstantiationException e) {
                                isInit = false;
                            }
                            return Observable.just(isInit);
                        }
                    }, new Function<Boolean, Boolean>() {
                        @Override
                        public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                            Log.d(TAG, "SDK is init util:" + aBoolean);
                            return aBoolean;
                        }
                    }, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            Log.d(TAG, "SDK is init result:" + aBoolean);
                            if(aBoolean) {
                                callback.onContinue(postcard);
                            }else{
                                HandlerUtils.postDelayOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, "播放器正在初始化，请稍后重试!");
                                    }
                                }, 0);
                            }
                        }
                    });

                break;
            default:
                callback.onContinue(postcard);//继续页面间的跳转
                break;
        }
    }

    @Override
    public void init(Context context) {

    }




}
