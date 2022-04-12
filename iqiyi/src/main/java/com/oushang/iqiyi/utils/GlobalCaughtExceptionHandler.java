package com.oushang.iqiyi.utils;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @Author: zeelang
 * @Description: 全局异常处理
 * @Time: 2022/3/17 22:49
 * @Since: 1.0
 */
public class GlobalCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "OUSHANG_IQIYI";

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler; //系统默认

    private GlobalCaughtExceptionHandler() {}

    private static class GlobalCaughtExceptionHandlerHolder {
        static GlobalCaughtExceptionHandler HOLDER = new GlobalCaughtExceptionHandler();
    }

    public static GlobalCaughtExceptionHandler getInstance() {
        return GlobalCaughtExceptionHandlerHolder.HOLDER;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (!handlerException(e) && mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }

    public void init() {
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();//获取系统默认
        Thread.setDefaultUncaughtExceptionHandler(this); //设置当前类为默认
    }

    private boolean handlerException(Throwable e) {
        if (e == null) {
            return true;
        }
        Log.e(TAG, "Happen Exception:" + Log.getStackTraceString(e));
        return false;
    }
}
