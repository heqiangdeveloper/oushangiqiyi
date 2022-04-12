package com.oushang.iqiyi.mcu.handler;

import android.car.hardware.CarPropertyValue;
import android.provider.Settings;
import android.util.Log;

import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.lib_service.constant.VideoNoPlay;

/**
 * @Author: zeelang
 * @Description: 车速处理
 * @Time: 2022/1/21 16:09
 * @Since: 1.0
 */
public class VehicleSpeedHandler extends BaseHandler<CarPropertyValue<Float>> {

    private VehicleSpeedHandler() {}

    private static class VehicleSpeedHandlerHolder {
       static VehicleSpeedHandler HOLDER = new VehicleSpeedHandler();
    }

    public static VehicleSpeedHandler getInstance() {
        return VehicleSpeedHandlerHolder.HOLDER;
    }

    @Override
    public void handler(CarPropertyValue<Float> carPropertyValue) {
        Log.d(TAG, "VehicleSpeedHandler:" + carPropertyValue);
        float speed = carPropertyValue.getValue(); //获取车速
        int disable = Settings.System.getInt(MainApplication.getContext().getContentResolver(),"video_disable_on_driving",0);//行车观看开关, 0 关闭， 1 打开
        Log.d(TAG, "speed:" + speed + ",disable:" + disable);
        if(speed > Constant.CAR_SPEED_TIP_VALUE) { //车速大于7km/h
            if(mPlayManager.isPlaying() && disable == 0) { //视频在播放中且行车观看开关关闭
                Log.d(TAG, "over speed pause");
                VideoNoPlay.getInstance().noPlay(VideoNoPlay.NO_PLAY_REASON_OVER_SPEED);//禁止播放
                mPlayManager.pause(); //暂停播放
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5923);
            } else if(disable == 1) { //行车观看开关打开
                VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_OVER_SPEED);//解除禁止播放
            }
        } else { //车速小于等于7km/h
            VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_OVER_SPEED); //解除禁止播放
        }
    }
}
