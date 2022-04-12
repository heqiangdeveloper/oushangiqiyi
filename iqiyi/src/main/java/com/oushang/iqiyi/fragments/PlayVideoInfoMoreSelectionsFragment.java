package com.oushang.iqiyi.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MoreSelectionsTimeAdapter;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.presenter.PlayVideoInfoMoreSelectionsPresenter;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoMoreSelectionsView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayListManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-更多选集
 * @Time: 2021/7/21 17:16
 * @Since: 1.0
 */
public class PlayVideoInfoMoreSelectionsFragment extends BasePlayFragment<PlayVideoInfoMoreSelectionsPresenter> implements IPlayVideoInfoMoreSelectionsView {
    private static final String TAG = PlayVideoInfoMoreSelectionsFragment.class.getSimpleName();

    @BindView(R.id.video_info_select_range)
    TabLayout mVideoInfoSelectRange; //数字型选集-选集范围标题

    @BindView(R.id.video_info_select_divide_episode)
    ViewPager2 mVideoInfoSelectDivideEpisode; //数字型选集-选集范围内容

    @BindView(R.id.video_info_select_by_time)
    RecyclerView mVideoInfoSelectTime; //时间型选集

    @BindView(R.id.more_selections_sort)
    Button mSelectionSort; //排序

    @BindView(R.id.more_selections_back_img)
    ImageView mBackImg; //返回键箭头

    @BindView(R.id.more_selections_label)
    TextView mMoreSelectionLabel; //更多选集标签

    @BindView(R.id.more_selections_back)
    LinearLayout mBackLayout; //返回键

    @BindView(R.id.video_info_details_container)
    FrameLayout mFragmentContainer;

    private MoreSelectionsTimeAdapter mTimeAdapter;// 时间型选集适配器

    private LongSparseArray<Fragment> mMoreSelectionsFragmentList; //分集fragment列表

    private FragmentStateAdapter mMoreDigitalAdapter; // 数字型选集适配器

    private List<Long> mFragmentItemIds; //分集fragment id 列表

    private List<String> mTabLayoutTitles; //分集tab 标题列表

    private int mCurrentSourceType = 0; //当前选集类型

    private static final int SOURCE_TYPE_DIGITAL = 1; //数字型选集
    private static final int SOURCE_TYPE_TIME = 2; //时间型选集

    private static final int DIVIDE = 10;//分集集数

    public volatile static int sSort = Constant.SORT_ORDER; //顺序

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager; //播放列表管理

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_moreselections;
    }

    @Override
    protected PlayVideoInfoMoreSelectionsPresenter createPresenter() {
        return new PlayVideoInfoMoreSelectionsPresenter();
    }

    public PlayVideoInfoMoreSelectionsFragment() {
        mMoreSelectionsFragmentList = new LongSparseArray<>();
        mFragmentItemIds = new ArrayList<>();
        mTabLayoutTitles = new ArrayList<>();
    }

    public static PlayVideoInfoMoreSelectionsFragment newInstance(long qipuId) { //专辑id
        Log.d(TAG, "qipuId:" + qipuId);
        PlayVideoInfoMoreSelectionsFragment fragment = new PlayVideoInfoMoreSelectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.BUNDLE_KEY_VIDEO_ID, qipuId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        //加载更多选集数据
        Bundle arguments = getArguments();
        if (arguments != null) {
            long qipuId = arguments.getLong(Constant.BUNDLE_KEY_VIDEO_ID);
            if (VideoInfo.isAlbumId(qipuId)) {
                Log.d(TAG, "loadLocalMoreSelection");
                presenter.loadLocalMoreSelection();
            }
        }
    }

    @Override
    protected void initView() {
        int id = getId();
        if (id == R.id.right_drawer_contaniner) { //右侧滑布局
            mBackImg.setVisibility(View.GONE);
            mBackLayout.setClickable(false);
        } else {
            mBackImg.setVisibility(View.VISIBLE);
            mBackLayout.setClickable(true);
        }
        Log.d(TAG, "sort:" + sSort);
        if(sSort == Constant.SORT_ORDER) { //如果是顺序
            mSelectionSort.setText(R.string.more_selection_forward_sort);
            mSelectionSort.setBackgroundResource(R.drawable.video_info_more_selection_sort);
        } else if(sSort == Constant.SORT_REVERSE) { //如果是倒序
            mSelectionSort.setText(R.string.more_selection_reverse_sort);
            mSelectionSort.setBackgroundResource(R.drawable.video_info_more_selection_reverse);
        }
    }

    /**
     * 初始化TabLayout
     *
     * @param episodeInfoList 剧集列表
     */
    private void initTabLayout(EpisodeInfoList episodeInfoList) {
        long albumId = episodeInfoList.getAlbumId();
        long total = episodeInfoList.getMixedCount();//获取总集数
        mTabLayoutTitles = divide_episode(total, DIVIDE);//分集范围列表
        Log.d(TAG, "initTabLayout:" + mTabLayoutTitles);
        int size = mTabLayoutTitles.size();
        for (int i = 0; i < size; i++) {
            //添加tab标签
            String tab = mTabLayoutTitles.get(i);
            mVideoInfoSelectRange.addTab(mVideoInfoSelectRange.newTab().setText(tab));
            mFragmentItemIds.add((albumId >> 1) + tab.hashCode() + System.currentTimeMillis());
        }

        //去除下划线
        mVideoInfoSelectRange.setSelectedTabIndicator(null);

        //TabLayout 和 viewpager 联动
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mVideoInfoSelectRange, mVideoInfoSelectDivideEpisode, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTabLayoutTitles.get(position));
            }
        });

        mMoreDigitalAdapter = new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return mTabLayoutTitles.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Log.d(TAG, "createFragment position:" + position);
                Long itemId = mFragmentItemIds.get(position);
                Log.d(TAG, "itemId:" + itemId);
                Fragment fragment = null;
                if (!mMoreSelectionsFragmentList.containsKey(itemId)) {
                    String range = mTabLayoutTitles.get(position);
                    int pos = 0;
                    int num = 1;
                    if (range.contains("-")) {
                        String[] rangestr = range.split("-");
                        pos = Integer.parseInt(rangestr[0]);
                        int end = Integer.parseInt(rangestr[1]);
                        num = Math.abs(end - pos) + 1;
                        if(end < pos) { //如果end 比 pos小，说明是倒序
                            pos = end;
                        }
                        if (pos > 0) {
                            pos -= 1;
                        }
                    }
                    Log.d(TAG, "pos:" + pos + ",num:" + num);
                    fragment = MoreSelectionsFragment.getInstance(albumId, pos, num, itemId, sSort);
                    mMoreSelectionsFragmentList.put(itemId, fragment);
                } else {
                    Log.d(TAG, "get fragment form MoreSelectionsFragmentList");
                    fragment = mMoreSelectionsFragmentList.get(itemId);
                }
