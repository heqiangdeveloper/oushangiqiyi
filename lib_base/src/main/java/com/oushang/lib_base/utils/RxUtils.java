package com.oushang.lib_base.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: rxjava工具类
 * @Description: ***
 * @Time: 2021/11/18 0018  11:11
 * @Since: 1.0
 */
public class RxUtils {
    private static final String TAG = RxUtils.class.getSimpleName();
    private CompositeDisposable mCompositeDisposable;

    public static RxUtils newInstance() {
        return new RxUtils();
    }

    private void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void unDisposable() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    private <T> Observable<T> executeObserver(Observable<T> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(RetryWithDelay.DEFAULT_MAX_RETRY, RetryWithDelay.DEFAULT_MAX_RETRY_DELAY_TIMES))
                .doOnSubscribe(this::addDisposable)
                .subscribeOn(Schedulers.io());
    }

    public <T> void execute(Observable<T> observable, @NonNull Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        Disposable disposable = executeObserver(observable)
                .subscribe(onSuccess, onFailure);
        addDisposable(disposable);
    }

    public void executeDelay(long start, long count, long initialDelay, long period, Consumer<Long> run) {
        Disposable disposable = Observable.intervalRange(start, count, initialDelay, period, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(run);
        addDisposable(disposable);
    }

    public <T> void executeUtil(long initialDelay, long period, Function<Long, Observable<T>> function, Function<T, Boolean> takeUtil, Consumer<T> result) {
        Disposable disposable = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .flatMap(function)
                .takeUntil(takeUtil::apply)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result);
        addDisposable(disposable);

    }


}
