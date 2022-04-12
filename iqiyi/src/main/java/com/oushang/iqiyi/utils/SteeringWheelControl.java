package com.oushang.iqiyi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.MainApplication;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;

/**
 * @Author: zeelang
 * @Description: 方向盘控制
 * @Time: 2021/10/27 0027  11:29
 * @Since: 1.0
 */
public class SteeringWheelControl {
    private static final String TAG = SteeringWheelControl.class.getSimpleName();
    public static final String KEY_CODE_PRE = "PRE"; //上键 上一首 上一个
    public static final String KEY_CODE_NEXT = "NEXT"; //下键 下一首 下一个
    public static final String KEY_CODE_MUTE = "MUTE"; // 静音\解除静音  方控不响应，改由car服务处理

    public static final String KEY_STATE_UP = "UP"; //按键类型：抬起
    public static final String KEY_LONG_UP = "LONG_UP";//按键类型：长按抬起

    public static final String KEY_CODE = "Key_code";
    public static final String KEY_STATE = "Key_state";
    public static final String ACTION = "com.coagent.intent.action.KEY_CHANGED";//方控广播

    private static SteeringWheelControlReceiver mSteeringWheelControlReceiver; //车辆方向盘控制广播

    @Autowired(name = Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager; //播单列表管理（剧集）

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    public SteeringWheelControl() {
        ARouter.getInstance().inject(this);
    }

    /**
     * 注册方控广播
     */
    public static void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        filter.addAction("test.speed");
        mSteeringWheelControlReceiver = new SteeringWheelControlReceiver();
        MainApplication.getContext().registerReceiver(mSteeringWheelControlReceiver, filter);
    }

    /**
     * 取消注册方控广播
     */
    public static void unRegister() {
        if (mSteeringWheelControlReceiver != null) {
            MainApplication.getContext().unregisterReceiver(mSteeringWheelControlReceiver);
            mSteeringWheelControlReceiver = null;
        }
    }

    /**
     * 切换上一集播放
     */
    public void prev(){
        Log.d(TAG, "prev operation");
        if (mPlayManager != null && mPlayManager.isAlbum()) {
            Log.d(TAG, "play prev");
            mPlayManager.playPrev();
        } else {
            Log.e(TAG, "current videoInfo is not album");
        }
    }

    /**
     * 切换下一集播放
     */
    public void next() {
        Log.d(TAG, "next operation");
        if (mPlayManager != null && mPlayManager.isAlbum() && !AppUtils.isPhoneCalling()) { //不在通话中，才响应切换下一集
            Log.d(TAG, "play next");
            mPlayManager.playNext();
        } else {
            Log.e(TAG, "current videoInfo is not album");
        }

    }

    /**
     * 暂停播放
     */
    public void pause(){
        if (mPlayManager.isPlaying()) {
            Log.d(TAG, "pause");
            mPlayManager.pause();
        }
    }

    /**
     * 方控广播
     */
    public static class SteeringWheelControlReceiver extends BroadcastReceiver {
        SteeringWheelControl steeringWheelControl;

        public SteeringWheelControlReceiver() {
            steeringWheelControl = new SteeringWheelControl();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SteeringWheelControlReceiver onReceive");
            String action = intent.getAction();
            if (ACTION.equals(action)) {
                String keyCode = intent.getStringExtra(KEY_CODE);
                String keyState = intent.getStringExtra(KEY_STATE);
                Log.d(TAG,"keyCode:" + keyCode + ",keyState:" + keyState);
                if (KEY_STATE_UP.equals(keyState)) {
                    switch (keyCode) {
                        case KEY_CODE_PRE:
                            if (steeringWheelControl != null) {
                                steeringWheelControl.prev();
                            }
                            break;
                        case KEY_CODE_NEXT:
                            if (steeringWheelControl != null) {
                                steeringWheelControl.next();
                            }
                            break;
                    }
                }
            }
        }

    }
}
