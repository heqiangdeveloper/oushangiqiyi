package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MoreSelectionsDigitalAdapter;
import com.oushang.iqiyi.adapter.MoreSelectionsTimeAdapter;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.presenter.PlayVideoInfoSynopsisPresenter;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoSynopsisView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.DefaultEpisode;
import com.oushang.lib_service.entries.PlayerInfo;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-剧集简介
 * @Time: 2021/7/21 10:54
 * @Since: 1.0
 */
public class PlayVideoInfoSynopsisFragment extends BasePlayFragment<PlayVideoInfoSynopsisPresenter> implements IPlayVideoInfoSynopsisView {
    private static final String TAG = PlayVideoInfoSynopsisFragment.class.getSimpleName();

    private static final String BUNDLE_ARGS_VIDEOINFO = "synopsis_video_info";

    private static final String BUNDLE_ARGS_ALBUMID = "albumid";

    private static final String BUNDLE_ARGS_YEAR = "year";

    private volatile VideoInfo mCurrentVideoInfo; //当前播放的视频信息

    private int order;//当前集数

    private long mAlbumId;

    private int mYear;

    @BindView(R.id.video_info_root)
    ConstraintLayout mVideoInfoRootLayout;//根布局

    @BindView(R.id.video_info_synopsis_label)
    TextView mVideoInfoSynopsisLabel;//剧集简介标签

    @BindView(R.id.video_info_related_recommend_layout)
    LinearLayout mRelateRecommendMore; //相关推荐布局

    @BindView(R.id.video_info_related_recommend_label)
    TextView mRelateRecommentLabel; //相关推荐标签

    @BindView(R.id.video_info_related_recommend_more)
    ImageView mRelateRecommendImg; //相关推荐图标

    @BindView(R.id.video_into_albumpic)
    ImageView mAlbumPiciv; //剧集封面图

    @BindView(R.id.video_info_name)
    TextView mVideoInfoName; //剧集名称

    @BindView(R.id.video_info_desc)
    TextView mVideoInfoDesc;  //剧集简介内容

    @BindView(R.id.video_info_details)
    TextView mVideoInfoDetails; //剧集详情

    @BindView(R.id.video_info_more_selections)
    RecyclerView mVideoInfoMoreSelections; //更多选集

    @BindView(R.id.more_selections_layout)
    ConstraintLayout mMoreSelectionsLayout; //更多选集布局

    @BindView(R.id.video_info_more_selections_label)
    TextView mMoreSelectionsLabel; //更多选集标签

    @BindView(R.id.video_info_more_selections_img)
    ImageView mMoreSelectionsImg; //更多选集图标

    @BindView(R.id.more_selections_loading)
    ProgressBar mProgressBtn;//加载进度条

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager; //播放列表管理

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    MoreSelectionsDigitalAdapter mMoreSelectionsDigitalAdapter; //数字型适配器

    MoreSelectionsTimeAdapter mMoreSelectionsTimeAdapter; //时间型适配器

    private volatile CastInfo mCastInfo; //演员信息

    private int mCurrentPartitionPos = -1;//分集位置，默认无分集位置

    private int mMoreSelectionType = 0; //剧集类型， 1 数字型， 2 时间型

