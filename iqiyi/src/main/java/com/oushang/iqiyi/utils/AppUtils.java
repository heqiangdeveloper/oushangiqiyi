package com.oushang.iqiyi.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oushang.iqiyi.MainApplication;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.PUT;

/**
 * @Author: zeelang
 * @Description: app工具类
 * @Time: 2021/7/12 15:45
 * @Since: 1.0
 */
public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    public static final String BTPHONE_ACTIVE = "btphone_active";
    public static final int BTPHONE_NO_CALL = 0, BTPHONE_CALL = 1;
    public static final Uri BTPHONE_ACTIVE_URI = Settings.System.getUriFor(BTPHONE_ACTIVE);

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 1;
        if (context != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }else {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName("com.android.internal.R$dimen");
                    Object object = clazz.newInstance();
                    int height = Integer.parseInt(clazz.getField("status_bar_height")
                            .get(object).toString());
                    statusBarHeight = context.getResources().getDimensionPixelSize(height);

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return statusBarHeight;
    }

    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static int getResId(String variableName, Class<?> clas) {
        try {
            Field idField = clas.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 时间格式
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    public static String parseVideoTime(int second) {
        int minute = second / 60;
        int sec = second % 60;
        return minute + ":" + sec;
    }

    public static String parseSecond(long second) {
        StringBuilder builder = new StringBuilder();
        long hour = second / (3600);
        long minute = second %3600 / 60;
        long sec = second %3600 % 60;
        if (hour > 0) {
            if (hour < 10) {
                builder.append("0").append(hour);
            } else {
                builder.append(hour);
            }
            builder.append(":");
        }
        if (minute < 10) {
            builder.append("0");
        }
        builder.append(minute).append(":");

        if (sec < 10) {
            builder.append("0");
        }
        builder.append(sec);

        return builder.toString();
    }

    public static String parseMills(long mills) {
        StringBuilder time = new StringBuilder();
        long second_unit = 1000;
        long minute_unit = second_unit * 60;
        long hour_unit = minute_unit * 60;

        long hour = mills / hour_unit;
        long minute = mills % hour_unit / minute_unit;
        long second = mills % hour_unit % minute_unit / second_unit;

        if (hour > 0) {
            if (hour < 10) {
                time.append("0").append(hour);
            } else {
                time.append(hour);
            }
            time.append(":");
        }
        if (minute < 10) {
            time.append("0");
        }
        time.append(minute).append(":");

        if (second < 10) {
            time.append("0");
        }
        time.append(second);

        return time.toString();
    }

    public static long getTime(String time) {
        long rTime = 0;
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse(time);
            if(parse != null) {
                rTime = parse.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rTime;
    }

    public static String getTime(long time) {
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public static int getVersionCode(Context context) {
        if (context == null) return 0;
        int code = 0;
        try {
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getVersionName(Context context) {
        if (context == null) return "";
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String appendImageUrl(String url, String append) {
        String newUrl = "";
        if (url != null && !url.isEmpty() && url.contains(".")) {
            int index = url.lastIndexOf(".");
            if (index >= 0) {
                String prefix = url.substring(0, index);
                String suffix = url.substring(index + 1);
                prefix += append;
                newUrl = prefix + "." + suffix;
            } else {
                return url;
            }
        }
        return newUrl;
    }

    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

    /**
     * 获取当前年份
     * @return 当前年份
     */
    public static String getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());
    }


    private static Disposable disposable;

    public static void startPolling(long start, long count, int initialDelay, long period, Consumer<Long> onNext) {
        disposable = Observable.intervalRange(start, count, initialDelay, period, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleLast(count, TimeUnit.SECONDS)
                .subscribe(onNext);
    }

    public static void stopPolling() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
    }

//    public static Bitmap blur(Bitmap bitmap) {
//        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//    }

    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {
        if (backBitmap == null || backBitmap.isRecycled() || frontBitmap == null || frontBitmap.isRecycled()) {
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(0,0,backBitmap.getWidth(),backBitmap.getHeight());
        Rect frontRect = new Rect(0,0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    private static final String KEY_VIN = "VIN";

    public static String getDeviceID(Context context) {
        String deviceId = Settings.System.getString(context.getContentResolver(), KEY_VIN);
        if (TextUtils.isEmpty(deviceId)) {
            return IqiyiApi.SN;
        }
        return deviceId;
    }

    public static boolean hasServiceRunning(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(10);
            Log.d(TAG, "runningServices：" + runningServices);
            return runningServices.size() > 0;
        }
        return false;
    }

    @RequiresApi(api =  Build.VERSION_CODES.LOLLIPOP)
    public static String getCurrentActivity(Context context) {
        String activityName = "";
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityName = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        }
        return activityName;
    }

    @RequiresApi(api =  Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasActivityRunning(Context context, String activity) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(40);
            Log.d(TAG, "runningTasks:" + runningTasks);
            if (runningTasks != null && !runningTasks.isEmpty()) {
                for (ActivityManager.RunningTaskInfo runningTaskInfo: runningTasks) {
                    String className = runningTaskInfo.baseActivity.getClassName();
                    Log.d(TAG, "className:" + className);
                    if (activity.equals(className)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasActivity(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            Log.d(TAG, "appTasks:" + appTasks);
            return appTasks != null && !appTasks.isEmpty();
        }
        return false;
    }

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */

    public static String getProcessName(Context context) {
        return getProcessName(context, android.os.Process.myPid());
    }

    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == pid) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    //获取前台的包名
    public static String getTopPackage(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获取正在运行的task列表，其中1表示最近运行的task，通过该数字限制列表中task数目，最近运行的靠前
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

            if (runningTaskInfos != null && runningTaskInfos.size() != 0) {
                return (runningTaskInfos.get(0).baseActivity).getPackageName();
            }
        } catch (Exception e) {
            Log.d(TAG, "栈顶应用:" + e);
        }
        return "";
    }

    /**
     * 是否在通话中
     * @return 通话中 返回true, 不在通话中
     */
    public static boolean isPhoneCalling() {
        int active = Settings.System.getInt(MainApplication.getContext().getContentResolver(), BTPHONE_ACTIVE, 0);
        Log.d(TAG, "isPhoneCalling:" + active);
        return active == BTPHONE_CALL;
    }

    public static String getSystemProperty(String key) {
        String prop = "";
        try {
            ClassLoader classLoader = MainApplication.getContext().getClassLoader();
            @SuppressLint("PrivateApi")
            Class systemProperties = classLoader.loadClass("android.os.SystemProperties");
            Method get = systemProperties.getDeclaredMethod("get", String.class);
            get.setAccessible(true);
            prop = (String) get.invoke(systemProperties, key);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, "exception:" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return prop;
    }


}
