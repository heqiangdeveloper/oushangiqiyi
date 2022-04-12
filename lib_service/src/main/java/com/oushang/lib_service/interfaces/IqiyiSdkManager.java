package com.oushang.lib_service.interfaces;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.callback.ISDKInitializedCallBack;
import com.oushang.lib_service.entries.DeviceRegister;

import io.reactivex.Observable;

public interface IqiyiSdkManager extends IProvider {

    /**
     * 初始化sdk
     */
    void initSdk(ISDKInitializedCallBack callBack);

    /**
     * sdk 是否初始化
     * @return true or false
     */
    boolean isSdkInitialized();


    /**
     *  设备注册
     * @param deviceUUID 硬件型号uuid，由爱奇艺分配
     * @param sn sn 车架号
     * @return 注册信息
     */
    Observable<DeviceRegister> register(String deviceUUID, String sn);
}
