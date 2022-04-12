package com.oushang.iqiyi.fragments.nesteds;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseNestedFragment;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.ChannelChildTagFragment;
import com.oushang.iqiyi.fragments.ChannelFragment;
import com.oushang.iqiyi.fragments.ClassifyDetailsFragment;
import com.oushang.iqiyi.fragments.ClassifyFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;


/**
 * @Author: zeelang
 * @Description: 分类界面
 * @Time: 2021/8/11 11:41
 * @Since: 1.0
 */
public class ClassifyNestedFragment extends BaseNestedFragment {
    private static final String TAG = ClassifyNestedFragment.class.getSimpleName();

    private String backTagName = "";

    @Override
    protected void initView() {
        super.initView();
//        setSearchViewHintColor(R.color.color_search_text);
        showLogo(false);
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        loadFragmentTransaction(getChildFragmentManager(), ClassifyFragment.newInstance(), false);
    }

    //分类-返回
    @Override
    protected void onBack() {
        super.onBack();
        Log.d(TAG, "onBack");
        FragmentManager fragmentManager = getChildFragmentManager();
//        int backCount = fragmentManager.getBackStackEntryCount();
//        Log.d(TAG, "backCount:" + backCount);
//        if(backCount > 0) {
//            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backCount - 1);
//            String fragmentClassName = backStackEntry.getName();
//            if (fragmentClassName != null && !fragmentClassName.isEmpty()) {
//                if (fragmentClassName.contains(ClassifyDetailsFragment.class.getName())) {
//                    showSearchView(true);
//                    showLogo(false);
//                    showUserAvatar(true);
//                    FragmentHelper.popBackAll(fragmentManager);//如果是在频道详情页，退出所有fragment
//                } else {
//                    showBackLayout(backTagName);
//                    showLogo(false);
//                }
//            }
//        }
        showSearchView(true);//显示搜索框
        showLogo(false);//不显示logo
        showUserAvatar(true);//显示用户头像
        FragmentHelper.popBackAll(fragmentManager);
    }


    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        if (eventType == EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT && eventParams != null) { //更新title区事件
            String who = eventParams.getString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO);
            if (who.equals(ClassifyDetailsFragment.class.getName())) {
                String backName = eventParams.getString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK);
                showBackLayout(backName);
                showLogo(false);
                showUserAvatar(false);
                backTagName = backName;
            } else if (who.equals(ChannelChildTagFragment.class.getName())) {
                String backName = eventParams.getString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK);
                showBackLayout(backName);
                showLogo(false);
                showUserAvatar(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
