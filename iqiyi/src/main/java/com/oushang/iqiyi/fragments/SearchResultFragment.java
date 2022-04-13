package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.activities.SearchActivity;
import com.oushang.iqiyi.adapter.SearchResultAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.SearchResultInfo;
import com.oushang.iqiyi.entries.SearchResultNoMore;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.SearchResultPresenter;
import com.oushang.iqiyi.mvp.view.ISearchResultView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.utils.ThemeContentObserver;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 搜索结果界面
 * @Time: 2021/8/6 10:37
 * @Since: 1.0
 */
public class SearchResultFragment extends BaseLazyFragment<SearchResultPresenter> implements ISearchResultView, ThemeManager.OnThemeChangeListener {
    private static final String TAG = "xyj_iqiyi";

    @BindView(R.id.search_result_empty_layout)
    LinearLayout mResultEmptyLayout; //暂无搜索结果布局

    @BindView(R.id.search_result_type_label)
    TextView mResultTypeLabel; //结果类型标签

    @BindView(R.id.search_result_relate_btn)
    Button mResultRelateBtn; //相关

    @BindView(R.id.search_result_newest_btn)
    Button mResultNewestBtn; //最新

    @BindView(R.id.search_result_hottest_btn)
    Button mResultHottestBtn; //最热

    @BindView(R.id.search_source_tips)
    TextView mResultSourceTips; //片源提示

    @BindView(R.id.search_result_content_rv)
    RecyclerView mResultContentRv; //搜索结果列表

    @BindView(R.id.search_result_group)
    View mSearchResultGroup;

    private String mSearchKey; //搜索关键字

    private volatile int mSearchType; //搜索方式， 0 输入搜索， 1 语音搜索

    private static final int SEARCH_RESULT_MAX_COUNT = 80; //最大数据量

    private static final int SEARCH_RESULT_PAGE_MAX_SIZE = 60; //每页最大数据量

    private volatile int mode = 1; //排序方式， 1 相关， 4 最新， 11 最热(非纪录片)  10 最热(纪录片频道)； 默认排序方式：排序

    private SearchResultAdapter mSearchResultAdapter;

    private boolean isloading = false;

    private ThemeContentObserver mThemeContentObserver;

    @Override
    protected int setLayout() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected SearchResultPresenter createPresenter() {
        return new SearchResultPresenter();
    }

    public static SearchResultFragment newInstance(String searchKey, int searchType) {
        return newInstance(searchKey, searchType, 1);
    }

    public static SearchResultFragment newInstance(String searchKey, int searchType, int resultMode) {
        SearchResultFragment resultFragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(Constant.SEARCH_KEYWORD, searchKey);
        args.putInt(Constant.SEARCH_TYPE, searchType);
        args.putInt(Constant.SEARCH_RESULT_MODE, resultMode);
        resultFragment.setArguments(args);
        return resultFragment;
    }

