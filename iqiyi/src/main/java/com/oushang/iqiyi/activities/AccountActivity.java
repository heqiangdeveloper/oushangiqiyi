package com.oushang.iqiyi.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chinatsp.drivercenter.server.AccountType;
import com.chinatsp.drivercenter.server.BindAccountInfo;
import com.chinatsp.drivercenter.server.OnGetBindAccountInfoListener;
import com.chinatsp.drivercenter.server.OnGetUserInfoListener;
import com.chinatsp.drivercenter.server.OnReadyListener;
import com.chinatsp.drivercenter.server.OnRequestListener;
import com.chinatsp.drivercenter.server.OnTokenRenewListener;
import com.chinatsp.drivercenter.server.TokenType;
import com.chinatsp.drivercenter.server.UserData;
import com.chinatsp.drivercenter.server.UserManager;
import com.oushang.iqiyi.IIqiyiUser;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.AccountPresenter;
import com.oushang.iqiyi.mvp.view.IAccountView;
import com.oushang.iqiyi.service.IqiyiAccountDataService;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.BallSpinGradientLoaderIndicator;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.utils.NetworkUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.entries.UserInfo;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @Author: zeelang
 * @Description: 账号
 * @Time: 2021/7/12 11:31
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_ACCOUNT)
public class AccountActivity extends BaseActivityMVP<AccountPresenter> implements IAccountView, EventBusHelper.EventListener {
    private static final String TAG = AccountActivity.class.getSimpleName();

    /**
     * 账号登录相关view
     */
    @BindView(R.id.account_login_in_layout)
    ConstraintLayout mLoginInLayout; //账号已登录布局

    @BindView(R.id.account_portrait)
    CircleImageView mAccountPortrait; //用户头像

    @BindView(R.id.account_user_name)
    TextView mAccountUserName; //用户名称

    @BindView(R.id.account_is_vip)
    View mIsVip; //是否是vip

    @BindView(R.id.account_vip_expire_time)
    TextView mAccountVipExpireTime;//到期时间

    @BindView(R.id.account_logout_btn)
    Button mAccountLogout; //退出登录按钮

    /**
     * 扫码二维码相关view
     */
    @BindView(R.id.account_scan_qrcode_tips)
    TextView mQRcodeTips;  //扫码提示

    @BindView(R.id.account_login_tips)
    TextView mLoginTips;  //登录提示

    @BindView(R.id.account_scan_qrcode_login_layout)
    ConstraintLayout mLoginScanQRCodeLayout; // 扫码登录二维码布局

    @BindView(R.id.qrcode_loading)
    ImageView mLoadingAnim; //二维码加载动画

    @BindView(R.id.account_login_loading)
    AVLoadingIndicatorView mLoginLoading; //加载二维码进度

    @BindView(R.id.account_login_qrcode_layout)
    ConstraintLayout mLoginQRCodeLayout; //二维码布局

    @BindView(R.id.account_login_QRCode)
    ImageView mLogingQRCode; //二维码

    @BindView(R.id.account_login_qrcode_invalid_layout)
    RelativeLayout mLoginQRCodeInvalidLayout; //二维码失效布局

    /**
     * 授权登录相关view
     */
    @BindView(R.id.vehicle_account_authorized_login_layout)
    ConstraintLayout mVehicleAccountAuthorizedLoginLayout; //授权登录布局

    @BindView(R.id.account_separator_view)
    View mAccountSeparator; //分隔线

    @BindView(R.id.vehicle_account_portrait)
    CircleImageView mVehicleAccountPortrait; //车机账号头像

    @BindView(R.id.vehicle_account_user_name)
    TextView mVehicleAccountUserName;//车机账号用户名称

    /**
     * 网络异常相关view
     */
    @BindView(R.id.network_error_layout)
    ConstraintLayout mNetworkErrorLayout; //网络异常

    private Disposable qrcodeInvalidDisposable; //登录二维码失效倒计时

    private Disposable bindQrcodeInvalidDisposable; //绑定二维码失效倒计时

    private String driveCenterAction = null;

    private IIqiyiUser mIqiyiUser = null;

    private IqiyiAccountDataService mIqiyiAccountDataService = null;

    private EventBusHelper mEventBusHelper;

    private RxUtils rxUtils;

    private volatile String mVehicleToken; //车机token

    private String versioncode; //app版本

    private int mBindQrcodeLoadFailCount = 0;

