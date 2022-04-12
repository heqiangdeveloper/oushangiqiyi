package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MoreSelectionsTimeAdapter;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Author: zeelang
 * @Description: 时间型选集
 * @Time: 2021/11/30 0030  9:54
 * @Since: 1.0
 */
public class MoreTimeSelectionsFragment extends BasePlayFragment {
    private static final String TAG = MoreTimeSelectionsFragment.class.getSimpleName();

    private static final String VIDEO_INFO_LIST = "video_info_list";

    @BindView(R.id.video_info_more_time_selections)
    RecyclerView mMoreTimeSelectionRV;

    @BindView(R.id.video_info_more_time_container)
    FrameLayout mMoreTimeSelectionContainer;

    private MoreSelectionsTimeAdapter mTimeAdapter;// 时间型选集适配器

    @Override
    protected int setLayout() {
        return R.layout.fragment_more_time_selections;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static MoreTimeSelectionsFragment newInstance(ArrayList<VideoInfo> videoInfoList) {
        MoreTimeSelectionsFragment fragment = new MoreTimeSelectionsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VIDEO_INFO_LIST, videoInfoList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        Log.d(TAG, "initView");
        if (mMoreTimeSelectionRV.getVisibility() == View.GONE) {
            mMoreTimeSelectionRV.setVisibility(View.VISIBLE);
        }
        mMoreTimeSelectionRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (mMoreTimeSelectionRV.getItemDecorationCount() == 0) {
            mMoreTimeSelectionRV.addItemDecoration(new LineItemDecoration(15, LineItemDecoration.VERTICAL));
        }
        if (mMoreTimeSelectionContainer.getVisibility() == View.VISIBLE) {
            mMoreTimeSelectionContainer.setVisibility(View.GONE);
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            List<VideoInfo> videoInfoList = arguments.getParcelableArrayList(VIDEO_INFO_LIST);
            mTimeAdapter = new MoreSelectionsTimeAdapter(getContext(), videoInfoList);
            mMoreTimeSelectionRV.setAdapter(mTimeAdapter);
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_EPISODE_DETAIL: //剧集详情
                String desc = eventParams.getString(EventConstant.EVENT_PARAMS_VIDEO_INFO_DESC);
                CastInfo castInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_VIDEO_INFO_CAST_INFO);
//                if (mMoreTimeSelectionRV.getVisibility() == View.VISIBLE) {
//                    mMoreTimeSelectionRV.setVisibility(View.GONE);
//                }
//                if (mMoreTimeSelectionContainer.getVisibility() == View.GONE) {
//                    mMoreTimeSelectionContainer.setVisibility(View.VISIBLE);
//                }
                FragmentHelper.loadFragment(R.id.right_drawer_contaniner, getActivity().getSupportFragmentManager(), TimeSelectionsDetailsFragment.newInstance(desc, castInfo), true);
                break;
        }
    }
}
