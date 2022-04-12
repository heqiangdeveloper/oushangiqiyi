package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 批量取消收藏回调
 * @Time: 2021/9/3 17:04
 * @Since: 1.0
 */
public interface IUnSubscriptionListCallback {

    void onSuccess();

    void onFailure(String err);

}
