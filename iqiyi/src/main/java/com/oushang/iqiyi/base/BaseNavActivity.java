package com.oushang.iqiyi.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.UserInfo;


import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 导航activity基类
 * @Time: 2021/8/10 18:31
 * @Since: 1.0
 */
public abstract class BaseNavActivity<P extends BasePresenter> extends BaseStksActivityMVP<BaseNavPresenter> implements IBaseNavView, EventBusHelper.EventListener {
    private static final String TAG = BaseNavActivity.class.getSimpleName();
    EventBusHelper mEventBusHelper;

    @Nullable
    @BindView(R.id.nav_tab_home_page)
    public TextView mNavTabHomePage;//导航-首页

    @Nullable
    @BindView(R.id.nav_tab_classify)
    public TextView mNavTabClassify;//导航-分类

    @Nullable
    @BindView(R.id.nav_tab_my)
    public TextView mNavTabMy; //导航-我的

    @BindView(R.id.nav_home_icon)
    public ImageView mNavHomeIcon;

    @BindView(R.id.nav_classify_icon)
    public ImageView mNavClassifyIcon;

    @BindView(R.id.nav_mine_icon)
    public ImageView mNavMineIcon;

    @BindView(R.id.nav_base_fragment_container)
    FrameLayout mFragmentContainer;//fragment容器

    @BindView(R.id.root_rl)
    RelativeLayout rootRl;

    private static final String NAV_SELECT = "nav_select";

    protected int mCurrentNavSelect = -1;

    @Override
    protected int setLayout() {
        return R.layout.activity_base_nav;
    }

    @Override
    protected BaseNavPresenter createPresenter() {
        return new BaseNavPresenter();
    }

