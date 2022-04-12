package com.oushang.iqiyi.manager;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.utils.AppUtils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by DELL on 2021-07-26 17:16
 *
 * @description: activity堆栈式管理
 * @Last Modified by  DELL on 2021-07-26 17:16
 */
public class AppManager {
    private static final String TAG = AppManager.class.getSimpleName();

    private volatile static Stack<Activity> activityStack;
    private static Stack<Fragment> fragmentStack;

    private AppManager() {
    }

    private static class AppManagerHolder {
        static AppManager manager = new AppManager();
    }

    public static AppManager getAppManager() {
        return AppManagerHolder.manager;
    }

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    public static Stack<Fragment> getFragmentStack() {
        return fragmentStack;
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.push(activity);
    }

    /**
     * 移除指定的Activity
     */
    public synchronized void removeActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
        }
    }


    /**
     * 是否有activity
     */
    public boolean hasActivity() {
        if (activityStack != null) {
            return !activityStack.isEmpty();
        }
        return false;
    }

    public int getActivityStackSize() {
        return activityStack != null ? activityStack.size() : 0;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null) {
            activity = activityStack.lastElement();
        }
        return activity;
    }

    public boolean hasTargetActivity(Class<?> cls) {
        if (!hasActivity()) {
            return false;
        }

        boolean has = false;
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack != null) {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public synchronized void finishActivity(Activity activity) {
        if (activity != null) {
            Log.d(TAG, "finish activity:" + activity.getClass().getSimpleName());
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public synchronized void finishAllActivity() {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                finishActivity(activity);
            }
            activityStack.clear();
        }
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }


    /**
     * 添加Fragment到堆栈
     */
    public void addFragment(Fragment fragment) {
        if (fragmentStack == null) {
            fragmentStack = new Stack<Fragment>();
        }
        fragmentStack.add(fragment);
    }

    /**
     * 移除指定的Fragment
     */
    public void removeFragment(Fragment fragment) {
        if (fragment != null && fragmentStack != null) {
            fragmentStack.remove(fragment);
        }
    }


    /**
     * 是否有Fragment
     */
    public boolean isFragment() {
        if (fragmentStack != null) {
            return !fragmentStack.isEmpty();
        }
        return false;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Fragment currentFragment() {
        if (fragmentStack != null) {
            return fragmentStack.lastElement();
        }
        return null;
    }


    /**
     * 退出应用程序
     */
    public synchronized void exitApp() {
        finishAllActivity();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        killAppProcess();
        System.exit(0);
    }

    /**
     * 杀进程
     */
    public void killAppProcess() {
        try {
            String pkg = MainApplication.getContext().getPackageName();
            ActivityManager mActivityManager = (ActivityManager) MainApplication.getContext().getSystemService(ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, pkg);  //packageName是需要强制停止的应用程序包名
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exitApp(Context context) {
        try {
            killAppProcess(context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void killAppProcess(Context context) {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
            if (runningAppProcessInfo.processName.contains(AppUtils.getProcessName(context))
                    && !TextUtils.equals(runningAppProcessInfo.processName, AppUtils.getProcessName(context))
            ) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        getAppManager().finishAllActivity();
        // 最后干掉当前主进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
