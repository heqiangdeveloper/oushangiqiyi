package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: zeelang
 * @Description: 账号接口
 * @Time: 2021/8/31 16:18
 * @Since: 1.0
 */
public interface IAccountView extends IBaseView {

    int QRCODE_TYPE_NORMAL = 1; //二维码类型：普通二维码
    int QRCODE_TYPE_BIND = 2; //二维码类型：绑定二维码

    int LOGIN_TYPE_SCAN_QRCODE = 1;     //扫码登录
    int LOGIN_TYPE_SCAN_BIND_QRCODE = 2;//扫码绑定登录
    int LOGIN_TYPE_AUTHORIZED_BIND = 3; //授权或静默绑定登录


    /**
     * 回调加载扫码登录二维码，仅在车机端账号未登录
     * @param code   二维码
     * @param expire 有效期
     */
    void onLoadLoginQRCode(String code, int expire);

    /**
     * 回调加载绑定登录二维码，车机端账号已登录
     * @param isBind  车机端是否绑定
     * @param code    二维码
     * @param expire  有效期
     */
    void onLoadBindLoginQRCode(boolean isBind, String code, int expire);

    /**
     *  回调加载二维码错误
     * @param qrCodeType 二维码类型
     * @param code 错误码
     * @param msg 错误信息
     */
    void onLoadQRCodeFail(int qrCodeType, String code, String msg);

    /**
     * 不存在绑定关系
     */
    void onNoBind();

    /**
     * 账号登录成功回调接口
     * @param loginType 登录方式
     * @param userInfo 用户信息
     */
    void onAccountLogin(int loginType, UserInfo userInfo);

    /**
     * 账号登录失败回调
     * @param loginType 登录方式
     */
    void onAccountLoginFail(int loginType, String code, String msg);

    /**
     * 退出登录成功
     */
    void onLogoutSuccess(boolean autoBind);


}