    @Override
    protected int setLayout() {
        return R.layout.activity_account;
    }

    @Override
    protected AccountPresenter createPresenter() {
        return new AccountPresenter();
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rxUtils = RxUtils.newInstance();
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();

        driveCenterAction = getIntent().getStringExtra("driveCenterAction");
        Log.d(TAG, "driveCenterAction:" + driveCenterAction);
//        if (mIqiyiUser == null) {
//            createLoginService();
//        }
        if (mIqiyiAccountDataService == null) {
            createLoginService();
        }
        versioncode = AppUtils.getVersionCode(getApplicationContext()) + ""; //获取app版本

        if (!NetworkUtils.isNetworkAvailable()) { //网络异常
            showLoadNetErrorView();
        } else if (driveCenterAction != null && driveCenterAction.equals(Constant.ACTION_BIND)) { //车机驾驶中心，立即绑定
            Log.d(TAG, "driverCenterAction");
            bindAccount();
        } else {
            int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0); //本地是否保存登录
            rxUtils.executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                if (isInit) {
                    boolean isLogin = presenter.isLogin(); //服务器端是否登录
                    Log.d(TAG, "status:" + status + ",isLogin:" + isLogin);
                    if (status == 1 && isLogin) { //如果账号已登录
                        showLoginUserInfo(getShareUserInfo(), false); //显示账号数据
                    } else { //如果账号未登录
                        loadLoginQRCode(); //加载二维码，扫码登录
                    }
                }
            });
        }

    }

    @Override
    protected void initListener() {
        super.initListener();
        UserManager.getInstance().bindUserService(getApplicationContext(), () -> {
            UserManager.getInstance().addOnTokenRenewListener(new OnTokenRenewListener() { //监听车机token刷新
                @Override
                public void onTokenRenew(String tokenType, String accessToken, String refreshToken) {
                    Log.d(TAG, "onTokenRenew: tokenType:" + tokenType + ",accessToken:" + accessToken + ",refreshToken:" + refreshToken);
                    mVehicleToken = refreshToken;
                }

                @Override
                public void onTokenRenewFailed(String tokenType, String msg) {
                    Log.e(TAG, "onTokenRenewFailed: tokenType:" + tokenType + ",msg:" + msg);
                }
            });
        });
    }

    @OnClick(R.id.account_back) //点击返回
    public void onClickBack() {
//        try {
//            mIqiyiUser.callbackFromSelf();
//        } catch (Exception e) {
//            Log.d(TAG, "onClickBack exception: " + e);
//        }
        if (mIqiyiAccountDataService != null) {
            mIqiyiAccountDataService.remoteCallbackExecute();
        }
        EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_PLAY_RECORD)); //通知更新历史记录
        finish();
    }

    @OnClick({R.id.account_login_qrcode_invalid_refresh, //二维码刷新
            R.id.account_logout_btn, //退出登录
            R.id.network_refresh_btn, //网络刷新
            R.id.vehicle_account_authorized_login_btn //授权登录
    })
    public void onClickView(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.account_login_qrcode_invalid_refresh: //点击刷新
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5908);//埋点数据
                loadLoginQRCode();
                break;
            case R.id.account_logout_btn: //退出登录
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5910);//埋点数据
                presenter.logout();//退出登录
                break;
            case R.id.network_refresh_btn: //刷新网络
                initView();
                break;
            case R.id.vehicle_account_authorized_login_btn: //授权登录
                bindAccount();
                break;
        }
    }

    /**
     * 加载登录二维码
     */
    private void loadLoginQRCode() {
        Log.d(TAG, "loadLoginQRCode");
        UserManager.getInstance().bindUserService(getApplicationContext(), () -> {
            boolean isVehicleLogin = UserManager.getInstance().isLogin();
            Log.d(TAG, "isVehicleLogin:" + isVehicleLogin);
            if (isVehicleLogin) { //车机端账号已登录
                UserManager.getInstance().renewToken(TokenType.ON_STYLE);
                mVehicleToken = UserManager.getInstance().getAccessToken(); //获取token
                versioncode = AppUtils.getVersionCode(getApplicationContext()) + ""; //获取app版本
                UserManager.getInstance().getBindAccountInfo(AccountType.AQIYI, new OnGetBindAccountInfoListener() {
                    @Override
                    public void onGetBindAccountInfoSucceed(BindAccountInfo bindAccountInfo) { //车机端已绑定
                        Log.d(TAG, "onGetBindAccountInfoSucceed:" + bindAccountInfo);
                        presenter.loadBindQRCode(bindAccountInfo != null && bindAccountInfo.getAccountType() != null, mVehicleToken, versioncode, ""); //加载绑定登录二维码
                    }

                    @Override
                    public void onGetBindAccountInfoFailed(String s) { //车机端未绑定
                        Log.e(TAG, "onGetBindAccountInfoFailed:" + s);
                        presenter.loadBindQRCode(false, mVehicleToken, versioncode, "");//加载绑定登录二维码
                    }
                });
            } else { //车机端未登录
                presenter.loadQRCode();//加载扫码登录二维码
            }
        });
    }

    /**
     * 绑定登录，（立即绑定（车机端）、授权登录）
     */
    private void bindAccount() {
        Log.d(TAG, "bindAccount");
        rxUtils.executeTimeOut(0, 20, 0, 1000, aLong -> {
            Log.d(TAG, "long:" + aLong);
            return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
        }, isInit -> isInit, isInit -> {
            if (isInit) {
                UserManager.getInstance().bindUserService(getApplicationContext(), new OnReadyListener() {
                    @Override
                    public void onReady() {
                        mVehicleToken = UserManager.getInstance().getAccessToken(); //获取token
                        String versioncode = AppUtils.getVersionCode(getApplicationContext()) + ""; //获取app版本
                        if (mVehicleToken == null) {
                            Log.e(TAG, "vehicleToken is null");
                            loadLoginQRCode(); //加载二维码
                            return;
                        }

                        Optional.ofNullable(presenter).ifPresent(accountPresenter -> {
                            accountPresenter.loadBindLogin(mVehicleToken, versioncode, "");//加载绑定登录（授权登录）
                        });
                    }
                });
            }
        });

    }

    private void createLoginService() {
        Intent mainService1 = new Intent();
        mainService1.setPackage("com.oushang.iqiyi");
        mainService1.setAction("com.oushang.iqiyi.servicelaunch.IqiyiAccDataService");
        bindService(mainService1, mConn, 0);
    }

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
//            mIqiyiUser = IIqiyiUser.Stub.asInterface(iBinder);
            IqiyiAccountDataService.IqiyiAccountDataServiceStub binder = (IqiyiAccountDataService.IqiyiAccountDataServiceStub) iBinder;
            mIqiyiAccountDataService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        driveCenterAction = intent.getStringExtra("driveCenterAction");
        Log.d(TAG, "onNewIntent driveCenterAction: " + driveCenterAction);
        initView();
    }

    /**
     * 二维码失效
     */
    @Override
    public void ShowLoginQrCodeInvalid() {
        Log.d(TAG, "ShowLoginQrCodeInvalid");
        if (qrcodeInvalidDisposable != null && !qrcodeInvalidDisposable.isDisposed()) {
            qrcodeInvalidDisposable.dispose();
        }
        if (bindQrcodeInvalidDisposable != null && !bindQrcodeInvalidDisposable.isDisposed()) {
            bindQrcodeInvalidDisposable.dispose();
        }
        if (mLoginQRCodeInvalidLayout.getVisibility() == View.GONE) {
            mLoginQRCodeInvalidLayout.setVisibility(View.VISIBLE);
            mLoginQRCodeInvalidLayout.setSelected(true);
        }
        mLoginQRCodeInvalidLayout.setAlpha(0.85f); //背景透明
        int count = mLoginQRCodeInvalidLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            mLoginQRCodeInvalidLayout.getChildAt(i).setAlpha(1.0f); //里面子view不透明
        }
    }

    /**
     * 显示二维码加载进度
     */
    @Override
    public void showLoading() {
        Log.d(TAG, "showLoading");
//        if (mLoginLoading.getVisibility() == View.GONE) { //显示加载loading
//            mLoginLoading.setVisibility(View.VISIBLE);
//        }
        if(mLoadingAnim.getVisibility() == View.GONE) { //显示加载loading动画
            mLoadingAnim.setVisibility(View.VISIBLE);
        }
        if (mLoginQRCodeLayout.getVisibility() == View.VISIBLE && mBindQrcodeLoadFailCount == 0) { //隐藏二维码布局
            mLoginQRCodeLayout.setVisibility(View.GONE);
        }

        mLoadingAnim.setImageResource(R.drawable.data_loading_anim_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        if (!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
//        mLoginLoading.setIndicator(new BallSpinGradientLoaderIndicator());
//        mLoginLoading.show();
    }

    /**
     * 隐藏二维码加载进度
     */
    @Override
    public void hideLoading() {
        Log.d(TAG, "hideLoading");
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        if (animationDrawable != null && mBindQrcodeLoadFailCount == 0) {
            animationDrawable.stop();
        }
        if (mLoadingAnim.getVisibility() == View.VISIBLE && mBindQrcodeLoadFailCount == 0) { //隐藏二维码加载动画
            mLoadingAnim.setVisibility(View.GONE);
        }
//        mLoginLoading.setVisibility(View.GONE);//隐藏二维码加载
//        mLogingQRCode.setVisibility(View.VISIBLE);//显示二维码
        if (mLoginQRCodeLayout.getVisibility() == View.GONE) { //显示二维码布局
            mLoginQRCodeLayout.setVisibility(View.VISIBLE);
        }
//        mLoginLoading.hide();
    }

    /**
     * 显示网络异常
     */
    @Override
    public void showLoadNetErrorView() {
        mNetworkErrorLayout.setVisibility(View.VISIBLE); //显示网络异常
        if (mLoginInLayout.getVisibility() == View.VISIBLE) { //若登录账号布局显示
            mLoginInLayout.setVisibility(View.GONE);//则隐藏
        }
        if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.VISIBLE) { //授权登录布局是否显示
            mVehicleAccountAuthorizedLoginLayout.setVisibility(View.GONE);//则隐藏
        }
        if (mAccountSeparator.getVisibility() == View.VISIBLE) { //分隔线显示
            mAccountSeparator.setVisibility(View.GONE); //则隐藏
        }
        if (mLoginScanQRCodeLayout.getVisibility() == View.VISIBLE) { //二维码布局显示
            mLoginScanQRCodeLayout.setVisibility(View.GONE);//则隐藏
        }

    }

    /**
     * 弹出toast
     *
     * @param msg toast信息
     */
    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, msg);
    }

    /**
     * 加载扫码登录二维码
     *
     * @param code   二维码
     * @param expire 有效期
     */
    @Override
    public void onLoadLoginQRCode(String code, int expire) {
        Log.d(TAG, "onLoadLoginQRCode: qrcode:" + code + ",expire:" + expire);
        if (mLoginInLayout.getVisibility() == View.VISIBLE) { //如果账号登录布局显示
            mLoginInLayout.setVisibility(View.GONE); //则隐藏
        }
        mBindQrcodeLoadFailCount = 0;
        hideLoading();
        mQRcodeTips.setText(R.string.account_login_qrcode_tips); //扫码提示
        mLoginTips.setVisibility(View.VISIBLE); //登录提示
        if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.VISIBLE) { //授权登录布局是否显示
            mVehicleAccountAuthorizedLoginLayout.setVisibility(View.GONE);//则隐藏
        }
        if (mAccountSeparator.getVisibility() == View.VISIBLE) { //分隔线显示
            mAccountSeparator.setVisibility(View.GONE); //则隐藏
        }

        if (mLoginQRCodeInvalidLayout.getVisibility() == View.VISIBLE) { //若二维码失效UI显示
            mLoginQRCodeInvalidLayout.setVisibility(View.GONE); //则隐藏
            mLoginQRCodeInvalidLayout.setSelected(false);
        }

        if (mLoginScanQRCodeLayout.getVisibility() == View.GONE) { //若登录二维码布局未显示
            mLoginScanQRCodeLayout.setVisibility(View.VISIBLE); //则显示
        }

        Glide2Utils.load(getApplicationContext(), Uri.parse(code), mLogingQRCode, 0, 0, 0);//显示二维码
        if (null != qrcodeInvalidDisposable && !qrcodeInvalidDisposable.isDisposed()) {
            qrcodeInvalidDisposable.dispose();
        }
        qrcodeInvalidDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.d(TAG, "long:" + aLong);
                        if (aLong >= expire) {
                            ShowLoginQrCodeInvalid();
                        }
                    }
                });
    }

    /**
     * 回调加载绑定登录二维码
     *
     * @param isBind 车机端是否绑定
     * @param code   二维码
     * @param expire 有效期
     */
    @Override
    public void onLoadBindLoginQRCode(boolean isBind, String code, int expire) {
        Log.d(TAG, "onLoadBindLoginQRCode: isBind:" + isBind + ",code:" + code + ",expire:" + expire);
        if (mLoginInLayout.getVisibility() == View.VISIBLE) { //如果账号登录布局显示
            mLoginInLayout.setVisibility(View.GONE); //则隐藏
        }
        mBindQrcodeLoadFailCount = 0;
        hideLoading();
        mQRcodeTips.setText(R.string.account_login_qrcode_tips); //扫码提示
        mLoginTips.setVisibility(View.VISIBLE); //登录提示
        if (isBind) { //如果车机端已绑定,则显示授权登录
            if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.GONE) { //授权登录布局显示
                mVehicleAccountAuthorizedLoginLayout.setVisibility(View.VISIBLE);
            }
            if (mAccountSeparator.getVisibility() == View.GONE) { //分隔线显示
                mAccountSeparator.setVisibility(View.VISIBLE);
            }
            UserManager.getInstance().getUserData(new OnGetUserInfoListener() { //获取车机账号信息
                @Override
                public void onGetUserInfoSucceed(UserData userData) { //获取车机账号信息
                    Log.d(TAG, "onGetUserInfoSucceed:" + userData);
                    String avatar = userData.getAvatar();
                    String nickname = userData.getNickname();
                    if (avatar != null && !avatar.isEmpty()) {
                        Glide2Utils.load(getApplicationContext(), Uri.parse(avatar), mVehicleAccountPortrait, R.drawable.account_portrait_test, R.drawable.account_portrait_test, 0);
                    }
                    mVehicleAccountUserName.setText(nickname); //设置车机账号用户名称
                }

                @Override
                public void onGetUserInfoFailed(String s) { //获取车机账号信息失败
                    Log.e(TAG, "onGetUserInfoFailed:" + s);
                    if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.VISIBLE) { //授权登录布局是否显示
                        mVehicleAccountAuthorizedLoginLayout.setVisibility(View.GONE);//则隐藏
                    }
                    if (mAccountSeparator.getVisibility() == View.VISIBLE) { //分隔线显示
                        mAccountSeparator.setVisibility(View.GONE); //则隐藏
                    }
                }
            });
        } else { //如果车机端未绑定，则隐藏授权登录
            if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.VISIBLE) { //授权登录布局是否显示
                mVehicleAccountAuthorizedLoginLayout.setVisibility(View.GONE);//则隐藏
            }
            if (mAccountSeparator.getVisibility() == View.VISIBLE) { //分隔线显示
                mAccountSeparator.setVisibility(View.GONE); //则隐藏
            }
        }

        if (mLoginScanQRCodeLayout.getVisibility() == View.GONE) { //若登录二维码布局未显示
            mLoginScanQRCodeLayout.setVisibility(View.VISIBLE); //则显示
        }
        if (mLoginQRCodeInvalidLayout.getVisibility() == View.VISIBLE) { //若二维码失效UI显示
            mLoginQRCodeInvalidLayout.setVisibility(View.GONE); //则隐藏
            mLoginQRCodeInvalidLayout.setSelected(false);
        }

        //显示二维码
        mLogingQRCode.setImageBitmap(presenter.getQRCodeBitmap(code));
        if (null != bindQrcodeInvalidDisposable && !bindQrcodeInvalidDisposable.isDisposed()) {
            bindQrcodeInvalidDisposable.dispose();
        }
        bindQrcodeInvalidDisposable = Observable.interval(0, 1, TimeUnit.SECONDS) //二维码失效倒计时
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.d(TAG, "long:" + aLong + ",expire:" + expire + "," + (aLong >= expire));
                        if (aLong >= expire) {
                            ShowLoginQrCodeInvalid();
                        }
                    }
                });
    }

    /**
     * 二维码加载失败
     *
     * @param code 错误码
     */
    @Override
    public void onLoadQRCodeFail(int qrCodeType, String code, String msg) {
        Log.d(TAG, "onLoadQRCodeFail: qrCodeType:" + qrCodeType + ",code:" + code + ",msg:" + msg);
//        presenter.showToast("二维码加载失败，重新加载！");
        if (qrCodeType == IAccountView.QRCODE_TYPE_NORMAL) { //普通二维码
            presenter.loadQRCode();
        } else if (qrCodeType == IAccountView.QRCODE_TYPE_BIND) { //绑定二维码
            if (mBindQrcodeLoadFailCount > 3) {
                Log.e(TAG, "bindQRCodeLoadFail count over 3");
                presenter.loadQRCode(); //加载普通二维码
            } else {
                loadLoginQRCode(); //重新加载二维码
            }
            mBindQrcodeLoadFailCount ++;
        }
    }

    /**
     * 不存在绑定
     */
    @Override
    public void onNoBind() {
        Log.d(TAG, "onNoBind");
//        presenter.showToast("授权登录失败，请扫码登录！");
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 0);//重置绑定状态
        presenter.loadBindQRCode(false, mVehicleToken, versioncode, "");//加载绑定登录二维码

        if (mIqiyiAccountDataService != null) {
            mIqiyiAccountDataService.remoteCallbackExecute();
        }
