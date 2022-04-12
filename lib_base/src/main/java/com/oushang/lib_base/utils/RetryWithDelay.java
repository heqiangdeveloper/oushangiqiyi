package com.oushang.lib_base.utils;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @Author: zeelang
 * @Description: 重试机制
 * @Time: 2021/8/20 17:58
 * @Since: 1.0
 */
public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
    private static final String TAG = RetryWithDelay.class.getSimpleName();
    public static final int DEFAULT_MAX_RETRY_DELAY_TIMES = 3; //多久重试一次
    public static final int DEFAULT_MAX_RETRY = 5; //重试几次

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (++retryCount <= maxRetries) {
                Log.d(TAG, "retry:" + retryCount);
                return Observable.timer(retryDelayMillis, TimeUnit.SECONDS);
            }
            return Observable.error(throwable);
        });
    }


}