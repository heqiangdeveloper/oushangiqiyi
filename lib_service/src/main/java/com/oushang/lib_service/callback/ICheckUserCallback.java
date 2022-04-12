package com.oushang.lib_service.callback;

/**
 * @Author: zeelang
 * @Description: 账号有效性校验接口
 * @Date: 2021/6/24
 */
public interface ICheckUserCallback {

    /**
     * 账号有效回调
     */
    void onCheckSuccess();

    /**
     * 账号已失效回调
     * @param errCode 错误信息
     */
    void onCheckFailure(String errCode);
}
