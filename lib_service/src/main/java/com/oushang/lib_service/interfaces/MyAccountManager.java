package com.oushang.lib_service.interfaces;

import android.graphics.Bitmap;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.callback.IBindLoginCallBack;
import com.oushang.lib_service.callback.ILoginQRCodeCallback;
import com.oushang.lib_service.callback.ILoginRenewCallBack;
import com.oushang.lib_service.callback.ILogoutCallback;
import com.oushang.lib_service.callback.IUnBindCallBack;
import com.oushang.lib_service.callback.IUserInfoCallback;

public interface MyAccountManager extends IProvider {

    /**
     * 生成二维码，通过扫码登录，轮询登录是否成功
     * @param codeCallback 二维码扫码登录回调
     */
    void loginQRCode(ILoginQRCodeCallback codeCallback);

    /**
     * 生成绑定二维码，通过扫码登录和绑定，轮询登录和绑定是否成功
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param codeCallback 二维码登录回调
     */
    void loginBindQRCode(String accessToken, String ottVersion, String partnerEnv, ILoginQRCodeCallback codeCallback);

    /**
     * 绑定登录
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param callBack    绑定登录回调
     */
    void loginBind(String accessToken, String ottVersion, String partnerEnv, IBindLoginCallBack callBack);

    /**
     * 退出登录
     * @param callback 退出回调
     */
    void logOut(ILogoutCallback callback);

    /**
     * 解绑
     * @param accessToken 合作方颁发的 access_token
     * @param partnerEnv 后台多环境传参，没有则默认传空
     * @param callBack 回调
     */
    void unBind(String accessToken, String partnerEnv, IUnBindCallBack callBack);

    /**
     * 是否登录，服务端账号是否在登录状态
     * @return 是 true, 否 false
     */
    boolean isLogin();

    /**
     * 更新 authCookie
     * 一次登录有效期为 90 天，该接口调用成功后，顺延 90 天；
     * 建议每次应用启动后，调用一次该接口用于账号续租；
     * @param callBack 更新回调
     */
    void loginRenew(ILoginRenewCallBack callBack);

    /**
     * 用户信息（vip 信息）查询接口
     * @param wrapperCallback 查询回调
     */
    void updateUserInfo(IUserInfoCallback wrapperCallback);

    /*
    *  处理绑定二维码
     */
    Bitmap handleBindQrCode(String qrUrl);

}
