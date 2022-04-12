package com.oushang.iqiyi.mcu.handler;

import android.car.hardware.CarPropertyValue;
import android.util.Log;


/**
 * @Author: zeelang
 * @Description: 电源管理
 * @Time: 2022/1/21 16:14
 * @Since: 1.0
 */
public class McuPowerHandler extends BaseHandler<CarPropertyValue<Integer>> {

    private McuPowerHandler(){}

    private static class McuPowerHandlerHolder{
        static McuPowerHandler HOLDER = new McuPowerHandler();
    }

    public static McuPowerHandler getInstance() {
        return McuPowerHandlerHolder.HOLDER;
    }

    @Override
    public void handler(CarPropertyValue<Integer> carPropertyValue) {
        Log.d(TAG, "McuPowerHandler CarPropertyValue:" + carPropertyValue);
        int powerState = carPropertyValue.getValue();
        Log.d(TAG, "powerState:" + powerState);

    }
}
