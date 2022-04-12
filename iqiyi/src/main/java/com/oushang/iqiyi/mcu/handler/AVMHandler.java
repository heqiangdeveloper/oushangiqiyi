package com.oushang.iqiyi.mcu.handler;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.constant.AVM;
import android.util.Log;

import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_service.constant.VideoNoPlay;

/**
 * @Author: zeelang
 * @Description: 【设置/反馈】全景显示请求处理
 * @Time: 2022/1/21 16:16
 * @Since: 1.0
 */
public class AVMHandler extends BaseHandler<CarPropertyValue<Integer>>{

    private AVMHandler() {}

    private static class AVMHandlerHolder{
        static AVMHandler HOLDER = new AVMHandler();
    }

    public static AVMHandler getInstance() {
        return AVMHandlerHolder.HOLDER;
    }

    @Override
    public void handler(CarPropertyValue<Integer> carPropertyValue) {
        if(carPropertyValue != null) {
            int avmStatus = carPropertyValue.getValue();
            String prop = AppUtils.getSystemProperty("VIDEO_REQUEST_AVM");
            Log.d(TAG, "avmStatus: " + avmStatus + ",prop:" + prop);
            if (avmStatus == AVM.AVM_ON) {
                Log.d(TAG, "avm on pause");
                VideoNoPlay.getInstance().noPlay(VideoNoPlay.NO_PLAY_REASON_AVM_ON);
                mPlayManager.pause();
            } else if(avmStatus == AVM.AVM_OFF) {
                Log.d(TAG, "avm off start");
                VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_AVM_ON);
                mPlayManager.start();
            }
        }
    }
}