//        try {
//            if (mIqiyiUser != null) {
//                mIqiyiUser.callbackFromSelf();
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 账号登录成功
     *
     * @param loginType 登录方式
     * @param userInfo  用户信息
     */
    @Override
    public void onAccountLogin(int loginType, UserInfo userInfo) {
        Log.d(TAG, "onAccountLogin: loginType:" + loginType + ",UserInfo:" + userInfo);

        Map<String, String> statsValue = new HashMap<>();
        statsValue.put("result", "成功");
        statsValue.put("name", userInfo.getNickName());
        statsValue.put("isVip", userInfo.isVip() ? "是" : "否");
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5907, statsValue); //埋点数据

        showLoginUserInfo(userInfo, true); //显示用户信息
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 1); //设置为已登录状态
        if (loginType == LOGIN_TYPE_AUTHORIZED_BIND || loginType == LOGIN_TYPE_SCAN_BIND_QRCODE) { //若授权登录或扫码绑定登录成功
            SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 1); //设置为已绑定状态
        }

        if (mIqiyiAccountDataService != null) {
            Log.d(TAG, "mIqiyiAccountDataService remote callback");
            mIqiyiAccountDataService.remoteCallbackExecute();
        } else {
            Log.e(TAG, "mIqiyiAccountDataService is null!");
        }
