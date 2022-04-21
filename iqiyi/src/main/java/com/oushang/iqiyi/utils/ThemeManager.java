package com.oushang.iqiyi.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.oushang.iqiyi.MainApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 日间夜间切换模式
 * @Time: 2021/7/13 13:57
 * @Since: 1.0
 */
public class ThemeManager {
    private static final String TAG = ThemeManager.class.getSimpleName();
    private static final String SHOW_MODE = "show_mode";

    //默认模式
    public static volatile ThemeMode sThemeMode = Settings.System.getInt(MainApplication.getContext().getContentResolver(), SHOW_MODE, 0) == 1 ? ThemeMode.DAY : ThemeMode.NIGHT;


    //日间模式资源后缀
    private static final String RESOURCE_SUFFIX = "_notnight";
    //模式切换监听
    private final List<OnThemeChangeListener> mThemeChangerListenerList = new ArrayList<>();
    //日间模式资源缓存 <资源类型, <资源名称, 资源id>>
    private static HashMap<String, HashMap<String, Integer>> sNotNightThemeCache = new HashMap<>();

    private ThemeManager() {
    }

    private static class ThemeManagerHolder {
        static ThemeManager HOLDER = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.HOLDER;
    }

    /**
     * 主题模式
     */
    public enum ThemeMode {
        DAY, NIGHT
    }

    /**
     * 切换日间/夜间模式
     *
     * @param themeMode 主题模式
     */
    public void changeThemeMode(int themeMode) {
        if(themeMode == 1) {
            sThemeMode = ThemeMode.DAY;
        } else {
            sThemeMode = ThemeMode.NIGHT;
        }
        for (OnThemeChangeListener themeChangeListener : mThemeChangerListenerList) {
            themeChangeListener.onThemeChanged(sThemeMode);
        }
    }

    /**
     * 获取主题模式
     *
     * @return 主题模式
     */
    public static ThemeMode getThemeMode() {
        return Settings.System.getInt(MainApplication.getContext().getContentResolver(), SHOW_MODE, 0) == 1 ? ThemeMode.DAY : ThemeMode.NIGHT;
    }

    /**
     * 注册主题模式监听
     *
     * @param themeChangeListener 主题模式监听
     */
    public void registerThemeChangeListener(OnThemeChangeListener themeChangeListener) {
        if (themeChangeListener != null
                && mThemeChangerListenerList.stream().noneMatch(onThemeChangeListener -> onThemeChangeListener == themeChangeListener)) {
            mThemeChangerListenerList.add(themeChangeListener);
        }
    }

    /**
     * 取消注册主题模式监听
     *
     * @param themeChangeListener 主题模式监听
     */
    public void unRegisterThemeChangeListener(OnThemeChangeListener themeChangeListener) {
        if(themeChangeListener != null) {
            mThemeChangerListenerList.removeIf(onThemeChangeListener -> onThemeChangeListener == themeChangeListener);
        }
    }

    /**
     * 获取当前主题下的资源
     *
     * @param context context
     * @param resId   资源id
     * @return 资源id
     */
    public static int getThemeResource(Context context, int resId) {
        Log.d(TAG, "getThemeReource:" + resId);
        if (context == null) {
            throw new IllegalArgumentException("context must be not null!");
        }

        //资源名称
        String resName = context.getResources().getResourceEntryName(resId);
        //资源类型
        String resType = context.getResources().getResourceTypeName(resId);
        Log.d(TAG, "resName:" + resName + ",resType:" + resType);

        //判断资源名称是否是日间模式资源
        if (resName.endsWith(RESOURCE_SUFFIX)) {
            throw new IllegalArgumentException("resource id end with " + RESOURCE_SUFFIX);
        }

        //如果是夜间模式
        if (sThemeMode == ThemeMode.NIGHT) {
            Log.d(TAG, "night theme, return " + resId);
            return resId;
        }
        Log.d(TAG, "day theme");
        //日间资源名称
        String notnightResName = resName + RESOURCE_SUFFIX;
        Log.d(TAG, "notNightResName:" + notnightResName);
        return context.getResources().getIdentifier(notnightResName, resType, context.getPackageName());

//        //从缓存中获取
//        HashMap<String, Integer> cacheRes = sNotNightThemeCache.get(resType);
//
//        if (cacheRes == null) {
//            cacheRes = new HashMap<>();
//        }
//        //获取资源id
//        Integer rId = cacheRes.get(notnightResName);
//
//        if (rId != null && rId != 0) {
//            Log.d(TAG, "cache id:" + rId);
//            return rId;
//        } else {
//            int reId = context.getResources().getIdentifier(notnightResName, resType, context.getPackageName());
//            cacheRes.put(notnightResName, reId);
//            sNotNightThemeCache.put(resType, cacheRes);
//            Log.d(TAG, "reId:" + reId);
//            return reId;
//        }
    }




    /**
     * 主题模式切换监听
     */
    public interface OnThemeChangeListener {

        /**
         * 主题模式回调
         *
         * @param themeMode 主题模式
         */
        void onThemeChanged(ThemeMode themeMode);
    }
}
