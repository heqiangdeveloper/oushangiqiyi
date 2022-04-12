package com.oushang.lib_service.iqiyiweb.interceptors;

import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_base.utils.SPUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    public static final String AUTHORIZATION = "Authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request newRequest = oldRequest;
        String TOKEN = SPUtils.getShareString("register", "token", "");
        if (!TOKEN.isEmpty()) {
            newRequest = oldRequest.newBuilder()
                    .addHeader(AUTHORIZATION, " Bearer " + TOKEN)
                    .build();
        } else {

            LogUtils.e("token is empty, please check need token (register not need token, ignore this log)");
        }
        return chain.proceed(newRequest);
    }
}
