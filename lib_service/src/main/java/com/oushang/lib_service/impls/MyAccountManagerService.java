package com.oushang.lib_service.impls;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.iqy.iv.player.AccountManager;
import com.iqy.iv.player.SDKUserInfo;
import com.iqy.iv.sdk.PlayerSdk;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.IBindLoginCallBack;
import com.oushang.lib_service.callback.ILoginQRCodeCallback;
import com.oushang.lib_service.callback.ILoginRenewCallBack;
import com.oushang.lib_service.callback.ILogoutCallback;
import com.oushang.lib_service.callback.IUnBindCallBack;
import com.oushang.lib_service.callback.IUserInfoCallback;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.UserInfo;
import com.oushang.lib_service.interfaces.MyAccountManager;

@Route(path = Constant.PATH_SERVICE_ACCOUNT_MANAGER)
public class MyAccountManagerService implements MyAccountManager {
    private static final String TAG = MyAccountManagerService.class.getSimpleName();

    private AccountManager mAccountManager;
    private boolean isInit = false;

    public MyAccountManagerService() {
        if (!isInit) {
            init(MediatorServiceFactory.getInstance().getContext());
        }
    }

    @Override
    public void init(Context context) {
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        isInit = true;
    }

    private UserInfo getUserInfo(SDKUserInfo userInfo) {
        UserInfo info = new UserInfo();
        info.setNickName(userInfo.getNickName());
        info.setVipExpireTime(userInfo.getVipExpireTime());
        info.setUid(userInfo.getUid());
        info.setVipLevel(userInfo.getVipLevel());
        info.setIconUrl(userInfo.getIconUrl());
        info.setVipSurplus(userInfo.getVipSurplus());
        info.setVip(userInfo.isVip());
        return info;
    }

