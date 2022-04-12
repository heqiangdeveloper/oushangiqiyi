package com.oushang.iqiyi.mcu.listener;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.cabin.CarCabinManager;
import android.util.Log;

import com.oushang.iqiyi.mcu.handler.AVMHandler;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2022/1/21 15:24
 * @Since: 1.0
 */
public class CarCabinEventListener implements CarCabinManager.CarCabinEventCallback, DispatchEventListener{

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        Log.d(TAG, "CarCabinEventCallback carPropertyValue:" + carPropertyValue);
        int propertyId = carPropertyValue.getPropertyId();
        switch (propertyId) {
//            case CarCabinManager.ID_AVM_DISPLAY_SWITCH: //【设置/反馈】全景显示请求
            case CarCabinManager.ID_MCU_AVM_DISPLAY_SWITCH: //【反馈】全景显示请求
                dispatchHandler(AVMHandler.getInstance(), carPropertyValue);
                break;
        }
    }

    @Override
    public void onErrorEvent(int i, int i1) {
        Log.d(TAG, "CarCabinEventCallback onErrorEvent:" + i + "," + i1);

    }
}
