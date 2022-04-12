package com.oushang.lib_service.entries;

/**
 * @Author: zeelang
 * @Description: 设备注册实体类
 * @Time: 2021/8/25 17:30
 * @Since: 1.0
 */
public class DeviceRegister {

    private String token; //签名

    private int expiredIn; //秘钥有效期，单位分钟, 默认有效期7天

    public DeviceRegister() {}

    public DeviceRegister(String token, int expiredIn) {
        this.token = token;
        this.expiredIn = expiredIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiredIn() {
        return expiredIn;
    }

    public void setExpiredIn(int expiredIn) {
        this.expiredIn = expiredIn;
    }

    @Override
    public String toString() {
        return "DeviceRegister{" +
                "token='" + token + '\'' +
                ", expiredIn=" + expiredIn +
                '}';
    }
}
