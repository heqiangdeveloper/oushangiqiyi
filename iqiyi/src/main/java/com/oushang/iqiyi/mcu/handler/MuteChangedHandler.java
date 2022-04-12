package com.oushang.iqiyi.mcu.handler;

import android.util.Log;

import com.oushang.iqiyi.mcu.CarManager;
import com.oushang.lib_service.constant.VideoNoPlay;

/**
 * @Author: zeelang
 * @Description: 系统静音状态处理
 * @Time: 2022/1/21 16:21
 * @Since: 1.0
 */
public class MuteChangedHandler extends BaseHandler<Void>{

    private MuteChangedHandler(){}

    private static class MuteChangeHandlerHolder{
        static MuteChangedHandler HOLDER = new MuteChangedHandler();
    }

    public static MuteChangedHandler getInstance() {
        return MuteChangeHandlerHolder.HOLDER;
    }

    @Override
    public void handler(Void unused) {
        if(CarManager.getInstance().isMute()) { //是否静音
            Log.d(TAG, "isMute true, mute pause");
            VideoNoPlay.getInstance().noPlay(VideoNoPlay.NO_PLAY_REASON_MASTER_MUTE);
            mPlayManager.pause();
        } else {
            Log.d(TAG, "isMute false, mute start");
            VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_MASTER_MUTE);
            mPlayManager.start();
        }
    }
}
