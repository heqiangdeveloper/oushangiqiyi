package com.oushang.lib_base.net;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

public class RetrofitConfig {
    public static long DEFAULT_CONNECTTIMEOUT = 10;
    public static long DEFAULT_READTIMEOUT = 10;
    private long connectTimeout = DEFAULT_CONNECTTIMEOUT;
    private long readTimeout = DEFAULT_READTIMEOUT;
    private List<Interceptor> interceptorList = new ArrayList<>();
    private String baseUrl;

    private RetrofitConfig(){}

    private static class RetrofitConfigHolder{
        private static RetrofitConfig HOLDER = new RetrofitConfig();
    }

    public static RetrofitConfig getInstance() {
        return RetrofitConfigHolder.HOLDER;
    }

    public RetrofitConfig withHost(String url) {
        this.baseUrl = url;
        return this;
    }

    public RetrofitConfig withConnectTimeout(long timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    public RetrofitConfig withReadTimeout(long timeout) {
        this.readTimeout = timeout;
        return this;
    }

    public RetrofitConfig addInterceptors(Interceptor... interceptor) {
        for(Interceptor inte: interceptor) {
            addInterceptor(inte);
        }
        return this;
    }

    public RetrofitConfig addInterceptor(Interceptor interceptor) {
        this.interceptorList.add(interceptor);
        return this;
    }

    public String getHost() {
        return baseUrl;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public List<Interceptor> getInterceptorList() {
        return interceptorList;
    }

}
