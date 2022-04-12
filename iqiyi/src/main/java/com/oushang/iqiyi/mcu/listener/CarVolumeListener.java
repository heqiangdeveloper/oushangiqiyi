package com.oushang.iqiyi.mcu.listener;

import android.car.media.ICarVolumeCallback;
import android.os.RemoteException;
import android.util.Log;

import com.oushang.iqiyi.mcu.handler.MuteChangedHandler;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2022/1/21 15:25
 * @Since:
 */
public class CarVolumeListener extends ICarVolumeCallback.Stub implements DispatchEventListener{

    @Override
    public void onGroupVolumeChanged(int i, int i1) throws RemoteException {
        Log.d(TAG, "onGroupVolumeChanged:" + i + "," + i1);
    }

    @Override
    public void onMasterMuteChanged(int i) throws RemoteException {
        Log.d(TAG, "onMasterMuteChanged:" + i);
        dispatchHandler(MuteChangedHandler.getInstance(), null);
    }
}
