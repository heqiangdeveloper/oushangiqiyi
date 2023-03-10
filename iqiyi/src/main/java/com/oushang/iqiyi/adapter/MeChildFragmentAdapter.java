package com.oushang.iqiyi.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.fragments.MyFavoriteFragment;
import com.oushang.iqiyi.fragments.MyRecordFragment;
import com.oushang.iqiyi.fragments.MySettingFragment;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/11/29 0029  10:00
 * @Since: 1.0
 */
public class MeChildFragmentAdapter extends FragmentStatePagerAdapter {

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    public MeChildFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragmentManager = fm;
        mCurTransaction = mFragmentManager.beginTransaction();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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
    public int getCount() {
        return 3;
    }

    public void hideItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
