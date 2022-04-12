package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 清空收藏回调
 * @Time: 2021/9/3 17:02
 * @Since: 1.0
 */
public interface IClearSubscriptionCallback {

    void onSuccess();

    void onFailure(String err);

}
