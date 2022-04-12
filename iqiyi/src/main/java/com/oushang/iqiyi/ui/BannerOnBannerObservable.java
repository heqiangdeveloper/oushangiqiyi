package com.oushang.iqiyi.ui;

import android.os.Looper;
import android.util.Log;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2022/1/11 10:38
 * @Since:
 */
public class BannerOnBannerObservable extends Observable<Object> {
    private static final String TAG = BannerOnBannerObservable.class.getSimpleName();
    private Banner banner;

    BannerOnBannerObservable(Banner banner) {
        this.banner = banner;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer) {
        Log.d(TAG, "subscribeActual");
        if (!checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(banner, observer);
        observer.onSubscribe(listener);
        banner.setOnBannerListener(listener);
    }

    static final class Listener extends MainThreadDisposable implements OnBannerListener {
        private Banner banner;
        private Observer<? super Object> observer;

        Listener(Banner banner, Observer<? super Object> observer) {
            this.banner = banner;
            this.observer = observer;
        }

        @Override
        public void OnBannerClick(Object data, int position) {
            if(!isDisposed()) {
                observer.onNext(data);
            }
        }

        @Override
        protected void onDispose() {
            banner.setOnBannerListener(null);
        }
    }

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onError(new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName()));
            return false;
        }
        return true;
    }
}
