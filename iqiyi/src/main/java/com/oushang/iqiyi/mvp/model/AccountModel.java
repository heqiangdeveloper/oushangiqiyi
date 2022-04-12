package com.oushang.iqiyi.mvp.model;

import android.graphics.Bitmap;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_service.callback.IBindLoginCallBack;
import com.oushang.lib_service.callback.ILoginQRCodeCallback;
import com.oushang.lib_service.callback.ILogoutCallback;
import com.oushang.lib_service.interfaces.MyAccountManager;

/**
 * @Author: zeelang
 * @Description: 账号业务逻辑
 * @Time: 2021/8/9 10:12
 * @Since: 1.0
 */
public class AccountModel extends BaseServiceModel {

    @Autowired
    MyAccountManager mAccountManager;

    /**
     * 生成二维码，通过扫码登录，轮询登录是否成功
     * @param codeCallback 二维码扫码登录回调
     */
    public void loginQrCode(ILoginQRCodeCallback codeCallback) {
        mAccountManager.loginQRCode(codeCallback);
    }

    /**
     * 生成绑定二维码，通过扫码登录和绑定，轮询登录和绑定是否成功
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param codeCallback 二维码登录回调
     */
    public void loginBindQrCode(String accessToken, String ottVersion, String partnerEnv,ILoginQRCodeCallback codeCallback) {
        mAccountManager.loginBindQRCode(accessToken,ottVersion,partnerEnv,codeCallback);
    }

    /**
     * 绑定登录
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param callBack    绑定登录回调
     */
    public void loginBind(String accessToken, String ottVersion, String partnerEnv, IBindLoginCallBack callBack) {
        mAccountManager.loginBind(accessToken,ottVersion,partnerEnv,callBack);
    }

    /**
     * 退出登录
     * @param callback 退出登录回调
     */
    public void logout(ILogoutCallback callback) {
        mAccountManager.logOut(callback);
    }

    /**
     * 是否登录（远程端）
     * @return 是 true, 否 false
     */
    public boolean isLogin() {
        return mAccountManager.isLogin();
    }


    //处理绑定二维码
    public Bitmap handleBindQrCode(String qrUrl){
        return  mAccountManager.handleBindQrCode(qrUrl);
    }

}
