package com.oushang.lib_base.net;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.oushang.lib_base.net.interceptors.HttpLogInterceptor;
import com.oushang.lib_base.net.listener.HttpEventListener;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @Author: zeelang
 * @Description: Retrofit客户端类
 * @Time: 2021/6/28 18:01
 * @Since: 1.0
 */
public class RetrofitClient {
    private static final String TAG = "IQIYI_HTTP";
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private RetrofitApiService retrofitApiService;
    private WeakHashMap<String, Object> params;
    private String baseUrl;
    private String url;
    private RequestBody body;
    private File file;
    private boolean isRetrofitInit = false;
    private boolean isOkHttpClientInit = false;

    enum HttpMethod {
        GET,
        POST,
        POST_BODY,
        PUT,
        PUT_BODY,
        DELETE,
        UPLOAD
    }

    private RetrofitClient(RetrofitClientBuilder builder) {
        this.params = builder.params;
        this.baseUrl = builder.baseUrl;
        this.url = builder.url;
        this.body = builder.body;
        this.file = builder.file;
        initOkHttpClient();
        initRetrofit();
        initApiService();
    }

    public static RetrofitClientBuilder newBuilder() {
        return new RetrofitClientBuilder();
    }

    private Observable<String> request(HttpMethod method) {
        Observable<String> observable = null;
        switch (method) {
            case GET:
                observable = retrofitApiService.get(url, params);
                break;
            case POST:
                observable = retrofitApiService.post(url, params);
                break;
            case POST_BODY:
                observable = retrofitApiService.postBody(url, body);
                break;
            case PUT:
                observable = retrofitApiService.put(url, params);
                break;
            case PUT_BODY:
                observable = retrofitApiService.putBody(url, body);
                break;
            case DELETE:
                observable = retrofitApiService.delete(url, params);
                break;
            case UPLOAD:
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()), file);
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                observable = retrofitApiService.upload(url, body);
                break;
            default:
                break;
        }
        return observable;
    }

    public Observable<String> get() {
        return request(HttpMethod.GET);
    }

    public final Observable<String> post() {
        if (body == null) {
            return request(HttpMethod.POST);
        } else {
            if (!params.isEmpty()) {
                throw new RuntimeException("params must be null!");
            }
            return request(HttpMethod.POST_BODY);
        }
    }

    public final Observable<String> put() {
        if (body == null) {
            return request(HttpMethod.PUT);
        } else {
            if (!params.isEmpty()) {
                throw new RuntimeException("params must be null!");
            }
            return request(HttpMethod.PUT_BODY);
        }
    }

    public Observable<String> delete() {
        return request(HttpMethod.DELETE);
    }

    public Observable<String> upload() {
        return request(HttpMethod.UPLOAD);
    }

    public Observable<ResponseBody> download() {
        return retrofitApiService.download(url, params);
    }

    /**
     * 初始化OkHttpCLient
     */
    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        long connectTimeout = RetrofitConfig.getInstance().getConnectTimeout();
        long readTimeout = RetrofitConfig.getInstance().getReadTimeout();
        if (connectTimeout <= 0) {
            connectTimeout = RetrofitConfig.DEFAULT_CONNECTTIMEOUT;
        }
        if (readTimeout <= 0) {
            readTimeout = RetrofitConfig.DEFAULT_READTIMEOUT;
        }
        List<Interceptor> interceptorList = RetrofitConfig.getInstance().getInterceptorList();
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
        okHttpClient = builder
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(new HttpLogInterceptor())
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.d(TAG, message)))
                .eventListenerFactory(HttpEventListener.FACTORY)
                .build();
    }

    /**
     * 初始化Retrofi
     */
    private void initRetrofit() {
        if (baseUrl == null) {
            baseUrl = RetrofitConfig.getInstance().getHost();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        isRetrofitInit = true;

    }

    /**
     * 初始化RetrofitApiService
     */
    private void initApiService() {
        retrofitApiService = retrofit.create(RetrofitApiService.class);
    }

    /**
     * 获取api service
     * 用户自定义的api service
     *
     * @param clazz class
     * @param <T>   类型
     * @return class
     */
    public <T> T getApiService(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static final class RetrofitClientBuilder {
        private WeakHashMap<String, Object> params;
        private String baseUrl = null;
        private String url = null;
        private RequestBody body = null;
        private File file = null;

        public RetrofitClientBuilder() {
            params = new WeakHashMap<>();
        }

        public RetrofitClientBuilder params(WeakHashMap<String, Object> params) {
            this.params.putAll(params);
            return this;
        }

        public RetrofitClientBuilder params(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public RetrofitClientBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RetrofitClientBuilder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * upload file
         *
         * @param fileName 文件名
         * @return RetrofitClientBuilder
         */
        public RetrofitClientBuilder file(String fileName) {
            this.file = new File(fileName);
            return this;
        }

        public RetrofitClientBuilder file(File file) {
            this.file = file;
            return this;
        }

        public RetrofitClientBuilder body(RequestBody body) {
            this.body = body;
            return this;
        }

        public RetrofitClient build() {
            return new RetrofitClient(this);
        }
    }
}
