package com.oushang.lib_service.entries;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/10/9 0009  10:54
 * @Since: 1.0
 */
public class RegisterEntity {
    private String deviceUUID;
    private String sn;
    private long timestamp;

    public RegisterEntity(String deviceUUID, String sn) {
        this.deviceUUID = deviceUUID;
        this.sn = sn;
        timestamp = System.currentTimeMillis();
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
