package com.oushang.iqiyi.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.lib_service.constant.VideoNoPlay;
import com.oushang.lib_service.interfaces.PlayManager;


public class WelcomeShowMonitor {
    private static final String TAG = "MCU_EVENT";
    private static final String WELCOME_SHOWING = "WELCOME_SHOWING";

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    public WelcomeShowMonitor() {
        ARouter.getInstance().inject(this);
    }

    private static class WelcomeShowMonitorHolder {
        static WelcomeShowMonitor HOLDER = new WelcomeShowMonitor();
    }

    public static WelcomeShowMonitor getInstance() {
        return WelcomeShowMonitorHolder.HOLDER;
    }

    /**
     * 在 Application#onCreate中初始化
     */
    public static void registerWelcomeShow(Context context) {
        Log.i(TAG, "Welcome show register");

        Uri uri = Settings.Global.getUriFor(WELCOME_SHOWING);
        ContentObserver welcomeShowContentObserver = new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                int welcomeshow = Settings.Global.getInt(context.getContentResolver(), WELCOME_SHOWING, 0);
                Log.i(TAG, "Welcome show = " + welcomeshow);
                boolean off = (welcomeshow == 1);
                // on true:播放, false:暂停
                PlayManager mPlayManager = WelcomeShowMonitor.getInstance().mPlayManager;
                if(!off) {
                    Log.d(TAG, "退出开机欢迎页");
                    VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_BOOT_WELECOME);
                } else {
                    Log.d(TAG, "开机欢迎页");
                    Log.d(TAG, "enter welcome pause");
                    VideoNoPlay.getInstance().noPlay(VideoNoPlay.NO_PLAY_REASON_BOOT_WELECOME);
                    mPlayManager.pause();
                }
            }
        };
        context.getContentResolver().registerContentObserver(uri, false, welcomeShowContentObserver);
    }
}
