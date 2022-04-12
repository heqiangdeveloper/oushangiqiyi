package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 批量删除记录回调
 * @Time: 2021/9/3 14:41
 * @Since: 1.0
 */
public interface IDeleteRecordListCallback {

    void onSuccess();

    void onFailure(int errCode, String msg);

}
