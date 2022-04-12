package com.oushang.lib_base.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * @Author: zeelang
 * @Description: handler工具类
 * @Time: 2021/6/28 18:01
 * @Since: 1.0
 */
public class HandlerUtils {
    private static HandlerThread mHandlerThread;
    private static Handler mHandler;
    private static WeakReference<Handler> mHandlerWR;

    static {
        mHandlerThread = new HandlerThread("HandlerUtils");
        mHandlerThread.setDaemon(true);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandlerWR = new WeakReference<>(mHandler);
    }

    /**
     * 执行Runnable(子线程)
     * @param runnable Runnable
     * @return true or false
     */
    public static boolean post(Runnable runnable) {
        if (mHandler != null) {
            return mHandler.post(runnable);
        }
        return false;
    }

    /**
     * 延迟执行
     * @param runnable Runnable
     * @param delayMillis 延迟时间
     * @return true or false
     */
    public static boolean postDelay(Runnable runnable, long delayMillis) {
        if (mHandler != null) {
            return mHandler.postDelayed(runnable,delayMillis);
        }
        return false;
    }

    /**
     * 获取子线程handler
     * @return handler
     */
    public static Handler getHandler() {
        Handler handler = null;
        if (mHandlerWR != null) {
            handler =  mHandlerWR.get();
            if (handler == null) {
                handler = mHandler;
            }
        }
        return handler;
    }

    public static boolean postDelayOnMainThread(Runnable runnable, long delayMillis) {
        Handler handler = new Handler(Looper.getMainLooper());
        return handler.postDelayed(runnable, delayMillis);
    }

}
