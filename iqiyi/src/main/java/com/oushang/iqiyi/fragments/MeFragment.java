package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MeChildFragmentAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.MePresenter;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.lib_base.event.RefreshStksEvent;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.UserInfo;
import com.oushang.lib_service.interfaces.MyAccountManager;
import com.oushang.voicebridge.Util;
//import com.oushang.voicebridge.Util;


import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author: zeelang
 * @Description: 我的界面
 * @Time: 2021/7/12 10:04
 * @Since: 1.0
 */
public class MeFragment extends BaseLazyFragment<MePresenter> {
    private static final String TAG = MeFragment.class.getSimpleName();

    @BindView(R.id.my_menu_tab)
    TabLayout mMyMenuTab;

    @BindView(R.id.my_edit)
    ImageView mMyEdit;

    @BindView(R.id.my_content_vp)
    ViewPager2 mMyContentVP;

    @BindView(R.id.my_record_edit_fun_layout)
    LinearLayout mMyEditFunLayout;

    @BindView(R.id.record_edit_btn)
    Button mMyRecordEditBtn; //编辑按钮

    @BindView(R.id.nav_user_portrait)
    CircleImageView mUserAvatar; //用户头像

    @BindView(R.id.my_setting_back)
    LinearLayout mMySettingBackLayout;//返回键

    @BindView(R.id.my_setting_back_tv)
    TextView mMySettingBackTv;//返回的标题

    @BindView(R.id.tab_menu_layout)
    ConstraintLayout mTabMenuLayout;

