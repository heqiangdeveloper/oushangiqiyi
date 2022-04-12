package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: zeelang
 * @Description: 登录二维码回调
 * @Date: 2021/6/24
 */
public interface ILoginQRCodeCallback {

    /**
     * 生成二维码成功回调
     * @param token token 签名
     * @param qrcode 二维码信息
     * @param expire expire 秘钥有效期
     */
    void onQRCodeSuccess(String token, String qrcode, int expire);

    /**
     * 生成二维码失败回调
     * @param errCode 错误信息
     */
    void onQRCodeFail(String errCode, String msg);

    /**
     * 登录成功回调
     * @param userInfo UserInfoWrapper 用户信息
     */
    void onLoginSuccess(UserInfo userInfo);

    /**
     * 登录失败回调
     * @param errCode 错误信息
     */
    void onLoginFail(String errCode, String msg);

}
