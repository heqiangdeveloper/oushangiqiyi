package com.oushang.iqiyi.ui;

import android.view.MotionEvent;

/**
 * @Author: zeelang
 * @Description: 播放手势
 * @Time: 2021/11/18 0018  12:46
 * @Since: 1.0
 */
public interface VideoGestureListener {

    default void onBrightNessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){}

    default void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){}

    default void onSeekGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){}

    default void onSingleTapGesture(){}

    default void onDoubleTapGesture(){}

}
