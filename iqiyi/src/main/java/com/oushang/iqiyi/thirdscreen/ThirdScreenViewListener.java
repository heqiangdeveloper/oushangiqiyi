package com.oushang.iqiyi.thirdscreen;

import android.util.Log;

import com.chinatsp.threescreenmiddleservice.TSNotifyManager;
import com.oushang.iqiyi.MainApplication;

/**
 * @Author: zeelang
 * @Description: 第三屏显示
 * @Time: 2022/3/29 11:35
 * @Since: 1.0
 */
public class ThirdScreenViewListener implements TSNotifyManager.INotifyViewListener {
    private static final String TAG = ThirdScreenViewListener.class.getSimpleName();

    //前缀
    public static final String MEDIA = "MEDIA";

    //后缀
    public static final String RADIO = "RADIO";
    public static final String MUSIC = "MUSIC";
    public static final String CC = "CC";
    public static final String CB = "CB";

    private static class ThirdScreenViewListenerHolder {
        static ThirdScreenViewListener HOLDER = new ThirdScreenViewListener();
    }

    public static ThirdScreenViewListener getInstance() {
        return ThirdScreenViewListenerHolder.HOLDER;
    }

    private ThirdScreenViewListener() {}

    private boolean isMedia(String view) {
        return view != null && !view.isEmpty() && view.startsWith(MEDIA) &&
                (view.endsWith(RADIO) || view.endsWith(MUSIC) || view.endsWith(CC) || view.endsWith(CB));
    }

    TSNotifyManager.ServiceConnectListener serviceConnectListener = new TSNotifyManager.ServiceConnectListener() {
        @Override
        public void onServiceConnected(boolean connected) {
            Log.d(TAG, "onServiceConnected:" + connected);
            if (connected) {
                String currentView = TSNotifyManager.getInstance().getCurrentView();
                Log.d(TAG, "currentView:" + currentView);
                if (isMedia(currentView)) {
                    TSNotifyManager.getInstance().notifyHideView(currentView);
                }
            }
        }
    };

    @Override
    public void show(String view) {
        Log.d(TAG, "show:" + view);
        if (isMedia(view)) {
            TSNotifyManager.getInstance().notifyHideView(view);
        }
    }

    @Override
    public void hide(String view) {
        Log.d(TAG, "hide:" + view);

    }

    public void register() {
        TSNotifyManager.getInstance().registerNotifyListener(this);
        TSNotifyManager.getInstance().addServiceConnectListener(serviceConnectListener);
        TSNotifyManager.getInstance().bind(MainApplication.getContext());
    }

    public void unRegister() {
        TSNotifyManager.getInstance().unRegisterNotifyListener(this);
        TSNotifyManager.getInstance().removeServiceConnectListener(serviceConnectListener);
        TSNotifyManager.getInstance().unBind();
    }
}
