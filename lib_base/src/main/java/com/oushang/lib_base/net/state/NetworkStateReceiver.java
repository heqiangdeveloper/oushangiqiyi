package com.oushang.lib_base.net.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.oushang.lib_base.env.LibraryRuntimeEnv;
import com.oushang.lib_base.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 网络状态广播
 * @Time: 2021/7/12 17:52
 * @Since: 1.0
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStateReceiver.class.getSimpleName();
    private static final String NETWORK_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private List<NetworkObserver> mNetworkObservers;
    private boolean isRegister = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (NETWORK_CHANGE_ACTION.equalsIgnoreCase(action)) {
            Log.d(TAG, "connectivity change");
            if (NetworkUtils.isNetworkAvailable()) {
                notifyObserver(true);
            } else {
                Log.e(TAG, "网络不可用");
                notifyObserver(false);
            }
        }
    }

    private void notifyObserver(boolean isConnected) {
        if (mNetworkObservers != null) {
            for (NetworkObserver observer : mNetworkObservers) {
                observer.onNetworkChanged(isConnected);
            }
        }
    }

    private NetworkStateReceiver() {
    }

    private static class NetWorkStateReceierHolder {
        private static NetworkStateReceiver HOLDER = new NetworkStateReceiver();
    }

    public static NetworkStateReceiver getInstance() {
        return NetWorkStateReceierHolder.HOLDER;
    }

    public void registerReceiver() {
        Log.d(TAG, "registerReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE_ACTION);
        LibraryRuntimeEnv.get().getContext().registerReceiver(NetworkStateReceiver.getInstance(), filter);
        isRegister = true;
    }

    public void unRegisterReceiver() {
        Log.d(TAG, "unRegisterReceiver");
        if (isRegister) {
            LibraryRuntimeEnv.get().getContext().unregisterReceiver(NetworkStateReceiver.getInstance());
            isRegister = false;
        }
    }

    public void registerObserver(NetworkObserver observer) {
        Log.d(TAG, "registerObserver");
        if (mNetworkObservers == null) {
            mNetworkObservers = new ArrayList<>();
        }
        mNetworkObservers.add(observer);
    }

    public void unRegisterObserver(NetworkObserver observer) {
        Log.d(TAG, "unRegisterObserver");
        if (mNetworkObservers != null && mNetworkObservers.contains(observer)) {
            mNetworkObservers.remove(observer);
        }
    }
}
