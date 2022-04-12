package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 删除/清空记录回调
 * @Time: 2021/9/3 14:34
 * @Since: 1.0
 */
public interface IDeleteCallback {

    void onSuccess();

    void onFailure(String err);

}
