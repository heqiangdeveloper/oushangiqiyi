package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 退出登录回调
 * @Date: 2021/6/24
 */
public interface ILogoutCallback {

    /**
     * 退登成功
     */
    void onLogoutSuccess();

    /**
     * 退登失败
     * @param errCode 错误代码
     * @param msg 错误信息
     */
    void onLogoutFail(String errCode, String msg);
}
