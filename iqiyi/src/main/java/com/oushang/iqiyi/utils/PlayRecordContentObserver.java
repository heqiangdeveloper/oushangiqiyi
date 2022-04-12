package com.oushang.iqiyi.utils;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.oushang.iqiyi.MainApplication;

/**
 * @Author: zeelang
 * @Description: 播放记录监听
 * @Time: 2021/10/27 0027  14:35
 * @Since: 1.0
 */
@Deprecated
public class PlayRecordContentObserver extends ContentObserver {
    private static final String TAG = PlayRecordContentObserver.class.getSimpleName();
    public static final int MSG_CHANGE = 1;

    private Handler mHandler;

    public PlayRecordContentObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(TAG, "onChange:");
        if (mHandler != null)
            mHandler.obtainMessage(MSG_CHANGE).sendToTarget();
    }
}
