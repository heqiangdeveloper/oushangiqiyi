package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chinatsp.ifly.voiceadapter.Business;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.dialog.ClearCacheDialog;
import com.oushang.iqiyi.entries.SettingEntry;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.CacheUtils;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.event.RefreshStksEvent;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.voicebridge.Util;
import com.oushang.voicebridge.VisibleDataProcessor;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @Author: zeelang
 * @Description: 我的设置界面
 * @Time: 2021/7/28 19:53
 * @Since: 1.0
 */
public class MySettingFragment extends BaseLazyFragment {
    private static final String TAG = MySettingFragment.class.getSimpleName();
    List<SettingEntry> settingEntryList = null;
    SimpleFastAdapter adapter = null;

    @BindView(R.id.my_setting_content_rv)
    RecyclerView mMySettingContentRV;

    private RxUtils rxUtils;
    private final VisibleDataProcessor visibleDataProcessor = new VisibleDataProcessor();
    @Override
    protected int setLayout() {
        return R.layout.fragment_my_setting;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public void notifyDataChanged(long size){
        for(SettingEntry item : settingEntryList){
            if(!TextUtils.isEmpty(item.getTips())){
                item.setTips(CacheUtils.byte2Format(size));
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void lazyInit() {
        long cacheSize = CacheUtils.getTotalCacheSize(getContext());
        Log.d(TAG, "cache size:" + cacheSize);
        settingEntryList = new ArrayList<>();
        rxUtils = RxUtils.newInstance();

        String[] settingItem = getResources().getStringArray(R.array.my_setting_menu);
        List<String> setting = new ArrayList<>(Arrays.asList(settingItem));

        for (String item: settingItem) {
            SettingEntry settingEntry = null;
            if (item.equals(getString(R.string.my_setting_item_clear_cache))) {
                settingEntry = new SettingEntry(item, CacheUtils.byte2Format(cacheSize));
            } else {
                settingEntry = new SettingEntry(item, "");
            }
            settingEntryList.add(settingEntry);
        }

        if(mMySettingContentRV.getItemDecorationCount() == 0){
            DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
            divider.setDrawable(getContext().getDrawable(R.drawable.my_setting_item_divider));
            mMySettingContentRV.addItemDecoration(divider);
        }
        adapter = new SimpleFastAdapter<SettingEntry>(getContext(),R.layout.item_my_setting, settingEntryList){
            @Override
            public void convert(BaseViewHolder holder, SettingEntry data, int position) {
                TextView itemName = holder.getView(R.id.my_setting_item_name);
                itemName.setText(data.getTitle());
                itemName.setClickable(true);

                TextView itemTips = holder.getView(R.id.my_setting_item_tip);
                itemTips.setText(data.getTips());
                holder.getItemView().setTag(data.getTitle());
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       String tag = (String) v.getTag();
                       if (tag.equals(mContext.getResources().getString(R.string.my_setting_item_play_doload))) {
                           DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5945);//埋点数据
                           List<String> list = new ArrayList<>();
                           list.add("自动跳过片头与片尾");

                           Bundle eventParams = new Bundle();
                           eventParams.putString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO, MySettingFragment.class.getName());
                           eventParams.putString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK, "播放与下载");
                           EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT, eventParams));

                           mMySettingContentRV.setAdapter(new SimpleFastAdapter<String>(getContext(),R.layout.item_my_setting_child,list){
                                @Override
                                public void convert(BaseViewHolder holder, String data, int position) {
                                    TextView textView = holder.getView(R.id.my_setting_child_item_name);
                                    textView.setText(data);

                                    Switch skipStartEndSw = holder.getView(R.id.skip_start_end_sw);
                                    boolean isSkipStartEnd = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE,Constant.SP_KEY_SKIP_START_END,false);
                                    skipStartEndSw.setChecked(isSkipStartEnd);
                                    skipStartEndSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){//选中时
                                                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5946, "开");//埋点数据
                                                SPUtils.putShareValue(Constant.SP_IQIYI_SPACE,Constant.SP_KEY_SKIP_START_END,true);
                                            }else {//未选中时
                                                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5946, "关");//埋点数据
                                                SPUtils.putShareValue(Constant.SP_IQIYI_SPACE,Constant.SP_KEY_SKIP_START_END,false);
                                            }
                                        }
                                    });
                                }
                            });

                           HandlerUtils.postDelay(new Runnable() {
                               @Override
                               public void run() {
                                   EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS));
                               }
                           },1000);
                       } else if (tag.equals(mContext.getResources().getString(R.string.my_setting_item_clear_cache))) {
                           ToastUtils.cancelCurrentToast();
                           new ClearCacheDialog().show(getChildFragmentManager());

                           HandlerUtils.postDelay(new Runnable() {
                               @Override
                               public void run() {
                                   EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS));

                               }
                           },1000);
                       } else if (tag.equals(mContext.getResources().getString(R.string.my_setting_item_privacy))) {
                           ARouter.getInstance().build(Constant.PATH_ACTIVITY_WEBVIEW)
                                   .withString(Constant.WEB_VIEW_URL, Constant.PRIVACY_URL)
                                   .navigation();
                       }
                    }
                });

                itemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("VisibleDataProcessor","itemName clicked..");
                        holder.getItemView().performClick();
                    }
                });
            }
        };
        mMySettingContentRV.setAdapter(adapter);
        //禁止滑动，防止刷新可见即可说时崩溃
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mMySettingContentRV.setLayoutManager(layoutManager);

        HandlerUtils.postDelay(new Runnable() {
            @Override
            public void run() {
                Log.d("VisibleDataProcessor","MySettingFragment refresh");

                EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS));

            }
        },1000);
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        if (eventType == EventConstant.EVENT_TYPE_POP_BACK_FRAGMENT) { //回退
            if (eventParams != null) {
                String fragmentName = eventParams.getString(EventConstant.EVENT_PARAMS_POP_BACK_FRAGMENT_HANDLER);
                if (fragmentName != null && fragmentName.equals(this.getClass().getName())) {
//                    initView();
                    lazyInit();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"MySetttingFragment onResume()..");
        long cacheSize = CacheUtils.getTotalCacheSize(getContext());
        Log.d(TAG, "cache size:" + cacheSize);
        notifyDataChanged(cacheSize);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("VisibleDataProcessor","mySettingFragment onDestroyView");
        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_SCOPE_MY_SETTING));
        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_SCOPE_CLEAR_CACHES));
        EventBus.getDefault().post(new RefreshStksEvent(RefreshStksEvent.EVENT_TYPE_SCOPE_MY_SETTING_ITEM));
    }
}
