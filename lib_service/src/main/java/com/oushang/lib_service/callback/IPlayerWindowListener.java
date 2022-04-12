package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 播放窗口监听
 * @Time: 2021/11/16 0016  13:59
 * @Since: 1.0
 */
public interface IPlayerWindowListener {

    default void onCreate(){}

    default void onStart(){}

    default void onResume(){}

    default void onPause(){}

    default void onStop(){}

    default void onDestroy(){}

}
