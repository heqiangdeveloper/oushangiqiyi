package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.DefinitionAdapter;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.ui.ProgressButton;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.VideoRate;
import com.oushang.lib_service.utils.VideoRateUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-高清清晰度选择
 * @Time: 2021/7/22 14:33
 * @Since: 1.0
 */
public class PlayVideoInfoDefinitionFragment extends BasePlayFragment {
    private static final String TAG = PlayVideoInfoDefinitionFragment.class.getSimpleName();
    private static final String DEFINITION_ARRAY = "definition_list"; //清晰度列表
    private static final String DEFINITION_CURRENT = "definition_current";//当前的清晰度
    private int currentBitStream;

    @BindView(R.id.defination_select_back)
    LinearLayout mDefinitionSelectBack; //返回键

    @BindView(R.id.defination_list)
    RecyclerView mDefinitionList; //清晰度列表

    @BindView(R.id.defination_select_back_img)
    ImageView mBackImg; //返回箭头

    @BindView(R.id.defination_select_label)
    TextView mDefinitionLabel; //清晰度选择标签

    private DefinitionAdapter mDefinitionAdapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_defination;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static PlayVideoInfoDefinitionFragment newInstance(List<VideoRate> videoRates, int currentBitStream) {
        PlayVideoInfoDefinitionFragment fragment = new PlayVideoInfoDefinitionFragment();
        Bundle bundle = new Bundle();
        int size = videoRates.size();
        bundle.putParcelableArray(DEFINITION_ARRAY, videoRates.toArray(new VideoRate[size]));
        bundle.putInt(DEFINITION_CURRENT, currentBitStream);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView");
        int id = getId();
        if (id == R.id.right_drawer_contaniner) { //如果是侧边栏显示
            mBackImg.setVisibility(View.GONE); //隐藏返回箭头
            mDefinitionSelectBack.setClickable(false); //禁止点击
        } else {
            mBackImg.setVisibility(View.VISIBLE);
            mDefinitionSelectBack.setClickable(true);
        }

        mDefinitionList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        if (mDefinitionList.getItemDecorationCount() == 0) {
            mDefinitionList.addItemDecoration(new LineItemDecoration(47, LineItemDecoration.VERTICAL));
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            VideoRate[] rates = (VideoRate[]) arguments.getParcelableArray(DEFINITION_ARRAY);
            List<VideoRate> videoRates = Arrays.asList(rates);
            currentBitStream = arguments.getInt(DEFINITION_CURRENT);
            for (VideoRate rate: videoRates) {
                if (rate.getRt() == currentBitStream) {
                    rate.setSelected(true);
                }
            }
            mDefinitionAdapter = new DefinitionAdapter(getContext(), videoRates);
            mDefinitionList.setAdapter(mDefinitionAdapter);
        }
    }

    //点击返回键
    @OnClick(R.id.defination_select_back)
    public void onClickBack() {
        FragmentHelper.popBackAll(getFragmentManager());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_PLAY_UPDATE_DEFINITION:
                int rate = eventParams.getInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_DEFINITION);
                if (rate != currentBitStream) {
                    currentBitStream = rate;
                    if (mDefinitionAdapter!= null) {
                        mDefinitionAdapter.setSelect(rate);
                    }
                }
                break;
        }
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        Log.d(TAG,"onThemeChanged");
        updateSkin();
    }

    /**
     * 主题换肤
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        mBackImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_skin_back_60), null));
        mDefinitionLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        if (mDefinitionAdapter != null) mDefinitionAdapter.notifyDataSetChanged();
    }
}
