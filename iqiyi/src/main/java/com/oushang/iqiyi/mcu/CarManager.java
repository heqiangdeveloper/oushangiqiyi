package com.oushang.iqiyi.mcu;

import android.app.Application;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.mcu.CarMcuManager;
import android.car.media.CarAudioManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.oushang.iqiyi.mcu.listener.CarCabinEventListener;
import com.oushang.iqiyi.mcu.listener.CarMcuEventListener;
import com.oushang.iqiyi.mcu.listener.CarVolumeListener;

/**
 * @Author: zeelang
 * @Description: 中间件管理
 * @Time: 2022/1/21 15:05
 * @Since: 1.0
 */
public class CarManager {
    private static final String TAG = CarManager.class.getSimpleName();
    private Application mContext;
    private Car mCar;
    public CarMcuManager mCarMcuManager;
    public CarAudioManager mCarAudioManager;
    public CarCabinManager mCarCabinManager;

    public CarMcuEventListener mCarMcuEventListener;
    public CarCabinEventListener mCarCabinEventListener;
    public CarVolumeListener mCarVolumeListener;

    private volatile boolean isRegister = false;

    private static class CarManagerHolder {
        static CarManager HOLDER = new CarManager();
    }

    public static CarManager getInstance() {
        return CarManagerHolder.HOLDER;
    }

    public void init(Application application) {
        Log.d(TAG, "init");
        if(application == null) {
            throw new IllegalArgumentException("Application is null!");
        }
        this.mContext = application;
    }

    public void register() {
        Log.d(TAG, "register");
        if(!isRegister) {
            mCarMcuEventListener = new CarMcuEventListener();
            mCarCabinEventListener = new CarCabinEventListener();
            mCarVolumeListener = new CarVolumeListener();
            mCar = Car.createCar(mContext, mServiceConnection);
            mCar.connect();
            isRegister = true;
        }
    }

    public void unRegister() {
        Log.d(TAG, "unRegister");
        try {
            if (mCarMcuManager != null) {
                mCarMcuManager.unregisterCallback(mCarMcuEventListener);
            }
            if (mCarCabinManager != null) {
                mCarCabinManager.unregisterCallback(mCarCabinEventListener);
            }
            if (mCarAudioManager != null) {
                mCarAudioManager.unregisterVolumeCallback(mCarVolumeListener);
            }
            if (mCar != null && mCar.isConnected()) {
                mCar.disconnect();
            }
            isRegister = false;
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            try {
                if (mCarMcuManager == null) {
                    mCarMcuManager = (CarMcuManager) mCar.getCarManager(Car.CAR_MCU_SERVICE);
                }
                if (mCarAudioManager == null) {
                    mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
                }
                if (mCarCabinManager == null) {
                    mCarCabinManager = (CarCabinManager) mCar.getCarManager(Car.CABIN_SERVICE);
                }

                mCarMcuManager.registerCallback(mCarMcuEventListener, new int[]{
                        CarMcuManager.ID_PERF_VEHICLE_SPEED, //【反馈】车速数据
                        CarMcuManager.ID_VENDOR_MCU_POWER_MODE //【反馈】电源管理模式
                });

                mCarCabinManager.registerCallback(mCarCabinEventListener, new int[]{
//                        CarCabinManager.ID_AVM_DISPLAY_SWITCH, //【反馈】AVM状态
                        CarCabinManager.ID_MCU_AVM_DISPLAY_SWITCH //AVM显示请求
                });

                mCarAudioManager.registerVolumeCallback(mCarVolumeListener);

            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };


    /**
     * 判断是否处于静音
     *
     * @return true: 处于静音状态
     */
    public boolean isMute() {
        if(mContext == null) {
            throw new IllegalArgumentException("you must be init in Application!");
        }
        int state = Settings.Global.getInt(mContext.getContentResolver(),
                CarAudioManager.VOLUME_SETTINGS_KEY_MASTER_MUTE, 0);
        Log.d(TAG, "state: " + state);
        return state == 1;
    }


    /**
     * 解除静音
     */
    public void cancelMute() {
        try {
            if (mCarAudioManager != null) {
                Log.d(TAG, "cancel mute success");
                mCarAudioManager.setMasterMute(false, 0);
            } else {
                Log.e(TAG, "cancel mute fail");
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            Log.e(TAG, "cancel mute fail:" + Log.getStackTraceString(e));
        }
    }


}
