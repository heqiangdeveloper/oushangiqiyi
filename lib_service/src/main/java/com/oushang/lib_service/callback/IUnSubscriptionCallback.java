package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 取消收藏回调
 * @Time: 2021/9/3 16:42
 * @Since: 1.0
 */
public interface IUnSubscriptionCallback {

    void onSuccess();

    void onFailure(String err);

}