//                Log.d(TAG, "fragment:" + fragment);
                return fragment;
            }

            @Override
            public boolean containsItem(long itemId) {
                return mFragmentItemIds.contains(itemId);
            }

            @Override
            public long getItemId(int position) {
                return mFragmentItemIds.get(position);
            }
        };

        mVideoInfoSelectDivideEpisode.setAdapter(mMoreDigitalAdapter);
        tabLayoutMediator.attach();
        mVideoInfoSelectDivideEpisode.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Long itemId = mFragmentItemIds.get(position);
                Log.d(TAG, "onPageSelected:" + position +",id:" + itemId);
                notifyMoreSelectionSortChange(itemId);
            }
        });

        mVideoInfoSelectRange.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab:" + tab.getPosition() + ",tabText:" + tab.getText());
                String tabRange = (String) tab.getText();
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5916, tabRange);
                mVideoInfoSelectDivideEpisode.setCurrentItem(tab.getPosition(), false); //切换tab，viewPager也跟着切换
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabUnselected tab:" + tab.getPosition() + ",tabText:" + tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected tab:" + tab.getPosition() + ",tabText:" + tab.getText());
            }
        });

        if(sSort == Constant.SORT_REVERSE) { //如果是倒序
//            Collections.reverse(mFragmentItemIds);
//            Collections.reverse(mTabLayoutTitles);
            reverse();
            mMoreDigitalAdapter.notifyDataSetChanged();//更新tab和fragment
        }

        updateTabSelectPos(mPlayListManager.getCurrentVideoInfo());



    }

    //点击返回
    @OnClick(R.id.more_selections_back)
    public void onClickBack() {
        FragmentHelper.popBackAll(getFragmentManager());
    }

    //点击排序
    @SuppressLint("NotifyDataSetChanged")
    @OnClick(R.id.more_selections_sort)
    public void onClickSort(View view) {
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5918);
        String sortText = mSelectionSort.getText().toString();
        String forwardSort = getString(R.string.more_selection_forward_sort); //正序
        String reverseSort = getString(R.string.more_selection_reverse_sort); //反序
        if (sortText.equals(forwardSort)) { //如果是正序
            sSort = Constant.SORT_REVERSE; //点击过后，倒序
            mSelectionSort.setText(reverseSort);
            mSelectionSort.setBackgroundResource(R.drawable.video_info_more_selection_reverse);
        } else if (sortText.equals(reverseSort)) { //如果是倒序
            sSort = Constant.SORT_ORDER; //点击过后，正序
            mSelectionSort.setText(forwardSort);
            mSelectionSort.setBackgroundResource(R.drawable.video_info_more_selection_sort);
        }
        Log.d(TAG, "click after sort:" + sSort);
        if (mCurrentSourceType == SOURCE_TYPE_DIGITAL) {
            Log.d(TAG, "before reverse:" + mFragmentItemIds);
//            Collections.reverse(mFragmentItemIds);
//            Collections.reverse(mTabLayoutTitles);
            reverse();
            Log.d(TAG, "after reverse:" + mFragmentItemIds);
            if (mMoreDigitalAdapter != null) {
                mMoreDigitalAdapter.notifyDataSetChanged();//更新Fragment顺序
                updateTabSelectPos(mPlayListManager.getCurrentVideoInfo());
            }

        } else if (mCurrentSourceType == SOURCE_TYPE_TIME) {
            if (mTimeAdapter != null) {
                mTimeAdapter.changeSort(sSort);
            }

        }
    }

    /**
     * 加载更多选集（时间型）
     *
     * @param infoList 剧集列表
     */
    @Override
    public void onLoadMoreSelectionByTime(EpisodeInfoList infoList) {
        Log.d(TAG, "onLoadMoreSelectionByTime:" + infoList);
        if (infoList != null) {
            if (mVideoInfoSelectTime.getVisibility() == View.GONE) { //显示时间型选集
                mVideoInfoSelectTime.setVisibility(View.VISIBLE);
            }
            if (mVideoInfoSelectRange.getVisibility() == View.VISIBLE
                    && mVideoInfoSelectDivideEpisode.getVisibility() == View.VISIBLE) { //隐藏数字型选集
                mVideoInfoSelectRange.setVisibility(View.GONE);
                mVideoInfoSelectDivideEpisode.setVisibility(View.GONE);
            }

            if (mVideoInfoSelectTime.getVisibility() == View.VISIBLE) {
                mVideoInfoSelectTime.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                if (mVideoInfoSelectTime.getItemDecorationCount() == 0) {
                    mVideoInfoSelectTime.addItemDecoration(new LineItemDecoration(15, LineItemDecoration.VERTICAL));
                }
            }
            mCurrentSourceType = SOURCE_TYPE_TIME;

            List<VideoInfo> videoInfos = infoList.getEpg(); //获取视频节点信息

            mTimeAdapter = new MoreSelectionsTimeAdapter(this.getContext(), videoInfos, new MoreSelectionsTimeAdapter.OnSortChangedListener() {
                @Override
                public void onChanged(int sort) {
                    Log.d(TAG, "onChanged sort:" + sort);
                    mTimeAdapter.reverse(sort);
                }
            });

            mVideoInfoSelectTime.setAdapter(mTimeAdapter);

            if(sSort != mTimeAdapter.getCurrentSort()) {
                Log.d(TAG, "sort:" + sSort + ",currentSort:"+ mTimeAdapter.getCurrentSort());
                mTimeAdapter.reverse(sSort);
            }
        }
    }

    /**
     * 加载更多选集（数字型）
     *
     * @param infoList 剧集列表
     */
    @Override
    public void onLoadMoreSelectionByDigital(EpisodeInfoList infoList) {
        Log.d(TAG, "onLoadMoreSelectionByDigital");
        if (infoList != null) {
            if (mVideoInfoSelectRange.getVisibility() == View.GONE
                    && mVideoInfoSelectDivideEpisode.getVisibility() == View.GONE) { //显示数字型选集
                mVideoInfoSelectRange.setVisibility(View.VISIBLE);
                mVideoInfoSelectDivideEpisode.setVisibility(View.VISIBLE);
            }
            if (mVideoInfoSelectTime.getVisibility() == View.VISIBLE) { //隐藏时间型选集
                mVideoInfoSelectTime.setVisibility(View.GONE);
            }
            mCurrentSourceType = SOURCE_TYPE_DIGITAL;
            initTabLayout(infoList);
        }
    }

    /**
     * 分集 (1-10)
     *
     * @param total  总集数
     * @param divide 分集集数
     * @return 分集数据
     */
    private List<String> divide_episode(long total, int divide) {
        int rangeStart = 1;
        String range = "";
        List<String> rangeList = new ArrayList<>();
        for (int i = 1; i < total + 1; i++) {
            if (i % divide == 0) {
                range = rangeStart + "-" + i;
                rangeStart = i + 1;
                rangeList.add(range);
                continue;
            }
            if (total - i < (total % divide)) {
                range = rangeStart + "-" + total;
                rangeList.add(range);
                break;
            }
        }
        return rangeList;
    }

    /**
     * 判断是在tab哪个位置
     * @param order 集数
     * @return 位置
     */
    private int indexOfTabLayoutTitleList(int order) {
        if(mTabLayoutTitles != null && !mTabLayoutTitles.isEmpty() && order > 0) {
            int size = mTabLayoutTitles.size();
            for(int i = 0; i < size; i++) {
                String[] splitTitle = mTabLayoutTitles.get(i).split("-");
                int start = Integer.parseInt(splitTitle[0]);
                int end = Integer.parseInt(splitTitle[1]);
                if(end >= start) {
                    if(order >= start && order <= end) {
                        return i;
                    }
                } else {
                    if(order >= end && order <= start) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 更新tab选中位置
     * @param videoInfo 视频信息
     */
    private synchronized void updateTabSelectPos(VideoInfo videoInfo) {
        Log.d(TAG, "updateTabSelectPos");
        if(videoInfo == null || mVideoInfoSelectRange == null) {
            return;
        }
        int tabIndex = indexOfTabLayoutTitleList(videoInfo.getOrder());//当前视频的集数，在tab 中的位置
        if(tabIndex != -1) {
            Log.d(TAG, "tabIndex:" + tabIndex);
            mVideoInfoSelectRange.selectTab(mVideoInfoSelectRange.getTabAt(tabIndex));//tab选中这个位置
        }
    }

    /**
     * 反转
     */
    private void reverse() {
        if(mTabLayoutTitles != null && mFragmentItemIds != null) {
            int tabCount = mTabLayoutTitles.size();
            for(int i = 0; i < tabCount; i++) {
                String tabTitle = mTabLayoutTitles.get(i);
                String[] tabRange = tabTitle.split("-");
                if(tabRange.length > 1) {
                    tabTitle = tabRange[1] + "-" + tabRange[0];
                    mTabLayoutTitles.set(i, tabTitle);
                }
            }
            Collections.reverse(mFragmentItemIds);
            Collections.reverse(mTabLayoutTitles);
        }
    }

    /**
     * 通知更多选集内容排序变更
     */
    private void notifyMoreSelectionSortChange(long id) {
        Log.d(TAG, "notifyMoreSelectionSortChange");
        Bundle eventParams = new Bundle();
        eventParams.putInt(EventConstant.EVENT_PARAMS_SORT_CHANGED, sSort);
        eventParams.putLong(EventConstant.EVENT_PARAMS_NOTIFY_FRAGMENT_ID, id);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_REVERSE_CHANGE, eventParams));
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        Log.d(TAG, "onEvent:" + event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_EPISODE_DETAIL: //剧集详情
                String desc = eventParams.getString(EventConstant.EVENT_PARAMS_VIDEO_INFO_DESC);
                CastInfo castInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_VIDEO_INFO_CAST_INFO);
                FragmentManager fragmentManager = null;
                if (presenter.isFullScreen()) {
                    if (getActivity() != null) {
                        fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentHelper.loadFragment(R.id.right_drawer_contaniner, fragmentManager, TimeSelectionsDetailsFragment.newInstance(desc, castInfo), true);
                    }

                } else {
                    if (getParentFragment() != null) {
                        fragmentManager = getParentFragment().getFragmentManager();
                        if (fragmentManager != null) {
                            FragmentHelper.loadFragment(R.id.video_info_container, fragmentManager, TimeSelectionsDetailsFragment.newInstance(desc, castInfo), true);
                        }
                    }
                }
                break;
            case EventConstant.EVENT_TYPE_EPISODE_LOAD_COMPLETE:
                Log.d(TAG, "episode load complete");
//                presenter.loadLocalMoreSelection();
                break;

            case EventConstant.EVENT_TYPE_UPDATE_PLAY_VIDEO_INFO:
                VideoInfo videoInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_PLAY_VIDEO_INFO);
                updateTabSelectPos(videoInfo);
                break;
            case EventConstant.EVENT_TYPE_UPDATE_REMEMBER_PLAY_VIDEO_INFO:
                VideoInfo videoInfo2 = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_REMEMBER_PLAY_VIDEO_INFO);
                updateTabSelectPos(videoInfo2);
                break;
            case EventConstant.EVENT_TYPE_FULL_SCREEN_CHANGED:
                if(eventParams != null) {
                    boolean isFull = eventParams.getBoolean(EventConstant.EVENT_PARAMS_FULL_SCREEN_CHANGED, false);
                    if(!isFull) { //非全屏
                        updateTabSelectPos(mPlayListManager.getCurrentVideoInfo());
                    }
                }
                break;
        }
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        updateSkin();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        mBackImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_skin_back_60), null));
        mMoreSelectionLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_more_selection_text), null));
        mVideoInfoSelectRange.setTabTextColors(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_more_selection_text), null), getResources().getColor(R.color.color_video_details,null));
    }

    @Override
    public void onDestroy() {
        mMoreSelectionsFragmentList = null;
        mFragmentItemIds = null;
        mTabLayoutTitles = null;
        super.onDestroy();
    }
}
