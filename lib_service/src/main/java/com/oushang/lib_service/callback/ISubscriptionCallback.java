package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: ζΆθεθ°
 * @Time: 2021/9/3 16:41
 * @Since: 1.0
 */
public interface ISubscriptionCallback {

    void onSuccess();

    void onFailure(int errCode, String msg);
}