    @Override
    protected void initView() {
        mResultContentRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        if (mResultContentRv.getItemDecorationCount() == 0) {
            mResultContentRv.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.HORIZONTAL));
            mResultContentRv.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.VERTICAL));
        }

        //下滑滚动监听
        mResultContentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "dy:" + dy);
                if (dy > 0) { //向下滚动
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount(); //可见item个数
                        int totalItemCount = layoutManager.getItemCount(); //总共有多少个item
                        int findFirstVisibleItemPos = layoutManager.findFirstVisibleItemPosition();

                        int itemCount = visibleItemCount + findFirstVisibleItemPos; //总共加载了多个
                        Log.d(TAG, "isloading:" + isloading + ",itemCount:" + itemCount + ",totalItemCount:" + totalItemCount);
                        if (!isloading && itemCount < SEARCH_RESULT_MAX_COUNT && totalItemCount < SEARCH_RESULT_MAX_COUNT) {
                            isloading = true;
                            presenter.loadMoreSearchResult(mSearchKey, 2, SEARCH_RESULT_PAGE_MAX_SIZE, mode); //加载更多
                        }
                    }
                }
            }
        });

        ThemeManager.getInstance().registerThemeChangeListener(this);
        mThemeContentObserver = new ThemeContentObserver(getContext(), new Handler());
        ThemeContentObserver.register(getContext(), mThemeContentObserver);
    }

    @Override
    public void lazyInit() {
        Bundle args = getArguments();
        if (args != null) {
            mSearchKey = args.getString(Constant.SEARCH_KEYWORD);
            mSearchType = args.getInt(Constant.SEARCH_TYPE);
            mode = args.getInt(Constant.SEARCH_RESULT_MODE, 1);
            Log.d(TAG, "receive args: searchKey" + mSearchKey + ",searchType:" + mSearchType + ",mode:" + mode);

            if (mode == 1) {
                mResultRelateBtn.setSelected(true); //默认相关选中
            } else if (mode == 4) {
                mResultNewestBtn.setSelected(true);
            } else if (mode == 11 || mode == 10) {
                mResultHottestBtn.setSelected(true);
            }
            presenter.loadSearchResult(mSearchKey, 1, SEARCH_RESULT_PAGE_MAX_SIZE, mode); //加载相关搜索

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "SearchResultFragment onSaveInstanceState: mode:" + mode);
        outState.putInt(Constant.SEARCH_RESULT_MODE, mode);
        SearchActivity.sMode = mode;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mode = savedInstanceState.getInt(Constant.SEARCH_RESULT_MODE, 1);
            Log.d(TAG, "SearchResultFragment onViewStateRestored：mode:" + mode);
        }
    }

    //按钮点击事件
    @OnClick({R.id.search_result_relate_btn,
            R.id.search_result_newest_btn,
            R.id.search_result_hottest_btn})
    public void onClickButton(View v) {
        mSearchType = Constant.SEARCH_TYPE_INPUT; //如果是点击了结果类型，则恢复为输入搜索
        changeSelected(v); //切换按钮选中
    }

    /**
     * 搜索结果回调
     *
     * @param searchResultInfoList 搜索结果信息
     * @param hasMore              还有更多
     */
    @Override
    public void onLoadSearchResultInfo(List<IMultiItem> searchResultInfoList, boolean hasMore) {
        Log.d(TAG, "hasMore:" + hasMore);
        if (mSearchResultGroup.getVisibility() == View.GONE) {
            mSearchResultGroup.setVisibility(View.VISIBLE);
        }
        if (mResultEmptyLayout.getVisibility() == View.VISIBLE) { //隐藏空布局
            mResultEmptyLayout.setVisibility(View.GONE);
        }
        if (mResultContentRv.getVisibility() == View.GONE) { //显示搜索结果布局
            mResultContentRv.setVisibility(View.VISIBLE);
        }
        isloading = false;
        mSearchResultAdapter = new SearchResultAdapter<>(getContext(), searchResultInfoList, mode);
        mResultContentRv.setAdapter(mSearchResultAdapter);
        if (!hasMore) {
            mSearchResultAdapter.addFooter(new SearchResultNoMore(getString(R.string.search_no_more)));
        }

        //如果是语音搜索
        if (mSearchType == Constant.SEARCH_TYPE_VOICE) {
            Log.d(TAG, "search by voice");
            GridLayoutManager layoutManager = (GridLayoutManager) mResultContentRv.getLayoutManager();
            if (layoutManager != null) {
                int firstPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                int lastPos = layoutManager.findLastCompletelyVisibleItemPosition();

                Log.d(TAG, "firstPos:" + firstPos + ",lastPos:" + lastPos + ",visibleFirst:" + layoutManager.findFirstVisibleItemPosition() + ",VisibleLast:" + layoutManager.findLastVisibleItemPosition());
                int count = layoutManager.getItemCount();
                Log.d(TAG, "visiable item count:" + count);

                Bundle eventParams = new Bundle();
                eventParams.putInt(EventConstant.EVENT_PARAMS_VOICE_SEARCH_RESULT_COUNT, count);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_VOICE_SEARCH_RESULT, eventParams));

            }
        }
    }

    /**
     * 加载更多搜索结果信息
     *
     * @param searchResultInfoList 搜索结果信息
     */
    @Override
    public void onLoadMoreSearchResultInfo(List<IMultiItem> searchResultInfoList) {
        if (mSearchResultAdapter != null && searchResultInfoList != null && !searchResultInfoList.isEmpty()) {
            int size = searchResultInfoList.size();
            Log.d(TAG, "onLoadMoreSearchResultInfo size:" + size);
            mSearchResultAdapter.addData(searchResultInfoList);
        }
        mSearchResultAdapter.addFooter(new SearchResultNoMore(getString(R.string.search_no_more)));
    }

    //搜索结果为空
    @Override
    public void onLoadEmptySearchResult() {
        if (mResultContentRv.getVisibility() == View.VISIBLE) { //隐藏搜索结果布局
            mResultContentRv.setVisibility(View.GONE);
        }
        if (mSearchResultGroup.getVisibility() == View.VISIBLE) {
            mSearchResultGroup.setVisibility(View.GONE);
        }

        if (mResultEmptyLayout.getVisibility() == View.GONE) { //显示空布局
            mResultEmptyLayout.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 切换选中
     *
     * @param view view
     */
    private void changeSelected(View view) {
        if (view != null) {
            if (view == mResultRelateBtn) {
                mResultNewestBtn.setSelected(false);
                mResultHottestBtn.setSelected(false);
            } else if (view == mResultNewestBtn) {
                mResultRelateBtn.setSelected(false);
                mResultHottestBtn.setSelected(false);
            } else if (view == mResultHottestBtn) {
                mResultNewestBtn.setSelected(false);
                mResultRelateBtn.setSelected(false);
            }
            view.setSelected(!view.isSelected());
            setSelected(view, view.isSelected());
        }
    }

    /**
     * 设置选中
     *
     * @param view     view
     * @param selected 是否选中
     */
    private void setSelected(View view, boolean selected) {
        int id = view.getId();
        if (selected) {
            switch (id) {
                case R.id.search_result_relate_btn:
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5954, "相关");//进点数据
                    mode = 1;
                    presenter.loadSearchResult(mSearchKey, 1, SEARCH_RESULT_PAGE_MAX_SIZE, mode);
                    break;
                case R.id.search_result_newest_btn:
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5954, "最新");//进点数据
                    mode = 4;
                    presenter.loadSearchResult(mSearchKey, 1, SEARCH_RESULT_PAGE_MAX_SIZE, mode);
                    break;
                case R.id.search_result_hottest_btn:
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5954, "最热");//进点数据
                    mode = 11;
                    presenter.loadSearchResult(mSearchKey, 1, SEARCH_RESULT_PAGE_MAX_SIZE, mode);
                    break;
            }
        } else {
            mode = 0;
            presenter.loadSearchResult(mSearchKey, 1, SEARCH_RESULT_PAGE_MAX_SIZE, mode);
        }
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        if (event != null) {
            int eventType = event.getEventType();
            if (eventType == EventConstant.EVENT_TYPE_SEARCH_TYPE_CHANGED) { //搜索类型变更事件
                Log.d(TAG, "search type changed");
                Bundle eventParams = event.getEventParams();
                mSearchType = eventParams.getInt(Constant.BUNDLE_KEY_SEARCH_Type);
            } else if (eventType == EventConstant.EVENT_TYPE_VOICE_SEARCH_SELECT) { //语音搜索选择节目事件
                Bundle eventParams = event.getEventParams();
                int select = eventParams.getInt(EventConstant.EVENT_PARAMS_VOICE_SEARCH_RESULT_SELECT); //选择第几个
                Log.d(TAG, "select num:" + select);
                if (mSearchResultAdapter != null) {
                    SearchResultInfo resultInfo = (SearchResultInfo) mSearchResultAdapter.getData(select);

                    if (resultInfo != null) {
                        VideoInfo videoInfo = resultInfo.getVideoInfo();
                        Log.d(TAG, "voice search result select:" + videoInfo);

                        ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                                .withLong(Constant.PLAY_VIDEO_ID, videoInfo.getQipuId())
                                .withLong(Constant.PLAY_ALBUM_ID, videoInfo.getAlbumId())
                                .navigation();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        updateSkin();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        mResultTypeLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_text), null));
        mResultSourceTips.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_search_result_source_tips), null));
        if (!mResultRelateBtn.isSelected()) {
            mResultRelateBtn.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_text), null));
            mResultRelateBtn.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.search_result_button_skin_bg), null));
        }
        if (!mResultNewestBtn.isSelected()) {
            mResultNewestBtn.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_text), null));
            mResultNewestBtn.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.search_result_button_skin_bg), null));
        }
        if (!mResultHottestBtn.isSelected()) {
            mResultHottestBtn.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_text), null));
            mResultHottestBtn.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.search_result_button_skin_bg), null));
        }
        if (mSearchResultAdapter != null) {
            mSearchResultAdapter.updateSkin();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mResultContentRv.setVerticalScrollbarThumbDrawable(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.scrollbar_thumb_skin_bg), null));
            mResultContentRv.setVerticalScrollbarTrackDrawable(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.scrollbar_track_skin_bg), null));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ThemeContentObserver.register(getContext(), mThemeContentObserver);
        ThemeManager.getInstance().unRegisterThemeChangeListener(this);
    }
}
