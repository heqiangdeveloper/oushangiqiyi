package com.oushang.lib_service.callback;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/10/20 0020  16:37
 * @Since: 1.0
 */
public interface ISDKInitializedCallBack {

    void onSuccess();

    void onFailure(int code, String msg);
}
