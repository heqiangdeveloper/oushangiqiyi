package com.oushang.iqiyi.base;

import android.util.Log;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.utils.RetryWithDelay;
import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.log.LogUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 基础服务presenter,提供执行服务功能
 * @Time: 2021/9/16 18:03
 * @Since: 1.0
 */
public abstract class BaseServicePresenter<V extends IBaseView, M extends IModel> extends BasePresenter<V, M> {
    private CompositeDisposable mCompositeDisposable;
    private Disposable mTimeOutDisposable;

    private void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    private void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    private <T> Observable<T> executeObserver(Observable<T> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(RetryWithDelay.DEFAULT_MAX_RETRY, RetryWithDelay.DEFAULT_MAX_RETRY_DELAY_TIMES))
                .doOnSubscribe(this::addDisposable)
                .onErrorReturn(throwable -> {
                    Log.d("executeObserver", "onError:" + Log.getStackTraceString(throwable));
                    return null;
                })
                .subscribeOn(Schedulers.io());
    }

    private <T> Observable<T> mergeObserver(Observable<T> observable, Observable<T> observable2) {
        return observable
                .mergeWith(observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(RetryWithDelay.DEFAULT_MAX_RETRY, RetryWithDelay.DEFAULT_MAX_RETRY_DELAY_TIMES))
                .subscribeOn(Schedulers.io());
    }


    private void processException(Throwable err) {
        if (isAttach()) {
            Log.e("OUSHANG_IQIYI",Log.getStackTraceString(err));
        }
    }

    protected <T> Disposable execute(Observable<T> observable, @NonNull Consumer<? super T> onSuccess, @NonNull Function<Throwable, Boolean> handleError, @NonNull Function<Boolean, Boolean> handleLoading) {
        return executeObserver(observable)
                .subscribe(value -> {
                    if (isAttach()) onSuccess.accept(value);
                }, (error) -> {
                    if (isAttach() && handleError.apply(error)) return;
                    processException(error);
                }, () -> {
                    if (isAttach() && handleLoading.apply(false)) return;
                    hideLoading();
                });
    }

    protected <T> Disposable executeMerge(Observable<T> observable, Observable<T> observable2, @NonNull Consumer<? super T> onSuccess, @NonNull Function<Throwable, Boolean> handleError, @NonNull Function<Boolean, Boolean> handleLoading) {
        return mergeObserver(observable, observable2)
                .subscribe(value -> {
                    if (isAttach()) onSuccess.accept(value);
                }, (error) -> {
                    if (isAttach() && handleError.apply(error)) return;
                    processException(error);
                }, () -> {
                    if (isAttach() && handleLoading.apply(false)) return;
                    hideLoading();
                });
    }

    public <T> void executeTimeOut(long start, long count, long initialDelay, long period, io.reactivex.functions.Function<Long, Observable<T>> run, io.reactivex.functions.Function<T, Boolean> takeUtil, Consumer<T> result) {
        if(mTimeOutDisposable != null && !mTimeOutDisposable.isDisposed()) {
            mTimeOutDisposable.dispose();
        }
        mTimeOutDisposable = Observable.intervalRange(start, count, initialDelay, period, TimeUnit.MILLISECONDS)
                .flatMap(run)
                .takeUntil(takeUtil::apply)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::addDisposable)
                .subscribe(result);
    }

    @Override
    public void detach() {
        unDisposable();
        super.detach();
    }
}
