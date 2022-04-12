package com.oushang.iqiyi.ui;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkNotNull;

import com.youth.banner.Banner;

import io.reactivex.Observable;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2022/1/11 10:52
 * @Since:
 */
public final class RxCustomView {
    private static void checkNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }

    public static Observable<Object> clickBanner(Banner banner) {
        checkNotNull(banner, "view == null");
        return new BannerOnBannerObservable(banner);
    }
}
