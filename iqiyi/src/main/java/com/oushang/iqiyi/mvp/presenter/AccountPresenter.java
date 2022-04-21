package com.oushang.iqiyi.mvp.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.mvp.model.AccountModel;
import com.oushang.iqiyi.mvp.view.IAccountView;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.IBindLoginCallBack;
import com.oushang.lib_service.callback.ILoginQRCodeCallback;
import com.oushang.lib_service.callback.ILogoutCallback;
import com.oushang.lib_service.entries.UserInfo;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 账号视图逻辑
 * @Time: 2021/8/9 10:09
 * @Since: 1.0
 */
public class AccountPresenter extends BaseServicePresenter<IAccountView, AccountModel> {
    private static final String TAG = AccountPresenter.class.getSimpleName();

    @Override
    protected AccountModel createModel() {
        return new AccountModel();
    }

    /**
     *  加载扫码登录二维码
     */
    public void loadQRCode() {
        Log.d(TAG, "loadQRCode");
        if(isAttach()) {
            showLoading();
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                Log.d(TAG, "sdk is init success:" + isInit);
                if (isInit) { //如果已初始化完成
                    model.loginQrCode(new ILoginQRCodeCallback() {
                        @Override
                        public void onQRCodeSuccess(String token, String qrcode, int expire) { //获取登录二维码成功回调
                            Log.d(TAG, "loadQRCode onQRCodeSuccess: token:" + token + ",qrcode:" + qrcode + ",expire:" + expire);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onLoadLoginQRCode(qrcode, expire);
                            });
                        }

                        @Override
                        public void onQRCodeFail(String errCode, String msg) { //获取登录二维码失败回调
                            Log.e(TAG, "loadQRCode onQRCodeFail:" + errCode);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onLoadQRCodeFail(IAccountView.QRCODE_TYPE_NORMAL, errCode, msg);
                            });
                        }

                        @Override
                        public void onLoginSuccess(UserInfo userInfo) { //扫码登录成功回调，返回用户信息
                            Log.d(TAG,"loadQRCode onLoginSuccess:" + userInfo);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLogin(IAccountView.LOGIN_TYPE_SCAN_QRCODE, userInfo);
                            });
                        }

                        @Override
                        public void onLoginFail(String errCode, String msg) { //扫码登录失败回调，返回错误信息
                            Log.e(TAG, "loadQRCode onLoginFail: errCode:" + errCode + ",msg:" + msg);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLoginFail(IAccountView.LOGIN_TYPE_SCAN_QRCODE, errCode, msg);
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 加载绑定二维码
     * @param isBind       车机端是否绑定
     * @param accessToken   accessToken
     * @param ottVersion    ottVersion
     * @param partnerEnv    partnerEnv
     */
    public void loadBindQRCode(boolean isBind, String accessToken, String ottVersion, String partnerEnv) {
        Log.d(TAG, "loadBindQRCode: isBind:" + isBind + ",accessToken:" + accessToken + ",ottVersion:" + ottVersion + ",partnerEnv:" + partnerEnv);
        if(isAttach()) {
            showLoading();
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                if(isInit) {
                    model.loginBindQrCode(accessToken, ottVersion, partnerEnv, new ILoginQRCodeCallback() {
                        @Override
                        public void onQRCodeSuccess(String token, String qrcode, int expire) {
                            Log.d(TAG, "loadBindQRCode onQRCodeSuccess: token:" + token + ",qrcode:" + qrcode + ",expire:" + expire);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onLoadBindLoginQRCode(isBind, qrcode, expire);
                            });
                        }

                        @Override
                        public void onQRCodeFail(String errCode, String msg) {
                            Log.d(TAG, "loadBindQRCode onQRCodeFail:" + errCode);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onLoadQRCodeFail(IAccountView.QRCODE_TYPE_BIND, errCode, msg);
                            });
                        }

                        @Override
                        public void onLoginSuccess(UserInfo userInfo) {
                            Log.d(TAG, "loadBindQRCode onLoginSuccess:" + userInfo);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLogin(IAccountView.LOGIN_TYPE_SCAN_BIND_QRCODE, userInfo);
                            });
                        }

                        @Override
                        public void onLoginFail(String errCode, String msg) {
                            Log.d(TAG, "loadBindQRCode onLoginFail:" + errCode);
                            hideLoading();
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLoginFail(IAccountView.LOGIN_TYPE_SCAN_BIND_QRCODE, errCode, msg);
                            });
                        }
                    });
                }

            });
        }

    }

    /**
     * 绑定登录，（授权登录）
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     */
    public void loadBindLogin(String accessToken, String ottVersion, String partnerEnv) {
        Log.d(TAG, "loadBindLogin: accessToken:" + accessToken + ",ottVersion:" + ottVersion + ",partnerEnv:" + partnerEnv);
        if(isAttach()) {
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                if(isInit) {
                    model.loginBind(accessToken, ottVersion, partnerEnv, new IBindLoginCallBack() {
                        @Override
                        public void onLoginSuccess(UserInfo userInfo) {
                            Log.d(TAG, "loadBindLogin onLoginSuccess:" + userInfo);
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLogin(IAccountView.LOGIN_TYPE_AUTHORIZED_BIND, userInfo);
                            });
                        }

                        @Override
                        public void onNoBind() {
                            Log.d(TAG, "loadBindLogin onNoBind");
                            Optional.ofNullable(getView()).ifPresent(IAccountView::onNoBind);
                        }

                        @Override
                        public void onFailure(String code, String msg) {
                            Log.d(TAG, "loadBindLogin onFailure: code:" + code + ",msg:" + msg);
                            Optional.ofNullable(getView()).ifPresent(iAccountView -> {
                                iAccountView.onAccountLoginFail(IAccountView.LOGIN_TYPE_AUTHORIZED_BIND, code, msg);
                            });
                        }
                    });
                }

            });
        }
    }

    /**
     * 退出登录
     */
    public void logout(boolean autoBind) {
        Log.d(TAG, "logout");
        if (isAttach()) {
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                Log.d(TAG, "sdk is init success:" + isInit);
                if (isInit) { //如果已初始化完成
                    model.logout(new ILogoutCallback() {
                        @Override
                        public void onLogoutSuccess() {
                            Log.d(TAG,"onLogoutSuccess");
                            SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
                            getView().onLogoutSuccess(autoBind);
                        }

                        @Override
                        public void onLogoutFail(String errCode, String msg) {
                            Log.e(TAG, "onLogoutFail:" + errCode + ",msg:" + msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 是否登录（远程端）
     * @return true 是，false 否
     */
    public boolean isLogin() {
        return model.isLogin();
    }

    /**
     * 获取二维码图片
     * @param qrcode 二维码信息
     * @return bitmap
     */
    public Bitmap getQRCodeBitmap(String qrcode) {
        return model.handleBindQrCode(qrcode);
    }

}
