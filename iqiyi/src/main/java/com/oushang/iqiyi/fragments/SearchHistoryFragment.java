package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.activities.SearchActivity;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.SearchHistoryPresenter;
import com.oushang.iqiyi.mvp.view.ISearchHistoryView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.ThemeContentObserver;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.VideoInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 搜索历史界面
 * @Time: 2021/8/6 10:36
 * @Since: 1.0
 */
public class SearchHistoryFragment extends BaseFragmentMVP<SearchHistoryPresenter> implements ISearchHistoryView, ThemeManager.OnThemeChangeListener {
    private static final String TAG = SearchHistoryFragment.class.getSimpleName();

    @BindView(R.id.search_history_keyword_rv)
    RecyclerView mSearchHistoryKeyRv;

    @BindView(R.id.search_history_delete_img)
    ImageView mSearchHistoryDelete;

    @BindView(R.id.hot_search_layout)
    LinearLayout mHotSearchLayout;

    @BindView(R.id.hot_search_content_rv)
    RecyclerView mHotSearchConentRv;

    @BindView(R.id.search_history_label)
    TextView mSearchHistoryLabel; //搜索历史标签

    @BindView(R.id.hot_search_label)
    TextView mHotSearchLabel; //热门搜索标签

    private SimpleFastAdapter<String> mSearchHistoryKeyWordAdapter;

    private ThemeContentObserver mThemeContentObserver;

    @Override
    protected int setLayout() {
        return R.layout.fragment_search_history;
    }

    @Override
    protected SearchHistoryPresenter createPresenter() {
        return new SearchHistoryPresenter();
    }

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Override
    protected void initView() {
        presenter.loadSearchHistory();
    }

    @Override
    protected void initListener() {
        SPUtils.getSP(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT).registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        ThemeManager.getInstance().registerThemeChangeListener(this);
        mThemeContentObserver = new ThemeContentObserver(getContext(), new Handler());
        ThemeContentObserver.register(getContext(), mThemeContentObserver);
    }

