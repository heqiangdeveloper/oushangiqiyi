package com.oushang.iqiyi.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.oushang.iqiyi.manager.AppManager;
import com.oushang.lib_base.log.LogUtils;

/**
 * @Author: zeelang
 * @Description: 赛道模式广播
 * @Time: 2021/11/24 0024  10:31
 * @Since: 1.0
 */
public class TrackReceiver extends BroadcastReceiver {
    private static final String TAG = TrackReceiver.class.getSimpleName();
    public static final String TRACK_PACKAGE_NAME = "com.onstyle.track";
    public static final String ACTION_CLOSE_TRACK = "com.onstyle.track.ACTION_CLOSE_TRACK"; //通知赛道关闭
    public static final String ACTION_OPEN_TRACK = "com.onstyle.track.ACTION_OPEN_TRACK_OUT"; //接受到赛道打开的通知

    private static class TrackReceiverHolder {
        static TrackReceiver HOLDER = new TrackReceiver();
    }

    public static TrackReceiver getInstance() {
        return TrackReceiverHolder.HOLDER;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (ACTION_OPEN_TRACK.equals(action)) {
            Log.d(TAG, "receive open track mode! exit iqiyi app");
            AppManager.getAppManager().exitApp();
        }
    }


    /**
     * 通知关闭赛道模式
     * @param context context
     */
    public static void sendBroadcastCloseTrack(Context context) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setPackage(TRACK_PACKAGE_NAME);
            intent.setAction(ACTION_CLOSE_TRACK);
            context.sendBroadcast(intent);
        }
    }

    public static void registerBroadcast(Context context) {
        if(context != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.onstyle.track.ACTION_OPEN_TRACK_OUT");
            context.registerReceiver(TrackReceiver.getInstance(), intentFilter);
        }
    }
}
