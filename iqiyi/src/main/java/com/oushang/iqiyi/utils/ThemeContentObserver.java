package com.oushang.iqiyi.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

/**
 * @Author: zeelang
 * @Description: 主题模式（日/夜间模式切换）
 * @Time: 2022/2/14 23:30
 * @Since: 1.0
 */
public class ThemeContentObserver extends ContentObserver {
    private static final String TAG = ThemeContentObserver.class.getSimpleName();
    private static final String SHOW_MODE = "show_mode";
    private final Context mContext;

    public ThemeContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(TAG, "onChange:" + selfChange);
        if(mContext != null) {
            int themeMode = Settings.System.getInt(mContext.getContentResolver(), SHOW_MODE, 0);
            Log.d(TAG, "showMode:" + themeMode);
            ThemeManager.getInstance().changeThemeMode(themeMode);
        }
    }

    public static void register(Context context, ThemeContentObserver contentObserver) {
        if(context != null) {
            context.getContentResolver().registerContentObserver(Settings.System.getUriFor(SHOW_MODE), false,  contentObserver);
        }
    }

    public static void unRegister(Context context, ThemeContentObserver contentObserver) {
        if(context != null) {
            context.getContentResolver().unregisterContentObserver(contentObserver);
        }
    }
}
