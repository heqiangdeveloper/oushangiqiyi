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
 * @Description: ??????????????????-????????????
 * @Time: 2021/7/21 10:54
 * @Since: 1.0
 */
public class PlayVideoInfoSynopsisFragment extends BasePlayFragment<PlayVideoInfoSynopsisPresenter> implements IPlayVideoInfoSynopsisView {
    private static final String TAG = PlayVideoInfoSynopsisFragment.class.getSimpleName();

    private static final String BUNDLE_ARGS_VIDEOINFO = "synopsis_video_info";

    private static final String BUNDLE_ARGS_ALBUMID = "albumid";

    private static final String BUNDLE_ARGS_YEAR = "year";

    private volatile VideoInfo mCurrentVideoInfo; //???????????????????????????

    private int order;//????????????

    private long mAlbumId;

    private int mYear;

    @BindView(R.id.video_info_root)
    ConstraintLayout mVideoInfoRootLayout;//?????????

    @BindView(R.id.video_info_synopsis_label)
    TextView mVideoInfoSynopsisLabel;//??????????????????

    @BindView(R.id.video_info_related_recommend_layout)
    LinearLayout mRelateRecommendMore; //??????????????????

    @BindView(R.id.video_info_related_recommend_label)
    TextView mRelateRecommentLabel; //??????????????????

    @BindView(R.id.video_info_related_recommend_more)
    ImageView mRelateRecommendImg; //??????????????????

    @BindView(R.id.video_into_albumpic)
    ImageView mAlbumPiciv; //???????????????

    @BindView(R.id.video_info_name)
    TextView mVideoInfoName; //????????????

    @BindView(R.id.video_info_desc)
    TextView mVideoInfoDesc;  //??????????????????

    @BindView(R.id.video_info_details)
    TextView mVideoInfoDetails; //????????????

    @BindView(R.id.video_info_more_selections)
    RecyclerView mVideoInfoMoreSelections; //????????????

    @BindView(R.id.more_selections_layout)
    ConstraintLayout mMoreSelectionsLayout; //??????????????????

    @BindView(R.id.video_info_more_selections_label)
    TextView mMoreSelectionsLabel; //??????????????????

    @BindView(R.id.video_info_more_selections_img)
    ImageView mMoreSelectionsImg; //??????????????????

    @BindView(R.id.more_selections_loading)
    ProgressBar mProgressBtn;//???????????????

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager; //??????????????????

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //????????????

    MoreSelectionsDigitalAdapter mMoreSelectionsDigitalAdapter; //??????????????????

    MoreSelectionsTimeAdapter mMoreSelectionsTimeAdapter; //??????????????????

    private volatile CastInfo mCastInfo; //????????????

    private int mCurrentPartitionPos = -1;//????????????????????????????????????

