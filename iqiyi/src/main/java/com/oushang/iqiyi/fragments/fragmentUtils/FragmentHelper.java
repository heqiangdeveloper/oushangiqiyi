package com.oushang.iqiyi.fragments.fragmentUtils;


import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.oushang.iqiyi.R;

import java.util.Collections;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: fragment工具类
 * @Time: 2021/7/26 16:21
 * @Since: 1.0
 */
public class FragmentHelper {
    private static final String TAG = FragmentHelper.class.getSimpleName();

    /**
     * fragment回退处理
     * @param fragmentManager fragmentManager
     * @return true or false
     */
    public static boolean handlerBack(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            int backstackEnryCount = fragmentManager.getBackStackEntryCount();
            Log.d(TAG, "backstackEntryCount:" + backstackEnryCount);
            if (backstackEnryCount > 0) {
                Log.d(TAG, "popBackStack");
                fragmentManager.popBackStack();
                return true;
            }
        }
        return false;
    }

    /**
     * 退出所有
     * @param fragmentManager fragmentManager
     */
    public static void popBackAll(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            int backstackEnryCount = fragmentManager.getBackStackEntryCount();
            Log.d(TAG, "backstackEntryCount:" + backstackEnryCount);
            for(int i = 0; i < backstackEnryCount; i++) {
                Log.d(TAG, "popBackStack:" + i);
                fragmentManager.popBackStack();
            }
        }
    }


    @Deprecated
    public static void loadFragmentTransaction(@IdRes int containerViewId, FragmentManager fragmentManager, Fragment fragment, boolean addBackStack) {
        if (fragmentManager != null && fragment != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fragmentList.isEmpty()) {
                Log.d(TAG, "loadFragmentTransaction fragmentList is empty");
                transaction.add(containerViewId, fragment, fragment.getClass().getName());
            } else if (!fragmentList.contains(fragment) && fragmentManager.findFragmentByTag(fragment.getTag()) == null) {
                Log.d(TAG, "loadFragmentTransaction fragment is add");
                boolean isExisted = false;
                for (Fragment fg : fragmentList) {
//                    if (fg.getClass().getName().equals(fragment.getClass().getName())) { //不同的实例，但是相同的fragment
//                        fragmentManager.popBackStack(fg.getTag(),FragmentManager.POP_BACK_STACK_INCLUSIVE);//退出该fragment以上的所有fragment
//                        isExisted = true;
//                        break;
//                    }
                    if (fg.isAdded() && !fg.isHidden()) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);//不走onResume生命周期，也就是不显示
                    }
                }
                if (isExisted) {
                    transaction.replace(containerViewId, fragment, fragment.getClass().getName()); //如果存在相同的fragment
                } else {
                    transaction.add(containerViewId, fragment, fragment.getClass().getName()); //如果不存在相同的fragment
                }

            } else {
                Log.d(TAG, "loadFragmentTransaction fragment is not add");
                for (Fragment fg : fragmentList) {
                    if (fg.isAdded() && !fg.isHidden()) {
                        transaction
                                .hide(fg)
                                .setMaxLifecycle(fg, Lifecycle.State.STARTED);
                    }
                }
            }
            transaction.show(fragment);
            if (addBackStack) {
                transaction.addToBackStack(fragment.getTag());
            }
            if (fragment.isAdded()) {
                transaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 加载Fragment
     * @param containerViewId Fragment容器
     * @param fragmentManager Fragment管理器
     * @param fragment        加载的Fragment
     * @param addBackStack    是否加入回退栈
     */
    public static void loadFragment(@IdRes int containerViewId, FragmentManager fragmentManager, Fragment fragment, boolean addBackStack) {
        if (containerViewId !=0 && fragmentManager != null && fragment != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();//获取所有的fragment
            FragmentTransaction transaction = fragmentManager.beginTransaction(); //开始fragment事务
            if (fragmentList.isEmpty()) { //如果fragment列表为空
                Log.d(TAG, "fragmentList is empty");
                transaction.add(containerViewId, fragment, fragment.getClass().getSimpleName());//加载该fragment
            } else if(fragment.isAdded() || fragmentList.stream().anyMatch(fragment1 -> fragment1.getClass() == fragment.getClass())){ //如果已加入，且FragmentList中存在
                Log.d(TAG, "fragment is add");
                transaction.replace(containerViewId, fragment);//替换该fragment
            } else { //如果fragment未加入
                Log.d(TAG, "fragment is not add");
                transaction.add(containerViewId, fragment, fragment.getClass().getSimpleName());
            }
            for (Fragment fg : fragmentList) {
                if (fg.isAdded() && !fg.isHidden() && fg.getClass() != fragment.getClass()) {
                    transaction.hide(fg).setMaxLifecycle(fg, Lifecycle.State.STARTED); //隐藏其他fragment
                }
            }
            if (fragment.isHidden()) {
                transaction.show(fragment).setMaxLifecycle(fragment, Lifecycle.State.RESUMED);//显示该fragment
            }
            if (addBackStack) { //是否加入回退栈
                transaction.addToBackStack(fragment.getClass().getSimpleName());//是，则加入回退栈
            }
            transaction.commitAllowingStateLoss();//提交fragment事务
        }
    }
}
