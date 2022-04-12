package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 更新 authCookie 回调
 * @Time: 2021/10/20 0020  16:27
 * @Since: 1.0
 */
public interface ILoginRenewCallBack {

    void onSuccess();

    void onFailure(String code, String msg);

}
