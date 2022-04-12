package com.oushang.iqiyi.ui;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Author: zeelang
 * @Description: 手势监听
 * @Time: 2021/10/29 0029  11:07
 * @Since: 1.0
 */
public class PlayerVideoGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = PlayerVideoGestureListener.class.getSimpleName();

    private static final int FLING_MIN_DISTANCE = 1;
    private VideoGestureListener mVideoGestureListener;

    public PlayerVideoGestureListener(VideoGestureListener listener) {
        this.mVideoGestureListener = listener;
    }

    /**
     * 当按下时触发该方法，所有手势第一个必定触发该方法
     * @param e 触摸事件
     * @return 是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onDown(MotionEvent e) { //当按下时触发该方法，所有手势第一个必定触发该方法
        Log.d(TAG, "onDown");
        return true;
    }

    /**
     * 当用户手指按下，但没有移动时触发该方法
     * @param e 触摸事件
     */
    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress");
        super.onShowPress(e);
    }

    /**
     * 当用户单击时触发
     * @param e 触摸事件
     * @return  是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        return true;
    }

    /**
     * 当用户手指在屏幕上拖动触发
     * @param e1 首次触摸事件
     * @param e2 最后一次触摸事件
     * @param distanceX x方向移动距离
     * @param distanceY y方向移动距离
     * @return 是否拦截，true 拦截，false 不拦截
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll{MotionEvent:e1:" + e1 + ",e2:" +e2 +",distanceX:" + distanceX + ",distanceY" + distanceY + "}");
        if (mVideoGestureListener != null && e1 != null && e2 != null) {
            if (e1.getX() > 70 && e2.getX() - e1.getX()> 0 && Math.abs(distanceX) > FLING_MIN_DISTANCE) {
                mVideoGestureListener.onSeekGesture(e1, e2, distanceX, distanceY);
                Log.d(TAG, "scroll by right");
            } else if (e1.getX() > 70 && e1.getX() - e2.getX() > 0 && Math.abs(distanceX) > FLING_MIN_DISTANCE) {
                mVideoGestureListener.onSeekGesture(e1, e2, distanceX, distanceY);
                Log.d(TAG, "scroll by left");
            } else if (e2.getY() - e1.getY() > 0 && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                Log.d(TAG, "scroll by down");
            } else if (e1.getY() - e2.getY() > 0 && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                Log.d(TAG, "scroll by up");
            }
            return true;

        }


        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    /**
     * 当用户长按屏幕时触发
     * @param e 触摸事件
     */
    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress");
        super.onLongPress(e);
    }

    /**
     * 当用户手指拖动后，手指离开屏幕时触发
     * @param e1  触摸事件，第一个ACTION_DOWN
     * @param e2  触摸事件，最后一个ACTION_MOVE
     * @param velocityX  x方向上的移动速度 (像素/秒）
     * @param velocityY  y方向上的移动速度 (像素/秒）
     * @return 是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling：" + velocityX + "," + velocityY);
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    /**
     * 单击确定后
     * @param e 触摸事件
     * @return 是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed");
        if (mVideoGestureListener != null) {
            mVideoGestureListener.onSingleTapGesture();
            return true;
        }
        return super.onSingleTapConfirmed(e);
    }

    /**
     * 当用户双击触发
     * @param e 触摸事件
     * @return  是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap");
        if (mVideoGestureListener != null) {
            mVideoGestureListener.onDoubleTapGesture();
            return true;
        }
        return super.onDoubleTap(e);
    }

    /**
     * 在双击事件确定发生时会对第二次按下产生的 MotionEvent 信息进行回调。
     * @param e 触摸事件
     * @return  是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent");
        return super.onDoubleTapEvent(e);
    }

    /**
     * 用于检测外部设备上的按钮是否按下的，例如蓝牙触控笔上的按钮，一般情况下，忽略即可。
     * @param e 触摸事件
     * @return  是否拦截，true 拦截 false 不拦截
     */
    @Override
    public boolean onContextClick(MotionEvent e) {
        Log.d(TAG, "onContextClick");
        return super.onContextClick(e);
    }
}
