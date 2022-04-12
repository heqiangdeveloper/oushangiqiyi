package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/11/3 0003  14:48
 * @Since: 1.0
 */
public interface IBindLoginCallBack {

    /**
     * 绑定和登录成功回调
     * @param userInfo UserInfo 用户信息
     */
    void onLoginSuccess(UserInfo userInfo);

    /**
     * 未绑定回调
     */
    void onNoBind();


    /**
     *  绑定登录失败回调
     * @param code 错误码
     * @param msg 错误信息
     */
    void onFailure(String code, String msg);
}
