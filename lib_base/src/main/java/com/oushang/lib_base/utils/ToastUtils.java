package com.oushang.lib_base.utils;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oushang.lib_base.env.LibraryRuntimeEnv;

/**
 * @Author: zeelang
 * @Description: toast
 * @Date: 2021/6/28
 */
public class ToastUtils {

    /**
     * 上次显示时间
     */
    private static long lastShowTime = 0;

    /**
     * 显示间隔时间
     */
    private static final int SHORT_DURATION_TIMEOUT = 2000;


    /**
     * 显示toast弹窗
     * @param resourceId toast布局
     * @param textViewId 文本控件
     * @param msg toast消息
     */
    public static void showToast(int resourceId, int textViewId, String msg) {
        View view = LayoutInflater.from(LibraryRuntimeEnv.get().getContext()).inflate(resourceId, null);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShowTime >SHORT_DURATION_TIMEOUT) {
            if (view == null) {
                Toast.makeText(LibraryRuntimeEnv.get().getContext(), msg, Toast.LENGTH_SHORT).show();
            } else {
                TextView tv = view.findViewById(textViewId);
                tv.setText(msg);
                Toast toast = Toast.makeText(LibraryRuntimeEnv.get().getContext(), msg, Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            lastShowTime = currentTime;
        }
    }

    private static Toast toast = null;
    public static void showToastNew(int resourceId, int textViewId, String msg) {
        if(null != toast){
            Log.d("heqq","toast show");
            toast.cancel();
        }
        View view = LayoutInflater.from(LibraryRuntimeEnv.get().getContext()).inflate(resourceId, null);
        if (view == null) {
            toast = Toast.makeText(LibraryRuntimeEnv.get().getContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            TextView tv = view.findViewById(textViewId);
            tv.setText(msg);
            toast = Toast.makeText(LibraryRuntimeEnv.get().getContext(), msg, Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void cancelCurrentToast(){
        if(null != toast){
            Log.d("heqq","cancelCurrentToast show");
            toast.cancel();
        }
    }

}
