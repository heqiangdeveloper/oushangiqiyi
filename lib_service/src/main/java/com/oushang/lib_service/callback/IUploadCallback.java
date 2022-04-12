package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 上传播放记录回调
 * @Time: 2021/9/3 14:29
 * @Since: 1.0
 */
public interface IUploadCallback {

    void onSuccess();

    void onFailure(String err);

}
