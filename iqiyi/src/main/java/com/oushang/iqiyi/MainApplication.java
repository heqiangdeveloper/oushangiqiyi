package com.oushang.iqiyi;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chinatsp.proxy.IVehicleNetworkCallback;
import com.chinatsp.proxy.VehicleNetworkManager;
import com.oushang.iqiyi.api.BalanceInfo;
import com.oushang.iqiyi.api.VehicleService;
import com.oushang.iqiyi.broadcasts.TrackReceiver;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.dialog.VehicleFlowDialog;
import com.oushang.iqiyi.manager.AppManager;
import com.oushang.iqiyi.mcu.CarManager;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.TimeStatistics;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.GlobalCaughtExceptionHandler;
import com.oushang.iqiyi.utils.MemoryInfoUtil;
import com.oushang.iqiyi.utils.WelcomeShowMonitor;
import com.oushang.iqiyi.voice.VoiceManager;
import com.oushang.lib_base.env.LibraryRuntimeEnv;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_base.net.RetrofitClient;
import com.oushang.lib_base.net.state.NetworkStateReceiver;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.ILoginRenewCallBack;
import com.oushang.lib_service.callback.ISDKInitializedCallBack;
import com.oushang.lib_service.callback.IUserInfoCallback;
import com.oushang.lib_service.entries.UserInfo;
import com.oushang.lib_service.interfaces.IqiyiSdkManager;
import com.oushang.lib_service.interfaces.MyAccountManager;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;
import com.oushang.voicebridge.VoiceEventManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainApplication extends Application {
    private static final String TAG = "OUSHANG_IQIYI";
    private static Context sContext;
    private volatile static int sCount = 0;
    private static Disposable countDown;
    private boolean isBackground = true;
    private static Disposable sBalanceDisposable; //车机流量

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        sContext = this.getApplicationContext();
        GlobalCaughtExceptionHandler.getInstance().init();//全局异常处理
        TrackReceiver.registerBroadcast(sContext); //注册通知赛道模式app关闭广播
        initLibrary(); //初始化library环境
        checkBalance(false); //检查爱奇艺车机流量
        initLog();
        initARouter(false);
        initService();
        initVoice();
        countDown();
        initCountDown();
        initStatistics();

        listenForForeground();//监听是否处于后台
        listenForScreenTurningOff();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext");
        TimeStatistics.beginTimeCalculate(TimeStatistics.COLD_START);//计算启动时间
    }

    private void listenForForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            //...
            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "onActivityResumed");
                if (isBackground) {
                    isBackground = false;
                    notifyForeground();
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d(TAG, "onActivityStopped");
                if (isBackground) {
                    isBackground = true;
                    notifyBackground();
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
            //...
        });
    }

    private void listenForScreenTurningOff() {
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isBackground = true;
                notifyBackground();
            }
        }, screenStateFilter);
    }


    private void notifyForeground() {
        VoiceManager.getInstance().updateStatus(false, true, VoiceEventManager.STATUS_PAUSE, null);//上传状态至语音
    }

    private void notifyBackground() {
        VoiceManager.getInstance().updateStatus(false, false, VoiceEventManager.STATUS_PAUSE, null);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "trim memory level:" + level);
        Glide2Utils.onTrimMemory(sContext, level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                Log.d(TAG, "all ui interface is hide");
                isBackground = true;
                notifyBackground();
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
                Log.e(TAG, "moderately low on memory");
                break;
            case TRIM_MEMORY_RUNNING_LOW:
                Log.e(TAG, "low on memory");
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                Log.e(TAG, "extremely low on memory");
                break;
            case TRIM_MEMORY_BACKGROUND:
                Log.e(TAG, "the process has gone on to the LRU list");
                break;
            case TRIM_MEMORY_MODERATE:
                Log.e(TAG, "the process is around the middle of the background LRU list");
                break;
            case TRIM_MEMORY_COMPLETE:
                Log.e(TAG, "the process is nearing the end of the background LRU list");
                break;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "low memory:" + dumpMemoryInfo());
        Glide2Utils.onLowMemory(sContext);
    }

    public static Context getContext() {
        return sContext;
    }

    public static int getCount() {
        return sCount;
    }

    private String dumpMemoryInfo() {
        StringBuilder memoryInfo = new StringBuilder();
        MemoryInfoUtil.dumpMemoryInfo(memoryInfo);
        return memoryInfo.toString();
    }

    private void initLog() {
        int versionCode = AppUtils.getVersionCode(getContext());
        String versionName = AppUtils.getVersionName(getContext());
        Log.d(TAG, "APP_INFO:" + "VersionCode:" + versionCode + ",VersionName:" + versionName);
        LogUtils.init(TAG);
    }

    private void initService() {
        Log.d(TAG, "initService");
        MediatorServiceFactory.getInstance().init(this);
        IqiyiSdkManager iqiyiSdkManagerService = null;
        try {
            iqiyiSdkManagerService = MediatorServiceFactory.getInstance().getIqiyiSdkManager();
            initSdk(iqiyiSdkManagerService);
            initApi(iqiyiSdkManagerService);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initLibrary() {
        LibraryRuntimeEnv.get().init(this);
        NetworkStateReceiver.getInstance().registerReceiver();//注册网络广播
    }

    private void initARouter(boolean isDebug) {
        if (isDebug) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug(); //线上要关闭
        }
        ARouter.init(this);
    }

    private void initSdk(final IqiyiSdkManager iqiyiSdkManager) {
        if (iqiyiSdkManager != null && !iqiyiSdkManager.isSdkInitialized()) {
            iqiyiSdkManager.initSdk(new ISDKInitializedCallBack() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "init iqiyi sdk onSuccess");
                    try {
                        MyAccountManager myAccountManager = MediatorServiceFactory.getInstance().getMyAccountManager();
                        myAccountManager.loginRenew(new ILoginRenewCallBack() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "update authCookie success");
                                myAccountManager.updateUserInfo(new IUserInfoCallback() {
                                    @Override
                                    public void onUpdateUserInfo(UserInfo userInfo) {
                                        Log.d(TAG, "userInfo: " + userInfo.toString());
                                        putShareUserInfo(userInfo);//存储用户信息
                                    }

                                    @Override
                                    public void onFailuer(String code, String msg) {
                                        Log.e(TAG, "userInfo: fail, code: " + code + ",msg" + msg);
                                        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
                                    }
                                });

                            }

                            @Override
                            public void onFailure(String code, String msg) {
                                Log.e(TAG, "update authCookie onFailure：code:" + code + ",msg" + msg);
                                SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
                            }
                        });

                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.d(TAG, "init iqiyi sdk onFailure");
                }
            });

        }

    }

    private void putShareUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, userInfo.getIconUrl());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_NICKNAME, userInfo.getNickName());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPEXPIRETIME, userInfo.getVipExpireTime());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_UID, userInfo.getUid());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPLEVEL, userInfo.getVipLevel());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPSURPLUS, userInfo.getVipSurplus());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, userInfo.isVip());
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 1);
    }

    private void initApi(final IqiyiSdkManager iqiyiSdkManager) {
        if (iqiyiSdkManager != null) {
            String sn = AppUtils.getDeviceID(getContext());
            Log.d(TAG, "sn:" + sn);
            Disposable disposable = executeObserver(iqiyiSdkManager.register(IqiyiApi.DEVICE_UUID, sn))
                    .subscribe(deviceRegister -> {
                        Log.d(TAG, "deviceRegister:" + deviceRegister);
                        SPUtils.putShareValue("register", "token", deviceRegister.getToken());
                        SPUtils.putShareValue("register", "expiredIn", deviceRegister.getExpiredIn());
                    }, throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                    });
        }
    }

    private <T> Observable<T> executeObserver(Observable<T> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
//                .retryWhen(new RetryWithDelay(RetryWithDelay.DEFAULT_MAX_RETRY, RetryWithDelay.DEFAULT_MAX_RETRY_DELAY_TIMES))
                .subscribeOn(Schedulers.io());
    }

    private void initVoice() {
        VoiceManager.getInstance().init(this);
        CarManager.getInstance().init(this);
        WelcomeShowMonitor.registerWelcomeShow(sContext);
    }

    //初始化数据埋点
    private void initStatistics() {
        DataStatistics.init(sContext);
    }

    public static void checkBalance(boolean isShowDialog) {
        checkBalance(isShowDialog, null);
    }

    //检查车机流量
    public synchronized static void checkBalance(boolean isShowDialog, OnBalanceListener listener) {
        Log.d(TAG, "checkBalance:" + isShowDialog + ",listener:" + listener);
        long expirationTime = SPUtils.getShareLong(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, 0L);
        long lastRequestTime = SPUtils.getShareLong(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_REQUEST_TIME, 0L);
        Log.d(TAG, "expirationTime:" + AppUtils.getTime(expirationTime) + ",lastRequestTime:" + AppUtils.getTime(lastRequestTime));
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime <= 30 * 1000) { //如果在半分钟之内请求流量接口
            Log.d(TAG, "within half a minute requested");
            if (expirationTime <= currentTime && isShowDialog) {
                Log.d(TAG, "show dialog");
                HandlerUtils.postDelayOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!VehicleFlowDialog.getInstance(MainApplication.getContext()).isShowing()) {
                            VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
                        } else {
                            Log.d(TAG, "dialog is show");
                        }
                    }
                }, 0);
            }
            if (listener != null) {
                Log.d(TAG, "onBalance");
                listener.onBalance(expirationTime);
            }
        } else {
            Log.d(TAG, "over half a minute requested");
            VehicleNetworkManager.getInstance().getToken(new IVehicleNetworkCallback() {
                @Override
                public void onCompleted(String token) { //获取token
                    Log.d(TAG, "getToken onCompleted:" + token);
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    Observable<BalanceInfo> balanceInfoObservable = RetrofitClient.newBuilder()
                            .baseUrl(VehicleService.BASE_URL)
                            .build()
                            .getApiService(VehicleService.class)
                            .getBalanceInfo(token, timestamp, "7ac3");
                    if (sBalanceDisposable != null && !sBalanceDisposable.isDisposed()) {
                        sBalanceDisposable.dispose();
                    }
                    sBalanceDisposable = balanceInfoObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(balanceInfo -> {
                                Log.d(TAG, "balance Info:" + balanceInfo);
                                long currentTime = System.currentTimeMillis();
                                SPUtils.putShareValue(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_REQUEST_TIME, currentTime);
                                if (balanceInfo.getCode() == 0) {
                                    List<BalanceInfo.Data> datas = balanceInfo.getData();
                                    datas.stream()
                                            .filter(data -> {
                                                BalanceInfo.AppInfo appInfo = data.getAppInfo();
                                                return appInfo.getCode().equals(BalanceInfo.IQIYI_APP_CODE);
                                            })
                                            .findFirst()
                                            .ifPresent(data -> {
                                                List<BalanceInfo.Balance> dataBalances = data.getBalances();
                                                if (dataBalances == null) {
                                                    Log.e(TAG, "balance data is null");
                                                    long time = AppUtils.getTime("2050-12-31 00:00:00");
                                                    SPUtils.putShareValue(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, time);
                                                    if (isShowDialog) {
                                                        VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
                                                    }
                                                    if (listener != null) {
                                                        listener.onBalance(time);
                                                    }
                                                    return;
                                                }
                                                dataBalances.stream()
                                                        .findFirst()
                                                        .ifPresent(balance -> {
                                                            String expirationTime = balance.getExpirationTime();
                                                            Log.d(TAG, "expirationTime:" + expirationTime);
                                                            long time = AppUtils.getTime(expirationTime);
                                                            long curTime = System.currentTimeMillis();
                                                            SPUtils.putShareValue(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, time);
                                                            if (time <= curTime && isShowDialog) {
                                                                VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
                                                            }
                                                            if (listener != null) {
                                                                listener.onBalance(time);
                                                            }
                                                        });
                                            });
                                } else {
                                    Log.e(TAG, "request Vehicle balance error!");
                                    long time = AppUtils.getTime("1970-12-31 00:00:00");
                                    currentTime = System.currentTimeMillis();
                                    SPUtils.putShareValue(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, time);
                                    if (time <= currentTime && isShowDialog) {
                                        VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
                                    }
                                    if (listener != null) {
                                        listener.onBalance(time);
                                    }
                                }
                            }, throwable -> Log.e(TAG, "error:" + Log.getStackTraceString(throwable)));
                }

                @Override
                public void onException(int e, String response) {
                    Log.e(TAG, "getToken onException: e:" + e + ",response:" + response);
                }
            });
        }
    }

    private void initCountDown() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.d(TAG, "onActivityCreated:" + activity.getClass().getSimpleName() + ":" + activity);
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.d(TAG, "onActivityStarted:" + activity.getClass().getSimpleName());
                sCount++;
                if (sCount > 0) {
                    if (countDown != null && !countDown.isDisposed()) {
                        Log.d(TAG, "取消后台进程退出倒计时");
                        countDown.dispose();
                    }
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                TrackReceiver.sendBroadcastCloseTrack(sContext); //通知赛道模式app关闭
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d(TAG, "onActivityStopped:" + activity.getClass().getSimpleName() + "," + activity);
                sCount--;
                if (sCount == 0) {
                    Log.d(TAG, "退出后台进程倒计时30s");
                    countDown = Observable.intervalRange(1, 30, 0, 1, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> Log.d(TAG, "long:" + aLong), Log::getStackTraceString, () -> {
                                Log.d(TAG, "退出app后台进程");
                                Glide2Utils.clearMemory(sContext);
                                NetworkStateReceiver.getInstance().unRegisterReceiver(); //取消注册网络广播
                                SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登陆状态重置
                                AppManager.getAppManager().exitApp();
                            });
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d(TAG, "onActivityDestroyed:" + activity.getClass().getSimpleName());
                AppManager.getAppManager().removeActivity(activity);
            }
        });
    }

    private void countDown() {
        Log.d(TAG, "后台进程退出倒计时30s");
        countDown = Observable.intervalRange(1, 30, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> Log.d(TAG, "long:" + aLong), Log::getStackTraceString, () -> {
                    Log.d(TAG, "退出app后台进程");
                    Glide2Utils.clearMemory(sContext);
                    NetworkStateReceiver.getInstance().unRegisterReceiver(); //取消注册网络广播
                    SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登陆状态重置
                    AppManager.getAppManager().exitApp();
                });
    }

    public static void cancelCountDown() {
        if (countDown != null && !countDown.isDisposed()) {
            Log.d(TAG, "取消后台进程退出倒计时");
            countDown.dispose();
        }
    }

    public interface OnBalanceListener {
        void onBalance(long expirationTime);
    }
}
