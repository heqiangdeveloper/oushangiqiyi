package com.oushang.iqiyi.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.IIqiyiAccountBindLoginCallback;
import com.oushang.iqiyi.IIqiyiAccountUnbindCallback;
import com.oushang.iqiyi.IIqiyiLoginStatusCallback;
import com.oushang.iqiyi.IIqiyiUser;
import com.oushang.iqiyi.bean.User;
import com.oushang.iqiyi.common.Constant;
import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.IBindLoginCallBack;
import com.oushang.lib_service.callback.IUnBindCallBack;
import com.oushang.lib_service.entries.UserInfo;
import com.oushang.lib_service.interfaces.MyAccountManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;

public class IqiyiAccountDataService extends BaseAidlService {
    private static final String TAG = "IqiyiAccountDataService";
    private final Lock mLock = new ReentrantLock();

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_ACCOUNT_MANAGER)
    MyAccountManager mMyAccountManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");

        if (mMyAccountManager == null) {
            Log.d(TAG, "from ARouter");
            mMyAccountManager = (MyAccountManager) ARouter.getInstance().build(com.oushang.lib_service.constant.Constant.PATH_SERVICE_ACCOUNT_MANAGER).navigation();
        }
        if (mMyAccountManager == null) {
            Log.d(TAG, "from MediatorServiceFactory");
            try {
                mMyAccountManager = MediatorServiceFactory.getInstance().getMyAccountManager();
            } catch (InstantiationException | IllegalAccessException e) {
                Log.d(TAG, "from MediatorServiceFactory exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * 账户绑定登录
     *
     * @param accessToken accessToken
     * @param ottVersion  ottVersion
     * @param partnerEnv  partnerEnv
     * @param callback    IIqiyiAccountBindLoginCallback
     */
    private void _accountBindLogin(String accessToken, String ottVersion, String partnerEnv, IIqiyiAccountBindLoginCallback callback) {
        mRxUtils.executeTimeOut(0, 20, 1, 1000,
                aLong -> Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()),//判断sdk是否初始化完成
                aBoolean -> aBoolean, //直到返回true时，结束轮询
                aBoolean -> {
                    if (aBoolean) { //sdk初始化已完成
                        mMyAccountManager.loginBind(accessToken, ottVersion, partnerEnv, new IBindLoginCallBack() {
                            @Override
                            public void onLoginSuccess(UserInfo userInfo) {
                                Log.d(TAG, "loginBind onLoginSuccess:" + userInfo);
                                if (callback != null) {
                                    try {
                                        User user = new User();
                                        if (null != userInfo) {
                                            user.setVip(userInfo.isVip());
                                            user.setVipExpireTime(userInfo.getVipExpireTime());
                                            user.setVipSurplus(userInfo.getVipSurplus());
                                            user.setUid(userInfo.getUid());
                                            user.setVipLevel(userInfo.getVipLevel());
                                            user.setIconUrl(userInfo.getIconUrl());
                                            user.setNickName(userInfo.getNickName());
                                        }
                                        callback.onBindLoginStatusChanged(true, user);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "loginBind onLoginSuccess RemoteException:" + Log.getStackTraceString(e));
                                    }
                                }

                            }

                            @Override
                            public void onNoBind() {
                                Log.d(TAG, "loginBind onNoBind");
                                if (callback != null) {
                                    try {
                                        callback.onBindLoginStatusChanged(false, null);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "loginBind onNoBind RemoteException:" + Log.getStackTraceString(e));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String code, String msg) {
                                Log.d(TAG, "loginBind onFailure:" + code + "," + msg);
                                if (callback != null) {
                                    try {
                                        callback.onBindLoginStatusChanged(false, null);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "loginBind onFailure RemoteException" + Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 解绑账户
     *
     * @param accessToken accessToken
     * @param partnerEnv  partnerEnv
     * @param callback    IIqiyiAccountUnbindCallback
     */
    private void _accountUnBind(String accessToken, String partnerEnv, IIqiyiAccountUnbindCallback callback) {
        mRxUtils.executeTimeOut(0, 20, 1, 1000,
                aLong -> Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()),//判断sdk是否初始化完成
                aBoolean -> aBoolean,
                aBoolean -> {
                    if (aBoolean) { //sdk初始化已完成
                        mMyAccountManager.unBind(accessToken, partnerEnv, new IUnBindCallBack() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "unBind onSuccess:");
                                SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 0);//重置为未绑定
                                if (callback != null) {
                                    try {
                                        callback.onUnbindStatusChanged(true);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "unBind onSuccess RemoteException:" + Log.getStackTraceString(e));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String code, String msg) {
                                Log.d(TAG, "unBind onFailure:" + code + "," + msg);
                                if (callback != null) {
                                    try {
                                        callback.onUnbindStatusChanged(false);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "unBind onFailure RemoteException:" + Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                });

    }

    private final IBinder mBinder = new IqiyiAccountDataService.IqiyiAccountDataServiceStub(this);
    final RemoteCallbackList<IIqiyiLoginStatusCallback> mRemoteCallbackList = new RemoteCallbackList<>();
    final RemoteCallbackList<IIqiyiAccountBindLoginCallback> mRemoteBindCallbackList = new RemoteCallbackList<>();

    /**
     * Binder接口实现类
     */
    public class IqiyiAccountDataServiceStub extends IIqiyiUser.Stub {
        private final WeakReference<IqiyiAccountDataService> mService;

        public IqiyiAccountDataServiceStub(final IqiyiAccountDataService service) {
            mService = new WeakReference<>(service);
        }

        public IqiyiAccountDataService getService() {
            return mService.get();
        }

        @Override
        public void registerCallback(IIqiyiLoginStatusCallback callback) throws RemoteException {
            LogUtils.d("registerCallback");
            if (callback != null) {
                mRemoteCallbackList.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IIqiyiLoginStatusCallback callback) throws RemoteException {
            if (callback != null) {
                mRemoteCallbackList.unregister(callback);
            }
        }

        @Override
        public void registerBindCallback(IIqiyiAccountBindLoginCallback callback) throws RemoteException {
            LogUtils.d("registerBindCallback");
            if (callback != null) {
                mRemoteBindCallbackList.register(callback);
            }
        }

        @Override
        public void unregisterBindCallback(IIqiyiAccountBindLoginCallback callback) throws RemoteException {
            if (callback != null) {
                mRemoteBindCallbackList.unregister(callback);
            }
        }

        @Override
        public void callbackFromSelf() throws RemoteException {
            remoteCallbackExecute();
        }

        @Override
        public User getUser() throws RemoteException {
            int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
            User user = null;
            if (status == 1 || mMyAccountManager.isLogin()) {//已登录
                user = new User();
                user.setNickName(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_NICKNAME, ""));
                user.setVipExpireTime(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPEXPIRETIME, ""));
                user.setUid(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_UID, ""));
                user.setVipLevel(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPLEVEL, ""));
                user.setIconUrl(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, ""));
                user.setVipSurplus(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPSURPLUS, ""));
                user.setVip(SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false));
            }
            return user;
        }

        @Override
        public boolean isBindSuccess() throws RemoteException {
            int bindStatus = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 0);
            return bindStatus == 1;
        }

        @Override
        public boolean isLogin() throws RemoteException {
            int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
            return status == 1 || mMyAccountManager.isLogin();
        }

        @Override
        public void accountBindLogin(String accessToken, String ottVersion, String partnerEnv, IIqiyiAccountBindLoginCallback callback) throws RemoteException {
            Log.d(TAG, "accountBindLogin");
            _accountBindLogin(accessToken, ottVersion, partnerEnv, callback);
        }

        @Override
        public void accountUnbind(String accessToken, String partnerEnv, IIqiyiAccountUnbindCallback callback) throws RemoteException {
            Log.d(TAG, "accountUnbind");
            _accountUnBind(accessToken, partnerEnv, callback);
        }

    }

    public void remoteCallbackExecute() {
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
        int bindStatus = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 0);
        User user = null;
        if (status == 1) {//已登录
            user = new User();
            user.setNickName(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_NICKNAME, ""));
            user.setVipExpireTime(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPEXPIRETIME, ""));
            user.setUid(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_UID, ""));
            user.setVipLevel(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPLEVEL, ""));
            user.setIconUrl(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, ""));
            user.setVipSurplus(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPSURPLUS, ""));
            user.setVip(SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false));
        }

        Log.d(TAG, "remoteCallbackExecute isLogin:" + (status == 1));
        Log.d(TAG, "remoteCallbackExecute isBind:" + (bindStatus == 1));

        remoteCallbackExecute(status == 1, user);
        remoteBindCallbackExecute(bindStatus == 1, user);
    }

    public void remoteCallbackExecute(boolean isLogin, User user) {
        mLock.lock();
        try {
            int remoteCallbackSize = mRemoteCallbackList.beginBroadcast();
            if (remoteCallbackSize > 0) {
                for (int i = 0; i < remoteCallbackSize; i++) {
                    try {
                        mRemoteCallbackList.getBroadcastItem(i).onLoginStatusChanged(isLogin, user);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mRemoteCallbackList.finishBroadcast();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    public void remoteBindCallbackExecute(boolean isBind, User user) {
        mLock.lock();
        try {
            int remoteCallbackSize = mRemoteBindCallbackList.beginBroadcast();
            if (remoteCallbackSize > 0) {
                for (int i = 0; i < remoteCallbackSize; i++) {
                    try {
                        mRemoteBindCallbackList.getBroadcastItem(i).onBindLoginStatusChanged(isBind, user);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mRemoteBindCallbackList.finishBroadcast();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }
}
