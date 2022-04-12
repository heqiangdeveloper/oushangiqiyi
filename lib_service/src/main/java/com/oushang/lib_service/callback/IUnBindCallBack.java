package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: ***
 * @Time: 2021/11/3 0003  15:06
 * @Since: 1.0
 */
public interface IUnBindCallBack {

    /**
     * 解绑成功
     */
    void onSuccess();

    /**
     * 解绑失败
     * @param code 错误码
     * @param msg 错误信息
     */
    void onFailure(String code, String msg);
}
