package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.presenter.PlayVideoInfoRelatedRecommendPresenter;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoRelatedRecommendView;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-相关推荐
 * @Time: 2021/7/21 18:16
 * @Since: 1.0
 */
public class PlayVideoInfoRelatedRecommendFragment extends BasePlayFragment<PlayVideoInfoRelatedRecommendPresenter> implements IPlayVideoInfoRelatedRecommendView {
    private static final String TAG = PlayVideoInfoRelatedRecommendFragment.class.getSimpleName();
    private static final String BUNDLE_ARGS_QIPUID = "bundle_args_qipuid";

    private static final int MAX_COUNT = 12;

    @BindView(R.id.related_recommend_root)
    ConstraintLayout mRootLayout;

    @BindView(R.id.related_recommend_back)
    ImageView mRelatedRecommendBack; //返回按钮

    @BindView(R.id.related_recommend)
    TextView mRelatedRecommendLabel; //相关推荐标签

    @BindView(R.id.related_recommend_album_info_rv)
    RecyclerView mRelatedRecommendAlbumInfo; //专辑剧集信息

    @BindView(R.id.related_recommend_empty)
    LinearLayout mRelatedRecommendEmpty; //空记录

    RecyclerView.Adapter mRelatedAdapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_relatedrecommend;
    }

    @Override
    protected PlayVideoInfoRelatedRecommendPresenter createPresenter() {
        return new PlayVideoInfoRelatedRecommendPresenter();
    }

    public static PlayVideoInfoRelatedRecommendFragment newInstance(long qipuId) {
        Log.d(TAG, "qipuId:" + qipuId);
        PlayVideoInfoRelatedRecommendFragment fragment = new PlayVideoInfoRelatedRecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_ARGS_QIPUID, qipuId);
        fragment.setArguments(bundle);
        return fragment;
    }

    //点击返回键
    @OnClick(R.id.related_recommend_back)
    public void onClickRelatedRecommendBack() {
        FragmentHelper.handlerBack(getFragmentManager());
    }

    @Override
    protected void initView() {
        super.initView();
        Log.d(TAG, "initView");
        mRelatedRecommendAlbumInfo.setLayoutManager(new GridLayoutManager(getContext(), 3));
        if (mRelatedRecommendAlbumInfo.getItemDecorationCount() == 0) {
            mRelatedRecommendAlbumInfo.addItemDecoration(new GridItemDecoration(25, GridItemDecoration.HORIZONTAL));
            mRelatedRecommendAlbumInfo.addItemDecoration(new GridItemDecoration(40, GridItemDecoration.VERTICAL));
        }
    }

    @Override
    public void initData() {
        Log.d(TAG, "lazyInit");
        if (mRelatedAdapter != null) {
            mRelatedRecommendAlbumInfo.setAdapter(mRelatedAdapter);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                long qipuId = arguments.getLong(BUNDLE_ARGS_QIPUID);
                presenter.LoadRecommendVideoInfo(qipuId, MAX_COUNT);
            }
        }
    }


    @Override
    public void onRelatedRecommendVideoInfo(List<VideoInfo> videoInfos) {
        Log.d(TAG, "onRelatedRecommendVideoInfo：" + videoInfos);
        if (videoInfos == null || videoInfos.isEmpty()) {
            if (mRelatedRecommendEmpty.getVisibility() == View.GONE) {
                mRelatedRecommendEmpty.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (mRelatedRecommendEmpty.getVisibility() == View.VISIBLE) {
            mRelatedRecommendEmpty.setVisibility(View.GONE);
        }

        mRelatedAdapter = new SimpleFastAdapter<VideoInfo>(getContext(), R.layout.item_video_info_related_recommend, videoInfos) {
            @Override
            public void convert(BaseViewHolder holder, VideoInfo data, int position) {
                ImageView albumPic = holder.getView(R.id.releated_recommend_album_pic);
                String albumPicUrl = data.getAlbumPic();
                if (albumPicUrl != null && !albumPicUrl.isEmpty()) {
                    String newPicUrl = AppUtils.appendImageUrl(albumPicUrl, "_180_236");
                    GlideUtils.loadImageWithRoundedCorners(getContext(), Uri.parse(newPicUrl), albumPic, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 8);
                }
                TextView albumName = holder.getView(R.id.releated_recommend_album_name);
                albumName.setText(data.getName());
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle eventParams = new Bundle();
                        eventParams.putParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_RELATE, data);
                        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_SELECT_RELATED, eventParams));
                    }
                });
                if(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    albumName.setTextColor(mContext.getColor(R.color.color_skin_text));
                } else {
                    albumName.setTextColor(mContext.getColor(R.color.color_skin_text_notnight));
                }
            }
        };
        mRelatedRecommendAlbumInfo.setAdapter(mRelatedAdapter);
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        updateSkin(themeMode);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin(ThemeManager.ThemeMode themeMode) {
        mRootLayout.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_background), null));
        mRelatedRecommendBack.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(),R.drawable.ic_skin_back_60), null));
        mRelatedRecommendLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_related_recommend_text),null));
        if(mRelatedAdapter != null) mRelatedAdapter.notifyDataSetChanged();
    }
}
