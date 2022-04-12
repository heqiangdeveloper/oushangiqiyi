package com.oushang.iqiyi.interceptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.chinatsp.proxy.IVehicleNetworkCallback;
import com.chinatsp.proxy.VehicleNetworkManager;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.api.BalanceInfo;
import com.oushang.iqiyi.api.VehicleService;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.dialog.VehicleFlowDialog;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.net.RetrofitClient;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.SPUtils;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 车联网服务拦截器
 * @Time: 2022/2/22 20:52
 * @Since: 1.0
 */
@Interceptor(priority = 2, name = "VehicleInternetInterceptor")
public class VehicleInternetInterceptor implements IInterceptor {
    private static final String TAG = VehicleInternetInterceptor.class.getSimpleName();
    private static double sBalanceLeft;

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        Log.d(TAG, "VehicleInternetInterceptor:" + path);
        long expirationTime = SPUtils.getShareLong(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, 0L);
        Log.d(TAG, "expirationTime:" + AppUtils.getTime(expirationTime));
        long currentTime = System.currentTimeMillis();
        if (expirationTime <= currentTime) {
            if (path.equals(Constant.PATH_ACTIVITY_DISCLAIMERS)) {
                Log.d(TAG, "disclaimers activity");
                MainApplication.checkBalance(true, new MainApplication.OnBalanceListener() {
                    @Override
                    public void onBalance(long expirationTime) {
                        Log.d(TAG, "onBalance expirationTime:" + AppUtils.getTime(expirationTime));
                        long currentTime = System.currentTimeMillis();
                        if (expirationTime > currentTime) {
                            callback.onContinue(postcard);//继续页面间的跳转
                        } else {
                            HandlerUtils.postDelayOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!VehicleFlowDialog.getInstance(MainApplication.getContext()).isShowing()) {
                                        VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
                                    }
                                }
                            }, 0);
                        }
                    }
                });
            } else {
                MainApplication.checkBalance(true);
                callback.onContinue(postcard);//继续页面间的跳转
            }
        } else {
            callback.onContinue(postcard);//继续页面间的跳转
        }
    }

    @Override
    public void init(Context context) {

    }
}
