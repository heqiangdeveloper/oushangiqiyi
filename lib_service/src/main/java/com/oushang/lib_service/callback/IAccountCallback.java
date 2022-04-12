package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.Account;

/**
 * @Author: zeelang
 * @Description: 账号回调监听
 * @Time: 2021/9/1 16:10
 * @Since: 1.0
 */
public interface IAccountCallback {

    int ACCOUNT_STATE_LOGIN = 0; //登录成功
    int ACCOUNT_STATE_LOGOUT = 1;
    int ACCOUNT_BENEFIT_SKIP_AD_ENABLED = 100; //允许跳过广告
    int ACCOUNT_BENEFIT_SKIP_AD_DISABLED = 101; //不允许跳过广告

    /**
     *  账号权益变更回调
     * @param account 账号
     * @param skip 是否具有跳过广告的权益
     */
    void onBenefitChanged(Account account, int skip);

    /**
     * 登录成功后回调
     * @param account 账号
     * @param state 登录状态
     * @param b
     */
    void OnStateChanged(Account account, int state, boolean b);
}
