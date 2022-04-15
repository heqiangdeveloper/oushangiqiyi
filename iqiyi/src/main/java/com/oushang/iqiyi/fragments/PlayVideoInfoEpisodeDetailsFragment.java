package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.PersonInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-剧集详情
 * @Time: 2021/7/21 18:19
 * @Since: 1.0
 */
public class PlayVideoInfoEpisodeDetailsFragment extends BasePlayFragment {
    private static final String TAG = PlayVideoInfoEpisodeDetailsFragment.class.getSimpleName();

    @BindView(R.id.episode_details_back)
    ImageView mEpisodeDetailsBack; //退出返回

    @BindView(R.id.episode_details_label)
    TextView mEpisodeDetailsLabel; //剧集详情标签

    @BindView(R.id.episode_details_desc)
    TextView mEpisodeDetailsDesc; //剧集描述

    @BindView(R.id.episode_label)
    TextView mEpisodeLabel; //剧集标签

    @BindView(R.id.episode_details_main_actor_label)
    TextView mMainActorLabel; //主演

    @BindView(R.id.episode_details_main_actor)
    RecyclerView mMainActorInfo;//主演信息

    private static final String DESC = "desc";

    private static final String CAST_INFO = "cast_info";

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_episodedetails;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static PlayVideoInfoEpisodeDetailsFragment newInstance(String desc, CastInfo castInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(DESC, desc);
        bundle.putParcelable(CAST_INFO, castInfo);
        PlayVideoInfoEpisodeDetailsFragment fragment = new PlayVideoInfoEpisodeDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //点击返回
    @OnClick(R.id.episode_details_back_layout)
    public void onClickBack() {
        FragmentHelper.handlerBack(getFragmentManager());
    }


    @Override
    protected void initView() {
        super.initView();
        mEpisodeDetailsDesc.setMovementMethod(ScrollingMovementMethod.getInstance()); //剧集简介
        if (mMainActorInfo.getItemDecorationCount() == 0) { //主演
            mMainActorInfo.addItemDecoration(new LineItemDecoration(24, LineItemDecoration.HORIZONTAL));
        }
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle arguments = getArguments();
        if (arguments != null) {
            String desc = arguments.getString(DESC);
            CastInfo castInfo = arguments.getParcelable(CAST_INFO);
            Log.d(TAG, "desc:" + desc + ",castInfo" + castInfo);
            mEpisodeDetailsDesc.setText(desc == null || desc.isEmpty() ? "无" : desc);
            if (castInfo != null) {
                //获取主演信息
                List<PersonInfo> mainActorList = castInfo.getMainActor();
                if((null != mainActorList) && (mainActorList.size() > 8)){
                    mainActorList = mainActorList.subList(0,8);
                }
                mMainActorInfo.setAdapter(new SimpleFastAdapter<PersonInfo>(getContext(), R.layout.item_main_actor, mainActorList) {
                    @Override
                    public void convert(BaseViewHolder holder, PersonInfo data, int position) {
                        if (data == null) {
                            return;
                        }
                        ImageView actorPic = holder.getView(R.id.actor_pic);
                        String cover = data.getCover();
                        if (cover != null && !cover.isEmpty()) {
                            GlideUtils.load(getContext(), Uri.parse(cover),actorPic, R.drawable.ic_albumpic_place_holder, R.drawable.ic_albumpic_place_holder);
                        } else {
                            //GlideUtils.loadRes(getContext(), R.drawable.ic_albumpic_place_holder, actorPic);
                            actorPic.setImageResource(R.drawable.ic_albumpic_place_holder);
                        }
                        TextView textView = holder.getView(R.id.actor_name);
                        textView.setText(data.getName());
                        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) { //黑夜
                            textView.setTextColor(mContext.getColor(R.color.color_skin_synopsis_detail_text));
                        } else { //白天
                            textView.setTextColor(mContext.getColor(R.color.color_skin_synopsis_detail_text_notnight));
                        }
                    }
                });
            }
        }

    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        Log.d(TAG, "onThemeChanged:" + (themeMode == ThemeManager.ThemeMode.DAY? "DAY":"NIGHT"));
        updateSkin();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        mEpisodeDetailsBack.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_skin_back_60), null));
        mEpisodeDetailsLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_detail_text), null));
        mEpisodeLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_detail_text), null));
        mEpisodeDetailsDesc.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_detail_text), null));
        mMainActorLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_detail_text), null));
        if (mMainActorInfo != null && mMainActorInfo.getAdapter() != null) {
            mMainActorInfo.getAdapter().notifyDataSetChanged();
        }
    }


}