    //点击首页
    @OnClick({R.id.nav_home_layout,R.id.nav_tab_home_page})
    public void onClickHomePage() {
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5905);
        setSelected(mNavTabHomePage);
    }

    //点击分类
    @OnClick({R.id.nav_classify_layout,R.id.nav_tab_classify})
    public void onClickClassify() {
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5924);
        setSelected(mNavTabClassify);
    }

    //点击我的
    @OnClick({R.id.nav_mine_layout,R.id.nav_tab_my})
    public void onClickMy() {
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5934);
        setSelected(mNavTabMy);
    }

    //点击关闭
    @OnClick(R.id.main_close)
    public void onClickClose() {
        Log.d(TAG,"main_close clicked...");
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            Log.d(TAG, "brought_to_front finish!");
            finish();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ARouter.getInstance().inject(this);
        if(savedInstanceState != null) {
            mCurrentNavSelect = savedInstanceState.getInt(NAV_SELECT, Constant.NAV_HOME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_SELECT, mCurrentNavSelect);
    }

    protected void navSelect(int nav) {
        Log.d(TAG, "navSelect:" + nav);
        switch (nav) {
            case Constant.NAV_CLASSIFY:
                setSelected(mNavTabClassify);
                break;
            case Constant.NAV_ME:
                setSelected(mNavTabMy);
                break;
            default:
                setSelected(mNavTabHomePage);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        rootRl.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (mEventBusHelper != null) {
            mEventBusHelper.unRegister();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int type = event.getEventType();
        if (type == EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT) {
            Log.d(TAG, "load childFragment event");
            Bundle bundle = event.getEventParams();
            int selectTabId = bundle.getInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID);
            Serializable bundleSerializable = bundle.getSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS);
            Fragment fragment = null;
            if(bundleSerializable != null) {
                String bundleClass = bundleSerializable.toString();
                String className = bundleClass.substring(bundleClass.indexOf(" ") + 1);
                Log.d(TAG, "className:" + className);
                Bundle args = bundle.getBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS);
                try {
                    Class<?> clazz = Class.forName(className);
                    fragment = (Fragment) clazz.newInstance();
                    fragment.setArguments(args);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
            onLoadFragment(selectTabId, fragment);
        }else if(type == EventConstant.EVENT_TYPE_HIDE_BASENAVACTIVITY){
            rootRl.setVisibility(View.INVISIBLE);
        }
    }

    protected abstract void onLoadFragment(int selectTabId, Fragment fragment);

    protected void setSelected(TextView view) {
        if (view != null) {
            if (view == mNavTabHomePage) {
                mCurrentNavSelect = Constant.NAV_HOME;
                mNavTabMy.setSelected(false);
                mNavTabClassify.setSelected(false);
                setIconSelect(mNavHomeIcon);
            }
            if (view == mNavTabClassify) {
                mCurrentNavSelect = Constant.NAV_CLASSIFY;
                mNavTabHomePage.setSelected(false);
                mNavTabMy.setSelected(false);
                setIconSelect(mNavClassifyIcon);
            }
            if (view == mNavTabMy) {
                mCurrentNavSelect = Constant.NAV_ME;
                mNavTabHomePage.setSelected(false);
                mNavTabClassify.setSelected(false);
                setIconSelect(mNavMineIcon);
            }
            view.setSelected(true);
            if(presenter != null) {
                presenter.navSelected(view);
            }
        }
    }

    protected void jumpSelected(TextView view, Fragment fragment) {
        if (view != null) {
            if (view == mNavTabHomePage) {
                mNavTabMy.setSelected(false);
                mNavTabClassify.setSelected(false);
                setIconSelect(mNavHomeIcon);
            }
            if (view == mNavTabClassify) {
                mNavTabHomePage.setSelected(false);
                mNavTabMy.setSelected(false);
                setIconSelect(mNavClassifyIcon);
            }
            if (view == mNavTabMy) {
                mNavTabHomePage.setSelected(false);
                mNavTabClassify.setSelected(false);
                setIconSelect(mNavMineIcon);
            }
            view.setSelected(true);
            if(presenter != null) {
                presenter.jumpSelected(view, fragment);
            }
        }
    }

    protected void setIconSelect(ImageView view) {
        if (view != null) {
            if (view == mNavHomeIcon) {
                mNavClassifyIcon.setSelected(false);
                mNavMineIcon.setSelected(false);
            }
            if (view == mNavClassifyIcon) {
                mNavHomeIcon.setSelected(false);
                mNavMineIcon.setSelected(false);
            }
            if (view == mNavMineIcon) {
                mNavHomeIcon.setSelected(false);
                mNavClassifyIcon.setSelected(false);
            }
            view.setSelected(true);
        }
    }

    @Override
    public void onLoadUserInfo(UserInfo userInfo) {
//        if (userInfo != null) {
//            String url = userInfo.getIconUrl();
//            if (url != null && !url.isEmpty()) {
//                GlideUtils.load(this, Uri.parse(url), mUserPortrait);
//            } else {
//                GlideUtils.loadRes(this, R.drawable.default_avatar, mUserPortrait);
//            }
//        } else {
//            GlideUtils.loadRes(this, R.drawable.default_avatar, mUserPortrait);
//        }
    }

    protected void loadFragmentsTransaction(FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager != null && fragment != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fragmentList.isEmpty()) { //如果是fragment list空的
                transaction
                        .add(R.id.nav_base_fragment_container, fragment, fragment.getClass().getName());
            } else if (!fragment.isAdded()) { //如果fragment 未添加进fragmentManager
                for (Fragment fg : fragmentList) {
                    transaction
                            .hide(fg)
                            .setMaxLifecycle(fg, Lifecycle.State.STARTED);//不走onResume生命周期，也就是不显示
                }
                transaction.add(R.id.nav_base_fragment_container, fragment, fragment.getClass().getName());
            } else {
                for (Fragment fg : fragmentList) {
                    if (fg != fragment) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);
                    }
                }
            }
            transaction
                    .show(fragment)
                    .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    .commit();
        }
    }

    protected void loadFragmentsTransactionCommitNow(FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager != null && fragment != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fragmentList.isEmpty()) {
                transaction
                        .add(R.id.nav_base_fragment_container, fragment, fragment.getClass().getName());
            } else if (!fragmentList.contains(fragment)) {
                for (Fragment fg : fragmentList) {
                    transaction
                            .hide(fg)
                            .setMaxLifecycle(fg, Lifecycle.State.STARTED);//不走onResume生命周期，也就是不显示
                }
                transaction.add(R.id.nav_base_fragment_container, fragment, fragment.getClass().getName());
            } else {
                for (Fragment fg : fragmentList) {
                    if (fg != fragment) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);
                    }
                }
            }
            transaction
                    .show(fragment)
                    .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    .commitNow();
        }
    }

    protected void show(FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager != null && fragment != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for(Fragment fg: fragmentList) {
                if(fg != fragment) {
                    transaction.hide(fg)
                            .setMaxLifecycle(fg, Lifecycle.State.STARTED);
                }
            }
            transaction.show(fragment)
                    .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    .commit();
        }
    }


}