//        try {
//            if (mIqiyiUser != null) {
//                mIqiyiUser.callbackFromSelf();
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        if (qrcodeInvalidDisposable != null && !qrcodeInvalidDisposable.isDisposed()) {
            qrcodeInvalidDisposable.dispose();
        }
        if (bindQrcodeInvalidDisposable != null && !bindQrcodeInvalidDisposable.isDisposed()) {
            bindQrcodeInvalidDisposable.dispose();
        }

        //静默绑定后，将用户信息传给驾驶员中心
        if ((driveCenterAction != null && driveCenterAction.equals(Constant.ACTION_BIND)) && UserManager.getInstance().isLogin()) {
            Log.d(TAG, "from driver center");
            UserManager.getInstance().saveBindAccountInfo(AccountType.AQIYI, userInfo.toString(), new OnRequestListener() {
                @Override
                public void onRequestSucceed(String s) {
                    Log.d(TAG, "saveBindAccountInfo success");
                }

                @Override
                public void onRequestSucceedFailed(String s) {
                    Log.e(TAG, "saveBindAccountInfo fail: " + s);
                }
            });
            finish();
        }
    }

    /**
     * 账号登录失败
     *
     * @param loginType 登录方式
     * @param code      错误码
     * @param msg       错误信息
     */
    @Override
    public void onAccountLoginFail(int loginType, String code, String msg) {
        Log.d(TAG, "onAccountLoginFail: loginType:" + loginType + ",code:" + code + ",msg:" + msg);
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5909); //埋点数据
        if (code != null && code.equals("P01007")) { //手机端取消
            mQRcodeTips.setText(R.string.account_login_cancel); //扫码提示
            mLoginTips.setVisibility(View.GONE); //登录提示
        } else {
            presenter.showToast("登录失败，请重新扫码登录！");
        }
        SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0); //登录状态重置
        if (loginType == IAccountView.LOGIN_TYPE_SCAN_QRCODE) { //扫码登录失败
            presenter.loadQRCode();//加载扫码登录二维码
        } else if (loginType == IAccountView.LOGIN_TYPE_SCAN_BIND_QRCODE || loginType == LOGIN_TYPE_AUTHORIZED_BIND) { //扫码绑定登录或授权登录失败
            SPUtils.putShareValue(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_BIND_STATUS, 0);//绑定状态重置
            presenter.loadBindQRCode(false, mVehicleToken, versioncode, "");//加载绑定登录二维码
        }

        if (mIqiyiAccountDataService != null) {
            mIqiyiAccountDataService.remoteCallbackExecute();
        }
