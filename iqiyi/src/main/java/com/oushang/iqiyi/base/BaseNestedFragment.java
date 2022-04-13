package com.oushang.iqiyi.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.entries.UserInfo;
import com.oushang.lib_service.interfaces.MyAccountManager;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author: zeelang
 * @Description: 嵌套fragment基类
 * @Time: 2021/8/11 10:07
 * @Since: 1.0
 */
public abstract class BaseNestedFragment extends BaseLazyFragment {
    private static final String TAG = BaseNestedFragment.class.getSimpleName();

    @BindView(R.id.nav_user_portrait)
    CircleImageView mUserAvatar; //用户头像

    @BindView(R.id.nested_search_view)
    LinearLayout mSearchDecorateView; //搜索框

    @BindView(R.id.nested_back_layout)
    LinearLayout mBackLayout; //返回键

    @BindView(R.id.nested_back_title)
    TextView mBackTitle; //返回键右边显示的文本

    @BindView(R.id.nested_logo_icon)
    ImageView mLogoImg; //爱奇艺logo

    @BindView(R.id.nested_title_layout)
    RelativeLayout mNestedTitleArea; //标题区

    @BindView(R.id.nested_fragment_container)
    FrameLayout mNestedFragmentContainer; //嵌套fragment容器

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_ACCOUNT_MANAGER)
    MyAccountManager mMyAccountManager;

    @Override
    protected int setLayout() {
        return R.layout.fragment_base_nested;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        super.initView();
        Log.d(TAG, "initView");
        showSearchView(true);
        showLogo(true);
    }

    @Override
    protected void initData() {
        super.initData();
        SPUtils.getSP(Constant.SP_LOGIN_SPACE).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
        boolean accountStatus = mMyAccountManager.isLogin();
        Log.d(TAG, "status:" + status + ",accountStatus:" + accountStatus);
        if (status == 1 && accountStatus) {
            Log.d(TAG, "is login");
            String iconUrl = SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, "");
            Log.d(TAG, "iconUrl:" + iconUrl);
            if (iconUrl != null && !iconUrl.isEmpty()) {
                Glide2Utils.load(getContext(), Uri.parse(iconUrl), mUserAvatar, R.drawable.default_avatar, R.drawable.default_avatar, 0);
            } else {
                mUserAvatar.setImageResource(R.drawable.default_avatar);
            }
        } else {
            mUserAvatar.setImageResource(R.drawable.default_avatar);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        SPUtils.getSP(Constant.SP_LOGIN_SPACE).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged:" + key);
            if (key.equals(Constant.SP_KEY_LOGIN_STATUS)) {
                int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS,0);
                Log.d(TAG, "status:" + status);
                Bundle loginParams = new Bundle();
                loginParams.putInt(EventConstant.EVENT_PARAMS_IS_LOGIN, status);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOGIN_CHANGE, loginParams));

                if(status == 1 && mMyAccountManager.isLogin()) {
                    Log.d(TAG, "login true");
                    String iconUrl = SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, "");
                    Log.d(TAG, "iconUrl:" + iconUrl);
                    if (iconUrl != null && !iconUrl.isEmpty()) {
                        Glide2Utils.load(getContext(), Uri.parse(iconUrl), mUserAvatar, R.drawable.default_avatar, R.drawable.default_avatar, 0);
                    }else {
                        mUserAvatar.setImageResource(R.drawable.default_avatar);
                    }
                } else {
                    mUserAvatar.setImageResource(R.drawable.default_avatar);
                }
            }
        }
    };

    /**
     * 点击搜索框
     */
    @OnClick(R.id.nested_search_view)
    public void onClickSearchView() {
        ARouter.getInstance().build(Constant.PATH_ACTIVITY_SEARCH)
                .withInt(Constant.SEARCH_TYPE, Constant.SEARCH_TYPE_INPUT)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .navigation();
    }

    //点击头像
    @OnClick(R.id.nav_user_portrait)
    public void onClickUserPortrait() {
        ARouter.getInstance().build(Constant.PATH_ACTIVITY_ACCOUNT).navigation();
    }

    @OnClick(R.id.nested_back_layout)
    public void onClickBack() {
        onBack();
    }

    @OnClick(R.id.nested_logo_icon)
    public void onClickLogo() { //显示app信息
        int versionCode = AppUtils.getVersionCode(getContext());
        String versionName = AppUtils.getVersionName(getContext());
        String date = "2022-04-13";
        String appInfo = "Iqiyi appInfo:\n" + "VersionName:"+versionName + "\nVersionCode:" + versionCode + "\nTime:" + date;
        Log.d(TAG, "appInfo:" + appInfo);
        ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, appInfo);
    }

    protected void onBack() {

    }

    public void showTitleArea(boolean show) {
        if (show) {
            mNestedTitleArea.setVisibility(View.VISIBLE);
        } else {
            mNestedTitleArea.setVisibility(View.GONE);
        }
    }

    public void showSearchView(boolean show) {
        if (show) {
            mSearchDecorateView.setVisibility(View.VISIBLE);
            mBackLayout.setVisibility(View.GONE);
        } else {
            mSearchDecorateView.setVisibility(View.GONE);
            mBackLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setSearchViewHintColor(@ColorRes int color) {
        TextView searchHint = mSearchDecorateView.findViewById(R.id.search_decorate_text);
        searchHint.setHintTextColor(getResources().getColor(color, null));
    }

    public void setSearchViewHint(CharSequence text) {
        TextView searchHint = mSearchDecorateView.findViewById(R.id.search_decorate_text);
        searchHint.setHint(text);
    }

    public void setSearchIcon(@DrawableRes int resId) {
        ImageView searchIcon = mSearchDecorateView.findViewById(R.id.search_decorate_icon);
        searchIcon.setImageResource(resId);
    }

    public void showBackLayout(String title) {
        showSearchView(false);
        mBackTitle.setText(title);
    }

    public void showLogo(boolean show) {
        if (show) {
            mLogoImg.setVisibility(View.VISIBLE);
        } else {
            mLogoImg.setVisibility(View.GONE);
        }
    }

    public void showUserAvatar(boolean show) {
        if (show) {
            mUserAvatar.setVisibility(View.VISIBLE);
        } else {
            mUserAvatar.setVisibility(View.GONE);
        }
    }

    public void hideBack() {
        mBackLayout.setVisibility(View.GONE);
    }

    public void loadFragmentTransaction(FragmentManager manager, Fragment fragment, boolean isAddBackStack) {
//        loadFragmentTransaction(R.id.nested_fragment_container,manager,fragment, isAddBackStack);
        FragmentHelper.loadFragment(R.id.nested_fragment_container,manager,fragment, isAddBackStack);
    }

    private UserInfo getShareUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_NICKNAME,""));
        userInfo.setVipExpireTime(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPEXPIRETIME,""));
        userInfo.setUid(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_UID,""));
        userInfo.setVipLevel(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPLEVEL,""));
        userInfo.setIconUrl(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL,""));
        userInfo.setVipSurplus(SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_VIPSURPLUS, ""));
        userInfo.setVip(SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false));
        return userInfo;
    }
}