    private int mMoreSelectionType = 0; //??????????????? 1 ???????????? 2 ?????????

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
            if (mCurrentVideoInfo.isAlbum()) { //???????????????
                oldAlbumId = mCurrentVideoInfo.getQipuId(); //????????????id, qipuId?????????id
            } else { //???????????????
                oldAlbumId = mCurrentVideoInfo.getAlbumId(); //????????????id, albumId?????????id
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
                if (mMoreSelectionsLayout.getVisibility() == View.VISIBLE) { //?????????????????????
                    mMoreSelectionsLayout.setVisibility(View.GONE);
                }
            }
            mCastInfo = mCurrentVideoInfo.getCast();
        }
    }

    @Override
    protected void initView() {
        if (mCurrentVideoInfo != null && mCurrentVideoInfo.isVideo()) { //???????????????
            showSynopsisInfo(mCurrentVideoInfo); //??????????????????
        }
    }

    /**
     * ??????????????????
     * @param videoInfo ????????????
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

            long oldAlbumId = mCurrentVideoInfo.getAlbumId(); //?????????????????????id
            if (mCurrentVideoInfo.isAlbum()) { //????????????????????????????????????
                oldAlbumId = mCurrentVideoInfo.getQipuId(); //???????????????????????????id
            }
            long newAlbumId = videoInfo.getAlbumId(); //??????????????????????????????id
            if (videoInfo.isAlbum()) { //?????????????????????????????????????????????
                newAlbumId = videoInfo.getQipuId(); //????????????????????????????????????id
            }
            Log.d(TAG, "oldAlbumId:" + oldAlbumId + ",newAlbumId:" + newAlbumId);
            if (oldAlbumId != newAlbumId) { //?????????????????????
                mCastInfo = videoInfo.getCast(); //??????????????????
            }
        }
        mCurrentVideoInfo = videoInfo;

        if (videoInfo != null && videoInfo.isVideo()) { //???????????????
            showSynopsisInfo(videoInfo); //??????????????????
        } else if (videoInfo != null && videoInfo.isAlbum()) { //???????????????/??????
            Optional.ofNullable(presenter).ifPresent(playVideoInfoSynopsisPresenter -> playVideoInfoSynopsisPresenter.loadVideoInfo(videoInfo.getQipuId()));//????????????????????????
        }
        if (videoInfo != null && mMoreSelectionsDigitalAdapter != null && videoInfo.getSourceCode() == 0) { //??????????????????
            mMoreSelectionsDigitalAdapter.setSelected(videoInfo); //??????????????????
        }
    }


    /**
     * ??????????????????
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
            if (mVideoInfoName != null) { //??????????????????
                mVideoInfoName.setText(videoName);
            }
            if (albumPic != null && !albumPic.isEmpty() && mAlbumPiciv != null) { // ??????????????????
                String newUrl = AppUtils.appendImageUrl(albumPic, "_195_260");
                GlideUtils.loadImageWithRoundedCorners(getContext(), Uri.parse(newUrl), mAlbumPiciv, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 8);
            }
            if (mVideoInfoDesc != null) { //??????????????????
                mVideoInfoDesc.setText(desc == null || desc.isEmpty() ? "???" : desc);
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

            if (mVideoInfoName != null) { //??????????????????
                mVideoInfoName.setText(name);
            }
            if (albumPic != null && !albumPic.isEmpty() && mAlbumPiciv != null) { // ??????????????????
                String newUrl = AppUtils.appendImageUrl(albumPic, "_195_260");
                GlideUtils.loadImageWithRoundedCorners(getContext(), Uri.parse(newUrl), mAlbumPiciv, R.drawable.ic_albumpic_place_holder, R.drawable.ic_albumpic_place_holder, 8);
            }
            if (mVideoInfoDesc != null) { //??????????????????
                mVideoInfoDesc.setText(desc == null || desc.isEmpty() ? "???" : desc);
            }
        }
    }

    //????????????
    @OnClick({R.id.video_info_related_recommend_layout, R.id.video_info_details, R.id.video_info_more_select_layout})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.video_info_related_recommend_layout: //????????????
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
            case R.id.video_info_details: //????????????
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5921);
                String desc = mCurrentVideoInfo.getDesc();
                FragmentHelper.loadFragment(R.id.video_info_container, getFragmentManager(), PlayVideoInfoEpisodeDetailsFragment.newInstance(desc, mCastInfo), true);
                break;
            case R.id.video_info_more_select_layout: //????????????
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
     * ?????????????????????
     *
     * @param videoInfoList ????????????
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
     * ?????????????????????
     *
     * @param videoInfoList ??????????????????
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
     * ??????????????????(???????????????
     * @param videoInfo ????????????
     */
    @Override
    public void onLoadVideoInfo(VideoInfo videoInfo) {
        Log.d(TAG, "onLoadVideoInfo:" + videoInfo);
        mCastInfo = mCurrentVideoInfo.getCast(); //??????????????????????????????
        showSynopsisInfo(videoInfo); //??????????????????
    }

    /**
     * ????????????
     * @param videoInfoList ??????????????????
     * @param type ????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onLoadVideoInfoList(List<VideoInfo> videoInfoList, int type) {
        if (mMoreSelectionsLayout.getVisibility() == View.GONE) { //????????????
            mMoreSelectionsLayout.setVisibility(View.VISIBLE);
        }
        mMoreSelectionType = type;
        if (type == TYPE_DIGITAL) { //?????????
            showDigitalTypeSelection(videoInfoList); //?????????????????????
        } else if (type == TYPE_TIME) { //?????????
            showTimeTypeSelection(videoInfoList); //?????????????????????
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
            case EventConstant.EVENT_TYPE_EPISODE_LOAD_COMPLETE: //??????????????????
                Log.d(TAG, "event type episode load complete: " + mMoreSelectionType);
                if (mMoreSelectionType == 0) { //????????????????????????
                    Optional.ofNullable(presenter).ifPresent(presenter -> presenter.loadPartitionSelection(mPlayListManager.getCurrentPartitionPos()));
                } else { //??????????????????????????????
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
            case EventConstant.EVENT_TYPE_PLAY_SELECT_RELATED: //????????????
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
     * ????????????
     * @param themeMode ??????/????????????
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin(ThemeManager.ThemeMode themeMode) {
        //????????????
        mVideoInfoRootLayout.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        //??????????????????
        mVideoInfoSynopsisLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //????????????
        mVideoInfoName.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //??????????????????
        mVideoInfoDesc.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        //??????????????????
        mRelateRecommentLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_synopsis_more_text), null));
        //??????????????????
        mRelateRecommendImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_video_info_skin_more), null));
        //??????????????????
        mMoreSelectionsLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_synopsis_more_text), null));
        //??????????????????
        mMoreSelectionsImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(),R.drawable.ic_video_info_skin_more), null));
        //?????????????????????
        if(mMoreSelectionsDigitalAdapter != null) mMoreSelectionsDigitalAdapter.updateSkin(themeMode);
        //?????????????????????
        if(mMoreSelectionsTimeAdapter != null) mMoreSelectionsTimeAdapter.updateSkin(themeMode);
    }
}