//        try {
//            if (mIqiyiUser != null) {
//                mIqiyiUser.callbackFromSelf();
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 退出登录成功
     */
    @Override
    public void onLogoutSuccess() {
        loadLoginQRCode(); //退出成功后，显示登录界面
    }

    /**
     * 显示用户账号信息
     *
     * @param userInfo 用户信息
     * @param isStore  是否保存到缓存
     */
    public void showLoginUserInfo(UserInfo userInfo, boolean isStore) {
        if (isStore) { //是否缓存
            putShareUserInfo(userInfo); //保存在缓存中
        }

        if (mVehicleAccountAuthorizedLoginLayout.getVisibility() == View.VISIBLE) { //授权登录布局是否显示
            mVehicleAccountAuthorizedLoginLayout.setVisibility(View.GONE);//则隐藏
        }
        if (mAccountSeparator.getVisibility() == View.VISIBLE) { //分隔线显示
            mAccountSeparator.setVisibility(View.GONE); //则隐藏
        }

        if (mLoginScanQRCodeLayout.getVisibility() == View.VISIBLE) { //二维码布局显示
            mLoginScanQRCodeLayout.setVisibility(View.GONE);//则隐藏
        }
        if (mNetworkErrorLayout.getVisibility() == View.VISIBLE) { //网络异常布局显示
            mNetworkErrorLayout.setVisibility(View.GONE); //则隐藏
        }
        if (mLoginInLayout.getVisibility() == View.GONE) { //账号已登录界面隐藏
            mLoginInLayout.setVisibility(View.VISIBLE); //则显示
        }

        if (userInfo != null) {
            String iconUrl = userInfo.getIconUrl();  //获取用户账号头像
            String nickName = userInfo.getNickName(); //获取用户账号名称
            boolean isVip = userInfo.isVip(); //是否是vip账号
            if (isVip) {
                String time = userInfo.getVipExpireTime(); //获取vip会员到期时间
                mIsVip.setVisibility(View.VISIBLE);
                mAccountVipExpireTime.setText(String.format(getString(R.string.vip_expire_time_label), exchangeVipExpireTime(time)));
            } else {
                mIsVip.setVisibility(View.GONE);
            }
            if (iconUrl != null && !iconUrl.isEmpty()) {
                Glide2Utils.load(getApplicationContext(), Uri.parse(iconUrl), mAccountPortrait, R.drawable.account_portrait_test, R.drawable.account_portrait_test, 0);
            }
            mAccountUserName.setText(nickName);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String exchangeVipExpireTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat s1 = new SimpleDateFormat("yyyy-M-d");
            try {
                Date date = s.parse(time);
                time = s1.format(date);
            } catch (Exception e) {
                Log.e(TAG, "exchangeVipExpireTime exception: " + e);
            }
            return time;
        } else {
            return "";
        }
    }

    /**
     * 保存用户信息
     * @param userInfo 用户信息
     */
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
    }

    /**
     * 获取用户信息
     * @return UserInfo 用户信息
     */
    private UserInfo getShareUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_NICKNAME, ""));
        userInfo.setVipExpireTime(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPEXPIRETIME, ""));
        userInfo.setUid(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_UID, ""));
        userInfo.setVipLevel(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPLEVEL, ""));
        userInfo.setIconUrl(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, ""));
        userInfo.setVipSurplus(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPSURPLUS, ""));
        userInfo.setVip(SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false));
        return userInfo;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            StatusBarUtil.hideStatusBar(this); //隐藏状态栏
        }
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_FINISH_ACCOUNTACTIVITY:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mEventBusHelper) {
            mEventBusHelper.unRegister();
        }
        if (qrcodeInvalidDisposable != null && !qrcodeInvalidDisposable.isDisposed()) {
            qrcodeInvalidDisposable.dispose();
        }
        if (bindQrcodeInvalidDisposable != null && !bindQrcodeInvalidDisposable.isDisposed()) {
            bindQrcodeInvalidDisposable.dispose();
        }
        //解绑驾驶员中心服务
        UserManager.getInstance().unBindUserService(this);
//        if (null != mIqiyiUser) {
//            unbindService(mConn);
//        }
        if (mIqiyiAccountDataService != null) {
            unbindService(mConn);
        }
    }
}