    @OnClick(R.id.search_history_delete_img)
    public void onClickDelete() { //点击删除
        Log.d(TAG, "clear all");
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5952);//埋点数据
        SPUtils.clearShareValue(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT);
        presenter.loadEmptySearchHistory();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Constant.SP_KEY_SEARCH_HISTORY_KEYWORD)) {
                List<String> currentSearchKeyWordList = SPUtils.getShareValue(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT, key, String.class);
                Log.d(TAG, "currentSearchKeyWordList:" + currentSearchKeyWordList);
                if (mSearchHistoryKeyWordAdapter != null) {
                    mSearchHistoryKeyWordAdapter.updateData(currentSearchKeyWordList);
                }
            }
        }
    };

    @Override
    public void onLoadSearchHistory(List<String> searchKeyWord) {
        mSearchHistoryKeyRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        if (mSearchHistoryKeyRv.getItemDecorationCount() == 0) {
            mSearchHistoryKeyRv.addItemDecoration(new LineItemDecoration(19, LineItemDecoration.HORIZONTAL));
        }

        mSearchHistoryKeyWordAdapter = new SimpleFastAdapter<String>(getContext(), R.layout.item_search_history_text, searchKeyWord) {
            @Override
            public void convert(BaseViewHolder holder, String data, int position) {
                if (data == null) {
                    return;
                }
                TextView textView = holder.getView(R.id.search_history_text);
                //Added by heqiang on 2021/11/30 for bug1098962
                String data1 = data;
                if (data1.length() >= 7) {
                    data1 = data1.substring(0, 5) + "...";
                }
                textView.setText(data1);
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    textView.setTextColor(mContext.getColor(R.color.color_skin_search_text));
                    textView.setBackground(mContext.getDrawable(R.drawable.search_history_skin_text_bg));
                } else {
                    textView.setTextColor(mContext.getColor(R.color.color_skin_search_text_notnight));
                    textView.setBackground(mContext.getDrawable(R.drawable.search_history_skin_text_bg_notnight));
                }

                holder.getItemView().setOnClickListener(new View.OnClickListener() { //点击缓存的搜索关键字
                    @Override
                    public void onClick(View v) {
                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5951, data);//埋点数据

                        Bundle eventParams = new Bundle();
                        eventParams.putString(Constant.BUNDLE_KEY_SEARCH_KEY, data.trim());
                        eventParams.putInt(Constant.SEARCH_TYPE, Constant.SEARCH_TYPE_INPUT);
                        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_SEARCH_RESULT, eventParams));
                        SearchActivity.updateHistoryKeyRecord(data.trim());
                    }
                });
            }
        };
        mSearchHistoryKeyRv.setAdapter(mSearchHistoryKeyWordAdapter);
    }

    @Override
    public void onLoadHotSearch(List<VideoInfo> videoInfoList) {
        if (videoInfoList == null) {
            return;
        }
        mHotSearchConentRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        if (mHotSearchConentRv.getItemDecorationCount() == 0) {
            mHotSearchConentRv.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.HORIZONTAL));
            mHotSearchConentRv.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.VERTICAL));
        }

        mHotSearchConentRv.setAdapter(new SimpleFastAdapter<VideoInfo>(getContext(), R.layout.item_hot_search_albumpic, videoInfoList) {
            @Override
            public void convert(BaseViewHolder holder, VideoInfo data, int position) {
                TextView albumName = holder.getView(R.id.hot_search_album_name);
                TextView albumFocus = holder.getView(R.id.hot_search_album_focus);
                ImageView album = holder.getView(R.id.hot_search_album_pic);

                albumName.setText(data.getName());
                albumFocus.setText(data.getFocus());

                ImageView albumExclusive = holder.getView(R.id.hot_search_album_exclusive);
                ImageView albumVip = holder.getView(R.id.hot_search_album_vip);

                boolean exclusive = data.isExclusive();
                if (exclusive) {
                    albumVip.setVisibility(View.GONE);
                    albumExclusive.setVisibility(View.VISIBLE);
                }
                String albumPic = data.getAlbumPic();
                if (albumPic != null && !albumPic.isEmpty()) {
                    String newUrl = AppUtils.appendImageUrl(albumPic, "_260_360");
                    GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newUrl), album, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 10);
                }

                long qipuId = data.getQipuId();
                long albumId = data.getAlbumId();

                if (data.isAlbum()) {
                    albumId = qipuId;
                    qipuId = data.getDefaultEpi().getQipuId();
                }

                final long qid = qipuId;
                final long aid = albumId;

                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "qid:" + qid + "," + aid);

                        Map<String, String> statsValue = new HashMap<>();
                        statsValue.put("item", data.getName());
                        statsValue.put("plate", data.getChnName());
                        statsValue.put("type", data.isVip()?"vip":"无");

                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5953, statsValue);//埋点数据

                        ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                                .withLong(Constant.PLAY_VIDEO_ID, qid)
                                .withLong(Constant.PLAY_ALBUM_ID, aid)
                                .withLong(Constant.PLAY_POSITION, 0)
                                .navigation();
                    }
                });
            }
        });
    }

    @Override
    public void onLoadEmptySearchHistory() {
        mSearchHistoryKeyRv.setVisibility(View.GONE);
        mSearchHistoryDelete.setVisibility(View.GONE);
        mHotSearchLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtils.getSP(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT).unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        ThemeContentObserver.register(getContext(), mThemeContentObserver);
        ThemeManager.getInstance().unRegisterThemeChangeListener(this);
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        updateSkin();
    }

    /**
     * 主题换肤
     */
    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(),R.color.color_skin_background), null));
        mSearchHistoryLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_search_text),null));
        mSearchHistoryDelete.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_search_history_delete_skin), null));
        mHotSearchLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_search_text),null));
        if (mSearchHistoryKeyWordAdapter != null) {
            mSearchHistoryKeyWordAdapter.notifyDataSetChanged();
        }
    }
}