    long oldAlbumId = 0;

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo;
    }

    @Override
    protected PlayVideoInfoSynopsisPresenter createPresenter() {
        return new PlayVideoInfoSynopsisPresenter();
    }

    public static PlayVideoInfoSynopsisFragment newInstance(VideoInfo videoInfo) {
        PlayVideoInfoSynopsisFragment fragment = new PlayVideoInfoSynopsisFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ARGS_VIDEOINFO, videoInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlayVideoInfoSynopsisFragment newInstance(PlayerInfo playerInfo) {
        PlayVideoInfoSynopsisFragment fragment = new PlayVideoInfoSynopsisFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ARGS_VIDEOINFO, playerInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlayVideoInfoSynopsisFragment newInstance(long albumId, int year) {
        PlayVideoInfoSynopsisFragment fragment = new PlayVideoInfoSynopsisFragment();
        Bundle args = new Bundle();
        args.putLong(BUNDLE_ARGS_ALBUMID, albumId);
        args.putInt(BUNDLE_ARGS_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCurrentVideoInfo = arguments.getParcelable(BUNDLE_ARGS_VIDEOINFO);
            if (mCurrentVideoInfo.isAlbum()) { //如果是剧集
                oldAlbumId = mCurrentVideoInfo.getQipuId(); //获取剧集id, qipuId是剧集id
            } else { //如果是视频
                oldAlbumId = mCurrentVideoInfo.getAlbumId(); //获取剧集id, albumId是剧集id
            }
            order = mCurrentVideoInfo.getOrder();
            Log.d(TAG, "albumId:" + oldAlbumId + ",order:" + order);
            if (oldAlbumId != 0) {
                presenter.loadPartitionSelection(mPlayListManager.getPartitionPos(mCurrentVideoInfo));
                long qipuId = mCurrentVideoInfo.getQipuId();
                if(qipuId != 0) {
                    presenter.loadVideoInfo(qipuId);
                } else {
                    presenter.loadVideoInfo(oldAlbumId);
                }
            } else {
                if (mMoreSelectionsLayout.getVisibility() == View.VISIBLE) { //如果显示选集，
                    mMoreSelectionsLayout.setVisibility(View.GONE);
                }
            }
            mCastInfo = mCurrentVideoInfo.getCast();
        }
    }

    @Override
    protected void initView() {
        if (mCurrentVideoInfo != null && mCurrentVideoInfo.isVideo()) { //如果是视频
            showSynopsisInfo(mCurrentVideoInfo); //显示剧集简介
        }
    }

    /**
     * 更新视频信息
     * @param videoInfo 视频信息
     */
    private synchronized void updateVideoInfo(VideoInfo videoInfo) {
        if (mCurrentVideoInfo != null && videoInfo != null) {
            int partitionPos = mPlayListManager.getPartitionPos(videoInfo);
            Log.d(TAG, "partitionPos:" + partitionPos + ",mCurrentPartitionPos:" + mCurrentPartitionPos);
            if(mCurrentPartitionPos == -1 || partitionPos != mCurrentPartitionPos) {
                mCurrentPartitionPos = partitionPos;
                mPlayListManager.updatePartitionPos(partitionPos);
                Optional.ofNullable(presenter).ifPresent(playVideoInfoSynopsisPresenter -> playVideoInfoSynopsisPresenter.loadPartitionSelection(mCurrentPartitionPos));
            }

            long oldAlbumId = mCurrentVideoInfo.getAlbumId(); //获取旧视频剧集id
            if (mCurrentVideoInfo.isAlbum()) { //如果旧视频是专辑详情视频
                oldAlbumId = mCurrentVideoInfo.getQipuId(); //重新获取旧视频剧集id
            }
            long newAlbumId = videoInfo.getAlbumId(); //获取当前播放视频剧集id
            if (videoInfo.isAlbum()) { //如果当前播放视频是专辑详情视频
                newAlbumId = videoInfo.getQipuId(); //重新获取当前播放视频剧集id
            }
            Log.d(TAG, "oldAlbumId:" + oldAlbumId + ",newAlbumId:" + newAlbumId);
            if (oldAlbumId != newAlbumId) { //如果是新剧集时
                mCastInfo = videoInfo.getCast(); //更新主演信息
            }
        }
        mCurrentVideoInfo = videoInfo;

        if (videoInfo != null && videoInfo.isVideo()) { //如果是视频
            showSynopsisInfo(videoInfo); //显示剧集简介
        } else if (videoInfo != null && videoInfo.isAlbum()) { //如果是专辑/剧集
            Optional.ofNullable(presenter).ifPresent(playVideoInfoSynopsisPresenter -> playVideoInfoSynopsisPresenter.loadVideoInfo(videoInfo.getQipuId()));//加载剧集详情信息
        }
        if (videoInfo != null && mMoreSelectionsDigitalAdapter != null && videoInfo.getSourceCode() == 0) { //如果是数字型
            mMoreSelectionsDigitalAdapter.setSelected(videoInfo); //更新播放位置
        }
    }


    /**
     * 显示剧集简介
     */
    private synchronized void showSynopsisInfo(VideoInfo videoInfo) {
        Log.d(TAG, "showSynopsisInfo");
        if (videoInfo != null) {
            String videoName = "";
            if(videoInfo.isAlbum()) {
                DefaultEpisode defaultEpi = videoInfo.getDefaultEpi();
                if(defaultEpi != null) {
                    videoName = defaultEpi.getName();
                }
            } else {
                videoName = videoInfo.getName();
            }
//            String name = videoInfo.getName();
            String desc = videoInfo.getDesc();
            String albumPic = videoInfo.getAlbumPic();
            if (mVideoInfoName != null) { //显示视频名称
                mVideoInfoName.setText(videoName);
            }
            if (albumPic != null && !albumPic.isEmpty() && mAlbumPiciv != null) { // 显示剧集海报
                String newUrl = AppUtils.appendImageUrl(albumPic, "_195_260");
                GlideUtils.loadImageWithRoundedCorners(getContext(), Uri.parse(newUrl), mAlbumPiciv, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 8);
            }
            if (mVideoInfoDesc != null) { //显示剧集简介
                mVideoInfoDesc.setText(desc == null || desc.isEmpty() ? "无" : desc);
            }
        }
    }

    private void showPlayerInfo(PlayerInfo playerInfo) {
        if(playerInfo != null) {
            String name = playerInfo.getTv_title();
            String desc = playerInfo.getTv_desc();
            String albumPic = playerInfo.getTv_img();

            if(albumPic == null || albumPic.isEmpty()) {
                albumPic = playerInfo.getAl_img();
            }

            if (mVideoInfoName != null) { //显示视频名称
                mVideoInfoName.setText(name);
            }
            if (albumPic != null && !albumPic.isEmpty() && mAlbumPiciv != null) { // 显示剧集海报
                String newUrl = AppUtils.appendImageUrl(albumPic, "_195_260");
                GlideUtils.loadImageWithRoundedCorners(getContext(), Uri.parse(newUrl), mAlbumPiciv, R.drawable.ic_albumpic_place_holder, R.drawable.ic_albumpic_place_holder, 8);
            }
            if (mVideoInfoDesc != null) { //显示剧集简介
                mVideoInfoDesc.setText(desc == null || desc.isEmpty() ? "无" : desc);
            }
        }
    }

    //点击事件
    @OnClick({R.id.video_info_related_recommend_layout, R.id.video_info_details, R.id.video_info_more_select_layout})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.video_info_related_recommend_layout: //相关推荐
                Map<String, String> statsValue = new HashMap<>();
                statsValue.put("item", mCurrentVideoInfo.getName());
                statsValue.put("plate", mCurrentVideoInfo.getChnName());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5919, statsValue);
                long qipuId = mCurrentVideoInfo.getQipuId();
                if (qipuId == 0) {
                    qipuId = mCurrentVideoInfo.getDefaultEpi().getQipuId();
                }
                FragmentHelper.loadFragment(R.id.video_info_container, getFragmentManager(), PlayVideoInfoRelatedRecommendFragment.newInstance(qipuId), true);
                break;
            case R.id.video_info_details: //剧集详情
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5921);
                String desc = mCurrentVideoInfo.getDesc();
                FragmentHelper.loadFragment(R.id.video_info_container, getFragmentManager(), PlayVideoInfoEpisodeDetailsFragment.newInstance(desc, mCastInfo), true);
                break;
            case R.id.video_info_more_select_layout: //更多选集
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5915);
                if(mCurrentVideoInfo != null) {
                    long albumId = mCurrentVideoInfo.getAlbumId();
                    if (mCurrentVideoInfo.isAlbum()) {
                        albumId = mCurrentVideoInfo.getQipuId();
                    }
                    Log.d(TAG, "albumId:" + albumId);
                    FragmentHelper.loadFragment(R.id.video_info_container, getFragmentManager(), PlayVideoInfoMoreSelectionsFragment.newInstance(albumId), true);
                }
                break;
        }

    }

    /**
     * 显示数字型选集
     *
     * @param videoInfoList 视频信息
     */
    private void showDigitalTypeSelection(List<VideoInfo> videoInfoList) {
        Log.d(TAG, "showDigitalTypeSelection");
        if (videoInfoList == null || videoInfoList.isEmpty()) {
            Log.e(TAG, "videoInfoList is null");
            return;
        }
        List<SelectionEntry> selectionEntryList = new ArrayList<>();
        for (VideoInfo videoInfo : videoInfoList) {
            selectionEntryList.add(new SelectionEntry(videoInfo, videoInfo == mCurrentVideoInfo));
        }

        mVideoInfoMoreSelections.setLayoutManager(new GridLayoutManager(getContext(), 5));
        if (mVideoInfoMoreSelections.getItemDecorationCount() == 0) {
            mVideoInfoMoreSelections.addItemDecoration(new GridItemDecoration(25, GridItemDecoration.HORIZONTAL));
            mVideoInfoMoreSelections.addItemDecoration(new GridItemDecoration(29, GridItemDecoration.VERTICAL));
        }

        if (mMoreSelectionsDigitalAdapter != null) {
            Log.d(TAG, "updateData");
            mMoreSelectionsDigitalAdapter.updateData(selectionEntryList);
            mMoreSelectionsDigitalAdapter.notifyDataSetChanged();
        } else {
            mMoreSelectionsDigitalAdapter = new MoreSelectionsDigitalAdapter(getContext(), selectionEntryList);
            mVideoInfoMoreSelections.setAdapter(mMoreSelectionsDigitalAdapter);
        }

        if (mCurrentVideoInfo != null) {
            order = mCurrentVideoInfo.getOrder();
        }
        if(order == 0) {
            order = 1;
        }
        Log.d(TAG, "select order:" + order);
        mMoreSelectionsDigitalAdapter.setSelectOrder(order);
    }

    /**
     * 显示时间型选集
     *
     * @param videoInfoList 视频信息列表
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("DiscouragedPrivateApi")
    private void showTimeTypeSelection(List<VideoInfo> videoInfoList) {
        Log.d(TAG, "showTimeTypeSelection");
        if (videoInfoList == null || videoInfoList.isEmpty()) {
            Log.e(TAG, "videoInfoList is null");
            return;
        }
        mVideoInfoMoreSelections.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mVideoInfoMoreSelections.getItemDecorationCount() == 0) {
            mVideoInfoMoreSelections.addItemDecoration(new LineItemDecoration(15, GridItemDecoration.VERTICAL));
        }

        mMoreSelectionsTimeAdapter = new MoreSelectionsTimeAdapter(getContext(), videoInfoList);
        mVideoInfoMoreSelections.setAdapter(mMoreSelectionsTimeAdapter);
    }

    /**
     * 加载视频信息(剧集简介）
     * @param videoInfo 视频信息
     */
    @Override
    public void onLoadVideoInfo(VideoInfo videoInfo) {
        Log.d(TAG, "onLoadVideoInfo:" + videoInfo);
        mCastInfo = mCurrentVideoInfo.getCast(); //重新获取一份主演信息
        showSynopsisInfo(videoInfo); //显示剧集简介
    }

    /**
     * 加载选集
     * @param videoInfoList 选集视频列表
     * @param type 视频类型
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onLoadVideoInfoList(List<VideoInfo> videoInfoList, int type) {
        if (mMoreSelectionsLayout.getVisibility() == View.GONE) { //显示选集
            mMoreSelectionsLayout.setVisibility(View.VISIBLE);
        }
        mMoreSelectionType = type;
        if (type == TYPE_DIGITAL) { //数字型
            showDigitalTypeSelection(videoInfoList); //显示数字型选集
        } else if (type == TYPE_TIME) { //时间型
            showTimeTypeSelection(videoInfoList); //显示时间型选集
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
        Log.d(TAG, "showLoading");
        mProgressBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        Log.d(TAG, "hideLoading");
        mProgressBtn.setVisibility(View.GONE);
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_EPISODE_LOAD_COMPLETE: //剧集加载完成
                Log.d(TAG, "event type episode load complete: " + mMoreSelectionType);
                if (mMoreSelectionType == 0) { //如果未加载出数据
                    Optional.ofNullable(presenter).ifPresent(presenter -> presenter.loadPartitionSelection(mPlayListManager.getCurrentPartitionPos()));
                } else { //如果已加载的是旧剧集
                    long new_albumId = mPlayListManager.getEpisodeInfo().getAlbumId();
                    Log.d(TAG, "new_albumId:" + new_albumId + ",old_albumId:" + oldAlbumId);
                    if(new_albumId != oldAlbumId) {
                        oldAlbumId = new_albumId;
                        Optional.ofNullable(presenter).ifPresent(presenter -> presenter.loadPartitionSelection(mPlayListManager.getCurrentPartitionPos()));
                    }
                }
                break;

            case EventConstant.EVENT_TYPE_EPISODE_DETAIL:
                Log.d(TAG, "load episode detail");
                String desc = eventParams.getString(EventConstant.EVENT_PARAMS_VIDEO_INFO_DESC);
                CastInfo castInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_VIDEO_INFO_CAST_INFO);
                FragmentHelper.loadFragment(R.id.video_info_container, getFragmentManager(), PlayVideoInfoEpisodeDetailsFragment.newInstance(desc, castInfo), true);
                break;
            case EventConstant.EVENT_TYPE_UPDATE_PLAY_VIDEO_INFO:
                Log.d(TAG, "update video info");
                VideoInfo videoInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_PLAY_VIDEO_INFO);
                updateVideoInfo(videoInfo);
                break;
//            case EventConstant.EVENT_TYPE_UPDATE_REMEMBER_PLAY_VIDEO_INFO:
//                Log.d(TAG, "update remember video info");
//                VideoInfo videoInfo2 = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_REMEMBER_PLAY_VIDEO_INFO);
//                updateVideoInfo(videoInfo2);
//                break;
            case EventConstant.EVENT_TYPE_PLAY_SELECT_RELATED: //相关推荐
                Log.d(TAG, "update related video info");
                if (eventParams != null) {
                    VideoInfo videoInfo3 = eventParams.getParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_RELATE);
                    updateVideoInfo(videoInfo3);
                }
                break;
        }
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        Log.d(TAG, themeMode == ThemeManager.ThemeMode.DAY? "DAY":"NIGHT");
        updateSkin(themeMode);
    }

    /**
     * 更换皮肤
     * @param themeMode 黑夜/白天模式
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin(ThemeManager.ThemeMode themeMode) {
        //更新背景
        mVideoInfoRootLayout.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        //剧集简介标签
        mVideoInfoSynopsisLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //剧集名称
        mVideoInfoName.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //剧集简介内容
        mVideoInfoDesc.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //相关推荐标签
        mRelateRecommentLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_synopsis_more_text), null));
        //相关推荐图标
        mRelateRecommendImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_video_info_skin_more), null));
        //更多选集标签
        mMoreSelectionsLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_synopsis_more_text), null));
        //更多选集图标
        mMoreSelectionsImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(),R.drawable.ic_video_info_skin_more), null));
        //更新数字型选集
        if(mMoreSelectionsDigitalAdapter != null) mMoreSelectionsDigitalAdapter.updateSkin(themeMode);
        //更新时间型选集
        if(mMoreSelectionsTimeAdapter != null) mMoreSelectionsTimeAdapter.updateSkin(themeMode);
    }
}
