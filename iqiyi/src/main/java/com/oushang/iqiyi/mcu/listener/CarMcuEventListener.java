package com.oushang.iqiyi.mcu.listener;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.mcu.CarMcuManager;
import android.util.Log;

import com.oushang.iqiyi.mcu.handler.McuPowerHandler;
import com.oushang.iqiyi.mcu.handler.VehicleSpeedHandler;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2022/1/21 15:22
 * @Since: 1.0
 */
public class CarMcuEventListener implements CarMcuManager.CarMcuEventCallback, DispatchEventListener{

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        Log.d(TAG, "CarMcuEventCallback carPropertyValue:" + carPropertyValue);
        int propertyId = carPropertyValue.getPropertyId();
        switch (propertyId) {
            case CarMcuManager.ID_PERF_VEHICLE_SPEED://【反馈】车速数据
                dispatchHandler(VehicleSpeedHandler.getInstance(), carPropertyValue);
                break;
            case CarMcuManager.ID_VENDOR_MCU_POWER_MODE: //【反馈】电源管理模式
                dispatchHandler(McuPowerHandler.getInstance(), carPropertyValue);
                break;
        }
    }

    @Override
    public void onErrorEvent(int i, int i1) {
        Log.d(TAG, "CarMcuEventCallback onErrorEvent:" + i + "," + i1);
    }
}
