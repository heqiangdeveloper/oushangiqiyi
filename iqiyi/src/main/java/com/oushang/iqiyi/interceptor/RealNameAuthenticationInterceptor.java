package com.oushang.iqiyi.interceptor;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.oushang.iqiyi.MainApplication;
import com.oushang.lib_common_component.authentication.RealNameAuthentication2;

/**
 * @Author: zeelang
 * @Description: 实名认证拦截器,优先级 1
 * @Time: 2022/1/5 19:54
 * @Since: 1.0
 */
@Interceptor(priority = 1, name = "RealNameAuthentication")
public class RealNameAuthenticationInterceptor implements IInterceptor {
    private static final String TAG = RealNameAuthenticationInterceptor.class.getSimpleName();

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        Log.d(TAG, "RealNameAuthenticationInterceptor:" + path);
        if (!RealNameAuthentication2.isAuthenticationed(MainApplication.getContext())) {
            Log.e(TAG, "no authentication");
            RealNameAuthentication2.launchCertifyGuidePage(MainApplication.getContext());
        } else {
            Log.d(TAG, "authentication is success!");
            callback.onContinue(postcard);//继续页面间的跳转
        }
    }

    @Override
    public void init(Context context) {

    }
}
