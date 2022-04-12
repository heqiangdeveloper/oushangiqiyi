package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 同步更新记录回调
 * @Time: 2021/9/3 14:38
 * @Since: 1.0
 */
public interface IMergeCallback {

    void onSuccess();

    void onFailure(String err);

}
