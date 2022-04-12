package com.oushang.lib_base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.oushang.lib_base.env.LibraryRuntimeEnv;

/**
 * @Author: zeelang
 * @Description: 网络工具类
 * @Time: 2021/6/28 10:49
 * @Since: 1.0
 */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    /**
     * 网络是否连接和可用
     * @return true of false 是否有活动的网络连接
     */
    public static boolean isNetworkAvailable() {
        //获取连接活动管理器
        ConnectivityManager connMgr = (ConnectivityManager) LibraryRuntimeEnv.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取链接网络信息
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(TAG, "isNetworkAvailable networkInfo:" + (networkInfo == null? "null": networkInfo.toString()));
        return (networkInfo != null && (networkInfo.isAvailable() || networkInfo.isConnected()));
    }

    /**
     * wifi是否连接
     * @return true or false
     */
    public static boolean isWifiConnected() {
        ConnectivityManager manager = (ConnectivityManager) LibraryRuntimeEnv.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 1;
    }

    /**
     * 数据网络是否连接
     * @return 是或否
     */
    public static boolean isMobileConnected() {
        ConnectivityManager manager = (ConnectivityManager) LibraryRuntimeEnv.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取网络类型
     * @return 网络类型
     */
    public static int getConnectedType() {
        ConnectivityManager manager = (ConnectivityManager) LibraryRuntimeEnv.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static String getNetworkInfo() {
        ConnectivityManager manager = (ConnectivityManager) LibraryRuntimeEnv.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null ? networkInfo.toString() : "";
    }
}
