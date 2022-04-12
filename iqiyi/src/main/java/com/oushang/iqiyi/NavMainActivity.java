package com.oushang.iqiyi;

import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.oushang.iqiyi.base.BaseNavActivity;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.fragments.nesteds.ClassifyNestedFragment;
import com.oushang.iqiyi.fragments.nesteds.HomeNestedFragment;
import com.oushang.iqiyi.fragments.nesteds.MeNestedFragment;
import com.oushang.iqiyi.mvp.presenter.NavPresenter;
import com.oushang.iqiyi.statistics.TimeStatistics;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.iqiyi.utils.StatusBarUtil;

/**
 * @Author: zeelang
 * @Description: 导航Activity
 * @Time: 2021/7/5 20:42
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_NAV)
public class NavMainActivity extends BaseNavActivity<NavPresenter> {
    private static final String TAG = NavMainActivity.class.getSimpleName();

    private SparseArray<Fragment> mFragmentArray = new SparseArray<>();

    private RxUtils mRxUtils;

    @Autowired(name = Constant.NAV_MENU_SELECT)
    int mNavSelect;

    @Override
    protected void initView() {
        super.initView();
        mRxUtils = RxUtils.newInstance();
        if (mCurrentNavSelect != -1) {
            navSelect(mCurrentNavSelect);
        } else {
            navSelect(mNavSelect);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRxUtils != null) {
            mRxUtils.unDisposable();
        }
    }

    @Override
    public void onNavSelected(TextView view) {
        int id = view.getId();
        Fragment fragment = mFragmentArray.get(id);
        if (fragment == null) {
            switch (id) {
                case R.id.nav_tab_home_page:
                    fragment = new HomeNestedFragment();
                    break;
                case R.id.nav_tab_classify:
                    fragment = new ClassifyNestedFragment();
                    break;
                case R.id.nav_tab_my:
                    fragment = new MeNestedFragment();
                    break;
            }
            mFragmentArray.put(id, fragment);
        }

        loadFragmentsTransaction(getSupportFragmentManager(), fragment);
    }


    @Override
    public void onJumpSelected(TextView view, Fragment fragment) {
        int id = view.getId();
        switch (id) {
            case R.id.nav_tab_home_page:
                Log.d(TAG, "jump to home fragment");
                Fragment homeNestedFragment = mFragmentArray.get(id);//获取嵌套fragment宿主
                if (homeNestedFragment == null) { //如果嵌套fragment宿主不存在，则创建宿主
                    homeNestedFragment = new HomeNestedFragment();
                    mFragmentArray.put(id, homeNestedFragment);//加入到缓存中
                }
                if (!homeNestedFragment.isAdded()) { //如果嵌套fragment宿主未加入
                    loadFragmentsTransactionCommitNow(getSupportFragmentManager(), homeNestedFragment); //立即加载到宿主activity中
                }
                if (homeNestedFragment.isAdded()) { //如果嵌套fragment宿主已加入
                    show(getSupportFragmentManager(), homeNestedFragment);//显示嵌套fragment宿主
                }

                break;
            case R.id.nav_tab_classify:
                Log.d(TAG, "jump to classify fragment");
                Fragment classifyParent = mFragmentArray.get(id);//获取嵌套fragment宿主
                if (classifyParent == null) { //如果嵌套fragment宿主不存在，则创建宿主
                    classifyParent = new ClassifyNestedFragment();
                    mFragmentArray.put(id, classifyParent);//加入到缓存中
                }
                loadFragmentsTransactionCommitNow(getSupportFragmentManager(), classifyParent); //立即加载到宿主activity中
                if (classifyParent.isAdded() && fragment != null) { //如果嵌套fragment宿主已加入
                    FragmentManager childFragmentManager = classifyParent.getChildFragmentManager();//获取嵌套fragment子fragmentManager
                    ClassifyNestedFragment nestFragment = (ClassifyNestedFragment) classifyParent;
                    nestFragment.loadFragmentTransaction(childFragmentManager, fragment, true);//加载嵌套fragment中的子fragment
                }
                break;
            case R.id.nav_tab_my:
                Log.d(TAG, "jump to me fragment");
                Fragment meParent = mFragmentArray.get(id);
                if (meParent == null) {
                    meParent = new MeNestedFragment();
                    mFragmentArray.put(id, meParent);
                }
                loadFragmentsTransactionCommitNow(getSupportFragmentManager(), meParent);
                if (meParent.isAdded() && fragment != null) {
                    FragmentManager childFragmentManager = meParent.getChildFragmentManager();
                    MeNestedFragment nestFragment = (MeNestedFragment) meParent;
                    nestFragment.loadFragmentTransaction(childFragmentManager, fragment, false);
                }
                break;
        }

    }

    @Override
    protected void onLoadFragment(int selectTabId, Fragment fragment) {
        Log.d(TAG, "onLoadFragment:" + fragment);
        jumpSelected(findViewById(selectTabId), fragment);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged:" + hasFocus);
        if (hasFocus) {
            StatusBarUtil.showStatusBar(this);
        }
        TimeStatistics.endTimeCalculate(TimeStatistics.COLD_START);
        long time = TimeStatistics.getCalculateTime(TimeStatistics.COLD_START);
        Log.d(TAG, "start time:" + time + "ms");
    }
}