    /**
     * 生成二维码，通过扫码登录，轮询登录是否成功
     *
     * @param callback 登录二维码回调
     */
    @Override
    public void loginQRCode(final ILoginQRCodeCallback callback) {
        Log.d(TAG, "loginQRCode");
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.generateLoginQRCode(new AccountManager.GenQrloginCallBack() { //获取二维码并生成 token
                @Override
                public void onSuccess(String token, String qrcodeUrl, int expire) {
                    Log.d(TAG, "generateLoginQRCode onSuccess:" + "{token:" + token + ",qrcodeUrl:" + qrcodeUrl + ",expire:" + expire + "}");
                    if (callback != null) {
                        callback.onQRCodeSuccess(token, qrcodeUrl, expire);
                    }

                    Log.d(TAG, "startPollingCheckLogin");
                    mAccountManager.startPollingCheckLogin(token, new AccountManager.LoginCallback() {
                        @Override
                        public void onSuccess(SDKUserInfo userInfo) {
                            Log.d(TAG, "startPollingCheckLogin onSuccess:" + userInfo);
                            if (callback != null) {
                                callback.onLoginSuccess(getUserInfo(userInfo));
                            }
                            Log.d(TAG, "stopPollingCheckLogin");
                            mAccountManager.stopPollingCheckLogin(); //停止轮询
                        }

                        @Override
                        public void onFailure(String code, String msg) {
                            Log.e(TAG, "startPollingCheckLogin onFailure:" + code + "," + msg);
                            if (callback != null) {
                                callback.onLoginFail(code, msg);
                            }
                            Log.d(TAG, "stopPollingCheckLogin");
                            mAccountManager.stopPollingCheckLogin(); //停止轮询
                        }
                    });
                }

                @Override
                public void onFailure(String code, String msg) {
                    Log.e(TAG, "generateLoginQRCode onFailure:" + code + "," + msg);
                    if (callback != null) {
                        callback.onQRCodeFail(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "AccountManager is null!");
            if (callback != null) {
                callback.onQRCodeFail("loginQRCode","AccountManager is null");
            }
        }
    }

    /**
     * 生成绑定二维码，通过扫码登录和绑定，轮询登录和绑定是否成功
     *
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param callback    二维码登录回调
     */
    @Override
    public void loginBindQRCode(String accessToken, String ottVersion, String partnerEnv, ILoginQRCodeCallback callback) {
        Log.d(TAG, "loginBindQRCode: accessToken:" + accessToken + ",ottVersion:" + ottVersion + ",partnerEnv:" + partnerEnv);
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.createBindInfo(accessToken, ottVersion, partnerEnv, new AccountManager.GenQrloginCallBack() {
                @Override
                public void onSuccess(String token, String qrUrl, int expire) {
                    Log.d(TAG, "createBindInfo onSuccess:" + "{token:" + token + ",qrcodeUrl:" + qrUrl + ",expire:" + expire + "}");
                    if (callback != null) {
                        callback.onQRCodeSuccess(token, qrUrl, expire);
                    }
                    mAccountManager.stopQueryBindResult(); //停止上一次轮询

                    Log.d(TAG, "queryBindResultInterval");
                    mAccountManager.queryBindResultInterval(token, ottVersion, 2000, new AccountManager.BindQueryResultCallBack() {
                        @Override
                        public void onSuccess(SDKUserInfo userInfo) {
                            Log.d(TAG, "queryBindResultInterval onSuccess:" + userInfo);
                            if (callback != null) {
                                callback.onLoginSuccess(getUserInfo(userInfo));
                            }
                            Log.d(TAG, "stopQueryBindResult");
                            mAccountManager.stopQueryBindResult(); //停止轮询
                        }

                        @Override
                        public void onFailure(String code, String msg) {
                            if(code != null && !code.equals("P00132")){//P00132,用户还未绑定,继续轮询
                                Log.e(TAG, "queryBindResultInterval onFailure:" + code + "," + msg);
                                if (callback != null) {
                                    callback.onLoginFail(code,msg);
                                }
                                Log.e(TAG, "stopQueryBindResult");
                                mAccountManager.stopQueryBindResult(); //停止轮询
                            } else {
                                Log.d(TAG, "queryBindResultInterval continue");
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String code, String msg) {
                    Log.e(TAG, "createBindInfo onFailure:" + code + "," + msg);
                    if (callback != null) {
                        callback.onQRCodeFail(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "AccountManager is null!");
            if (callback != null) {
                callback.onQRCodeFail("loginBindQRCode","AccountManager is null");
            }
        }
    }

    /**
     * 绑定登录
     *
     * @param accessToken 合作方颁发的 access_token (车机端提供的access_token）
     * @param ottVersion  合作方客户端版本号
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param callBack    绑定登录回调
     */
    @Override
    public void loginBind(String accessToken, String ottVersion, String partnerEnv, IBindLoginCallBack callBack) {
        Log.d(TAG, "loginBind: accessToken:" + accessToken + ",ottVersion:" + ottVersion + ",partnerEnv:" + partnerEnv);
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.bindLogin(accessToken, ottVersion, partnerEnv, new AccountManager.BindLoginCallBack() {
                @Override
                public void onSuccess(SDKUserInfo userInfo) { //已绑定且登录成功，返回用户信息
                    Log.d(TAG, "bindLogin onSuccess:" + userInfo);
                    if (callBack != null) {
                        callBack.onLoginSuccess(getUserInfo(userInfo));
                    }
                }

                @Override
                public void onNoBind() { //绑定关系不存在, 走绑定二维码接口
                    Log.d(TAG, "bindLogin onNoBind");
                    if (callBack != null) {
                        callBack.onNoBind();
                    }
                }

                @Override
                public void onFailure(String code, String msg) { //绑定登录失败
                    Log.d(TAG, "bindLogin onFailure:{code:" + code + ",msg:" + msg + "}");
                    if (callBack != null) {
                        callBack.onFailure(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "loginBind error: AccountManager is null!");
            if (callBack != null) {
                callBack.onFailure("bindLogin", "AccountManager is null");
            }
        }

    }

    /**
     * 退出登录
     *
     * @param callback 退出回调
     */
    @Override
    public void logOut(final ILogoutCallback callback) {
        Log.d(TAG, "logOut");
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.logout(new AccountManager.LogoutCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "logout onSuccess");
                    if (callback != null) {
                        callback.onLogoutSuccess();
                    }
                }

                @Override
                public void onFailure(String code, String msg) {
                    Log.d(TAG, "logout onFailure:{" + code + ",msg:" + msg + "}");
                    if (callback != null) {
                        callback.onLogoutFail(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "logOut error: AccountManager is null!");
            if (callback != null) {
                callback.onLogoutFail("logOut", "AccountManager is null");
            }
        }
    }

    /**
     * 解绑
     *
     * @param accessToken 合作方颁发的 access_token
     * @param partnerEnv  后台多环境传参，没有则默认传空
     * @param callBack    回调
     */
    @Override
    public void unBind(String accessToken, String partnerEnv, IUnBindCallBack callBack) {
        Log.d(TAG, "unBind: accessToken:" + accessToken + ",partnerEnv:" + partnerEnv);
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.unBind(accessToken, partnerEnv, new AccountManager.UnBindCallBack() {
                @Override
                public void onSuccess() { //解绑成功
                    Log.d(TAG, "unBind onSuccess");
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }

                @Override
                public void onFailure(String code, String msg) { //解绑失败
                    Log.d(TAG, "unBind onFailure:" + code + "," + msg);
                    if (callBack != null) {
                        callBack.onFailure(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "unBind error: AccountManager is null!");
            if (callBack != null) {
                callBack.onFailure("unBind", "AccountManager is null");
            }
        }
    }

    /**
     * 是否登录，服务端账号是否在登录状态
     *
     * @return 是 true, 否 false
     */
    @Override
    public boolean isLogin() {
        Log.d(TAG, "isLogin");
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            return mAccountManager.isLogin();
        } else {
            Log.e(TAG, "isLogin error: AccountManager is null!");
        }
        return false;
    }

    /**
     * 更新 authCookie
     * 一次登录有效期为 90 天，该接口调用成功后，顺延 90 天；
     * 建议每次应用启动后，调用一次该接口用于账号续租；
     *
     * @param callBack 更新回调
     */
    @Override
    public void loginRenew(ILoginRenewCallBack callBack) {
        Log.d(TAG, "loginRenew");
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null && mAccountManager.isLogin()) {
            mAccountManager.loginRenew(new AccountManager.LoginRenewCallBack() {
                @Override
                public void onSuccess() {
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }

                @Override
                public void onFailure(String code, String msg) {
                    if (callBack != null) {
                        callBack.onFailure(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "loginRenew error:" + (mAccountManager == null ? "AccountManager is null!" : "AccountManager is logout!"));
            if (callBack != null) {
                callBack.onFailure("loginRenew", "AccountManager is null or logout");
            }
        }
    }

    /**
     * 更新用户信息
     *
     * @param callback 更新用户信息回调
     */
    @Override
    public void updateUserInfo(final IUserInfoCallback callback) {
        Log.d(TAG, "updateUserInfo");
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            mAccountManager.updateUserInfo(new AccountManager.LoginCallback() {
                @Override
                public void onSuccess(SDKUserInfo sdkUserInfo) {
                    Log.d(TAG, "updateUserInfo onSuccess:" + sdkUserInfo);
                    if (callback != null) {
                        callback.onUpdateUserInfo(getUserInfo(sdkUserInfo));
                    }
                }

                @Override
                public void onFailure(String code, String msg) {
                    Log.e(TAG, "updateUserInfo onFailure: code:" + code + ",msg:" + msg);
                    if (callback != null) {
                        callback.onFailuer(code, msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "updateUserInfo error: AccountManager is null!");
            if (callback != null) {
                callback.onFailuer("updateUserInfo", "AccountManager is null");
            }
        }
    }

    @Override
    public Bitmap handleBindQrCode(String qrUrl) {
        Log.d(TAG, "handleBindQrCode mAccountManager is null: " + (mAccountManager == null));
        if (mAccountManager == null) {
            mAccountManager = PlayerSdk.getInstance().getAccountManager();
        }
        if (mAccountManager != null) {
            return mAccountManager.createQRImage(qrUrl, 200, 200);
        }
        return null;
    }
}
