package com.oushang.iqiyi.fragments.nesteds;

import android.os.Bundle;
import android.util.Log;

import com.oushang.iqiyi.base.BaseNestedFragment;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.MeFragment;
import com.oushang.iqiyi.fragments.MySettingFragment;
import com.oushang.lib_base.event.RefreshStksEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: zeelang
 * @Description: 我的界面
 * @Time: 2021/8/11 14:57
 * @Since: 1.0
 */
public class MeNestedFragment extends BaseNestedFragment {
    private static final String TAG = MeNestedFragment.class.getSimpleName();

    @Override
    protected void initView() {
        super.initView();
        Log.d(TAG, "initView");
        showTitleArea(false);
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        loadFragmentTransaction(getChildFragmentManager(), new MeFragment(), false);
    }

    @Override
    protected void onBack() {
        Log.d(TAG, "onBack");
        Bundle eventParams = new Bundle();
        eventParams.putString(EventConstant.EVENT_PARAMS_POP_BACK_FRAGMENT_HANDLER, MySettingFragment.class.getName());
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_POP_BACK_FRAGMENT, eventParams));
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
                showBackLayout(backName);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_SCOPE_SETTING_CHILD));
    }
}
