package com.oushang.iqiyi.fragments.nesteds;

import android.util.Log;

import com.oushang.iqiyi.base.BaseNestedFragment;
import com.oushang.iqiyi.fragments.HomePageFragment;

/**
 * @Author: zeelang
 * @Description: 首页界面
 * @Time: 2021/8/11 14:56
 * @Since: 1.0
 */
public class HomeNestedFragment extends BaseNestedFragment {
    private static final String TAG = HomeNestedFragment.class.getSimpleName();

    @Override
    protected void initView() {
        super.initView();
        showSearchView(true);
        showLogo(true);
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        loadFragmentTransaction(getChildFragmentManager(), new HomePageFragment(), false);
    }
}
