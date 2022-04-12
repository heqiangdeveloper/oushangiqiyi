package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: zeelang
 * @Description: 用户信息回调
 * @Date: 2021/6/24
 */
public interface IUserInfoCallback {

    /**
     * 查询成功，返回UserInfo
     * @param userInfo
     */
    void onUpdateUserInfo(UserInfo userInfo);

    /**
     * 账号失效
     * @param code 错误码，msg 错误信息
     */
    void onFailuer(String code, String msg);


}