    @BindView(R.id.my_content_container)
    FrameLayout mContentContainer;

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_ACCOUNT_MANAGER)
    MyAccountManager mMyAccountManager;

    MeChildFragmentAdapter mChildFragmentAdapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_me;
    }

    @Override
    protected MePresenter createPresenter() {
        return new MePresenter();
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        Bundle bundle = getArguments();
        int recordType = Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY;
        if (bundle != null) {
            recordType = bundle.getInt(Constant.BUNDLE_KEY_RECORD_TYPE);
            Log.d(TAG, "recordType:" + recordType);
        }
        initTabLayout(recordType);
    }

    @Override
    protected void initData() {
        super.initData();
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS,0);
        Log.d(TAG, "status:" + status);
        if (status == 1 && mMyAccountManager.isLogin()) {
            Log.d(TAG, "account is login");
            String iconUrl = SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, "");
            Log.d(TAG, "iconUrl:" + iconUrl);
            if (iconUrl != null && !iconUrl.isEmpty()) {
                Glide2Utils.load(getContext(), Uri.parse(iconUrl), mUserAvatar, R.drawable.default_avatar, R.drawable.default_avatar,0);
            }else {
                mUserAvatar.setImageResource(R.drawable.default_avatar);
            }
        }else {
            mUserAvatar.setImageResource(R.drawable.default_avatar);
        }
        SPUtils.getSP(Constant.SP_LOGIN_SPACE).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtils.getSP(Constant.SP_LOGIN_SPACE).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged:" + key);
            if (key.equals(Constant.SP_KEY_LOGIN_STATUS)) {
                int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS,0);
                Log.d(TAG, "MeFragment status:" + status);
                if(status == 1 && mMyAccountManager.isLogin()) {
                    Log.d(TAG, "account is login");
                    String iconUrl = SPUtils.getShareString(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ICONURL, "");
                    Log.d(TAG, "MeFragment iconUrl:" + iconUrl);
                    if (iconUrl != null && !iconUrl.isEmpty()) {
                        Glide2Utils.load(getContext(), Uri.parse(iconUrl), mUserAvatar, R.drawable.default_avatar, R.drawable.default_avatar,0);
                    }else {
                        mUserAvatar.setImageResource(R.drawable.default_avatar);
                    }
                } else {
                    mUserAvatar.setImageResource(R.drawable.default_avatar);
                }
            }
        }
    };

    private void initTabLayout(int recordType) {
        Log.d(TAG, "initTabLayout");
        String[] menus = getResources().getStringArray(R.array.my_menu);
        mMyMenuTab.setTabTextColors(getResources().getColor(R.color.color_my_tab_text, null), getResources().getColor(R.color.color_my_tab_select_text, null));
        mChildFragmentAdapter = new MeChildFragmentAdapter(getChildFragmentManager());

        mMyMenuTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                statistics(tab);//埋点数据
                int position = tab.getPosition();
                Log.d(TAG, "onTabSelected position:" + position);
                Fragment fragment = (Fragment) mChildFragmentAdapter.instantiateItem(mContentContainer, position);
                mChildFragmentAdapter.setPrimaryItem(mContentContainer, position, fragment);
                mChildFragmentAdapter.finishUpdate(mContentContainer);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(TAG, "onTabUnselected position:" + position);
                Fragment fragment = (Fragment) mChildFragmentAdapter.instantiateItem(mContentContainer, position);
                mChildFragmentAdapter.destroyItem(mContentContainer, position, fragment);
                mChildFragmentAdapter.finishUpdate(mContentContainer);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(TAG, "onTabReselected position:" + position);
            }
        });

        for(int i = 0; i < menus.length; i++) {
            if(recordType == 1){//历史
                mMyMenuTab.addTab(mMyMenuTab.newTab().setText(menus[i]), i == 0);
            }else if(recordType == 2){//收藏
                mMyMenuTab.addTab(mMyMenuTab.newTab().setText(menus[i]), i == 1);
            }
        }

        //刷新可见即可说关键字
        HandlerUtils.postDelay(new Runnable() {
            @Override
            public void run() {
                Log.d("VisibleDataProcessor","MeFragemnt fresh");
                EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS));
            }
        },1000);

        LinearLayout tabStrip = (LinearLayout) mMyMenuTab.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }
    }

    private void statistics(TabLayout.Tab tab) {
        if(tab != null && tab.getText() != null) {
            String tabText = tab.getText().toString();
            if(tabText.equals(getString(R.string.my_tab_text_history))) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5935);
            } else if(tabText.equals(getString(R.string.my_tab_text_favorite))) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5940);
            } else if(tabText.equals(getString(R.string.my_tab_text_setting))) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5944);
            }
        }
    }

    private void initViewPager() {
        Log.d(TAG, "initViewPager");
        if (getActivity() == null) {
            Log.e(TAG, "FragmentActivity is null");
        }
        mMyContentVP.setUserInputEnabled(false);//禁止滑动

        mMyContentVP.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return MyRecordFragment.newInstance(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                    case 1:
                        return MyFavoriteFragment.newInstance();
                    default:
                        return new MySettingFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }


        });
    }

    private void initSelect(int recordType) {
        if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY) {
            mMyMenuTab.getTabAt(0).select();
            viewPagerSelect(0);
        } else if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE) {
            mMyMenuTab.getTabAt(1).select();
            viewPagerSelect(1);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void viewPagerSelect(int position) {
        try {
            Field field = mMyContentVP.getClass().getDeclaredField("mCurrentItem");
            field.setAccessible(true);
            field.setInt(mMyContentVP, position);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentStateAdapter stateAdapter = (FragmentStateAdapter) mMyContentVP.getAdapter();
        if (stateAdapter != null) {
            stateAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "viewpager adapter is null");
        }
        mMyContentVP.setCurrentItem(position,false);
    }

    @Override
    protected void initListener() {
//        mMyContentVP.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        if (mMyEditFunLayout.getVisibility() == View.GONE) {
//
//                        }
//                        mMyRecordEditBtn.setVisibility(View.GONE);
//                    case 1:
//                        if (mMyEditFunLayout.getVisibility() == View.VISIBLE) {
//                            mMyEditFunLayout.setVisibility(View.GONE);
//                        }
//                        mMyRecordEditBtn.setVisibility(View.GONE);
//                        break;
//                    default:
////                        mMyEdit.setVisibility(View.GONE);
//
//                        mMyRecordEditBtn.setVisibility(View.GONE);
//                        break;
//                }
//            }
//        });
    }

    @OnClick({R.id.my_setting_back,R.id.my_setting_back_tv})
    public void onClickBack() {
        mTabMenuLayout.setVisibility(View.VISIBLE);
        mMySettingBackLayout.setVisibility(View.GONE);
        Bundle eventParams = new Bundle();
        eventParams.putString(EventConstant.EVENT_PARAMS_POP_BACK_FRAGMENT_HANDLER, MySettingFragment.class.getName());
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_POP_BACK_FRAGMENT, eventParams));
    }

    @OnClick(R.id.record_edit_btn)
    public void onClickMyEdit() {
        int currentItem = mMyContentVP.getCurrentItem();
        Log.d(TAG, "currentItem:" + currentItem);
        if (!mMyRecordEditBtn.isSelected()) {
            mMyRecordEditBtn.setSelected(true);
            if (currentItem == 0) { //如果是历史记录，启用编辑时
                mMyEditFunLayout.setVisibility(View.VISIBLE); //显示编辑菜单
                mMyRecordEditBtn.setVisibility(View.GONE);
            } else { //如果是收藏记录
                mMyRecordEditBtn.setText(getString(R.string.cancel_edit));
            }
            Bundle eventParams = new Bundle();
            eventParams.putInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT_ENABLE, EventConstant.MY_RECORD_EDITED);
            EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_EDIT_ENABLE, eventParams));

        } else {
            mMyRecordEditBtn.setSelected(false);
            if (currentItem == 1) {
                mMyRecordEditBtn.setText(getString(R.string.edit));
            } else {
                mMyEditFunLayout.setVisibility(View.GONE);
            }
            //发送启用编辑事件
            Bundle eventParams = new Bundle();
            eventParams.putInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT_ENABLE, EventConstant.MY_RECORD_UNEDITED);
            EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_EDIT_ENABLE, eventParams));
        }

    }

    @OnClick({
            R.id.my_record_edit_all_select,
            R.id.my_record_edit_delete,
            R.id.my_record_edit_cancel
    })
    public void onClickEditFunLayout(View view) {
        int id = view.getId();
        Bundle eventParams = new Bundle();
        switch (id) {
            case R.id.my_record_edit_all_select: //全选
                eventParams.putInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT, EventConstant.MY_RECORD_ALL_SELECT);
                break;
            case R.id.my_record_edit_delete: //删除
                eventParams.putInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT, EventConstant.MY_RECORD_DELETE_SELECT);
                break;
            case R.id.my_record_edit_cancel: //取消
                mMyRecordEditBtn.setSelected(false);
                mMyRecordEditBtn.setVisibility(View.VISIBLE);
                mMyEditFunLayout.setVisibility(View.GONE);
                eventParams.putInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT, EventConstant.MY_RECORD_CANCEL_SELECT);
                break;
        }
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_EDIT, eventParams));
    }

    @OnClick(R.id.nav_user_portrait)
    public void onClickUserPortrait() {
        ARouter.getInstance().build(Constant.PATH_ACTIVITY_ACCOUNT).navigation();
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

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        if (eventType == EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT && eventParams != null) {
            String who = eventParams.getString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO);
            if (who.equals(MySettingFragment.class.getName())) {
                String backName = eventParams.getString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK);
                mMySettingBackTv.setText(backName);
                mMySettingBackLayout.setVisibility(View.VISIBLE);
                mTabMenuLayout.setVisibility(View.GONE);

                HandlerUtils.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS));
                    }
                },1000);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_SCOPE_SETTING_CHILD));
    }
}
