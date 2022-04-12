package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.ChannelChildTagAdapter;
import com.oushang.iqiyi.adapter.ChannelInfoAdapter;
import com.oushang.iqiyi.adapter.ChannelThirdTagAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.presenter.ClassifyDetailsPresenter;
import com.oushang.iqiyi.mvp.view.IClassifyDetailsView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.entries.VideoInfo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 分类详情
 * @Time: 2021/7/19 11:20
 * @Since: 1.0
 */
@Route(path = "/com/classifyDetailsFragment")
public class ClassifyDetailsFragment extends BaseLazyFragment<ClassifyDetailsPresenter> implements IClassifyDetailsView {
    private static final String TAG = ClassifyDetailsFragment.class.getSimpleName();

    @BindView(R.id.channel_child_tag_rv)
    RecyclerView mChannelChildTagRV; //子标签

    @BindView(R.id.channel_albumpic_info_rv)
    RecyclerView channelAlbumPicInfo; //频道详情信息

    @BindView(R.id.channel_third_tag_rv)
    RecyclerView mChannelThirdTagRV; //第三级标签

    @BindView(R.id.child_tag_fragment_contanier)
    FrameLayout mChildTagFragmentLayout;

    @BindView(R.id.child_tag_back_layout)
    LinearLayout childTagBackLayout; //返回

    @BindView(R.id.child_tag_back_title)
    TextView childTagBackTitle; //频道详情名称

    @BindView(R.id.channel_child_tag_clear)
    TextView mChildTagClear; //清空

    @BindView(R.id.channel_info_empty_layout)
    ConstraintLayout mChannelInfoEmpty; //空数据

    private String mChannelName; //频道名称

    private ChannelInfoAdapter mChannelInfoAdapter; //频道详情适配器

    private ChannelChildTagAdapter mChannelChildTagAdapter; //子标签适配器

    private static final int MAX_TOTAL = 800; //最大数据量

    private int mPage_num = 1; //第几页

    private static final int PAGE_SIZE = 5; //一页请求的个数

    private HashMap<ChannelTag, ChannelTag> mSelectChannelTag = new HashMap<>(); //频道标签筛选列表<子标签, 第三级标签>

    private volatile boolean isSlideRight = false; //是否右滑

    private int lastItemCount = 0; //上次item的个数

    private volatile int mLoadTotal = 0; //总共能加载多少数据

    @Override
    protected int setLayout() {
        return R.layout.fragment_classify_details;
    }

    @Override
    protected ClassifyDetailsPresenter createPresenter() {
        return new ClassifyDetailsPresenter();
    }

    public static ClassifyDetailsFragment newInstance() {
        return new ClassifyDetailsFragment();
    }

    @OnClick({R.id.channel_child_tag_clear, R.id.child_tag_back_layout})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.channel_child_tag_clear: //点击清空
                presenter.loadChannelInfos(mChannelName, 1, PAGE_SIZE); //加载频道所有详情信息
                mSelectChannelTag.clear(); //清空标签筛选列表
                mChildTagClear.setEnabled(false); //设置清空不可用
                break;
            case R.id.child_tag_back_layout: //点击返回键
                FragmentHelper.handlerBack(getChildFragmentManager());
                break;
        }

    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        Bundle arguments = requireArguments();
        Log.d(TAG, "arguments:" + arguments);
        if (arguments != null) {
            ChannelParentTag channelParentTag = arguments.getParcelable(Constant.BUNDLE_KEY_CHANNEL_PARENT_TAG);
            if (channelParentTag != null) {
                Log.d(TAG, "receive tag:" + channelParentTag);
                mChannelName = channelParentTag.getChannelTag().getName();
                presenter.loadChannelInfos(mChannelName, 1, PAGE_SIZE); //加载频道详情信息
            } else {
                Log.e(TAG,"args is null");
            }
        }
    }

    private String getSifitThirdChannelTagIds() {
        StringBuilder ids = new StringBuilder();
        if (mSelectChannelTag != null && !mSelectChannelTag.isEmpty()) {
            List<ChannelTag> thirdChannelTag = new ArrayList<>(mSelectChannelTag.values());
            for (int i = 0; i < thirdChannelTag.size(); i++) {
                ids.append(thirdChannelTag.get(i).getId());
                if(i != thirdChannelTag.size()-1) {
                    ids.append(",");
                }
            }
        }
        return ids.toString();
    }

    @Override
    protected void initView() {
        super.initView();

        //子标签列表
        mChannelChildTagRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mChannelChildTagRV.addItemDecoration(new GridItemDecoration(60, GridItemDecoration.HORIZONTAL));

        //频道详情列表
        channelAlbumPicInfo.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        channelAlbumPicInfo.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.HORIZONTAL));

        //第三级标签列表
        mChannelThirdTagRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        if (mChannelThirdTagRV.getItemDecorationCount() == 0) {
            mChannelThirdTagRV.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.HORIZONTAL));
            mChannelThirdTagRV.addItemDecoration(new GridItemDecoration(20, GridItemDecoration.VERTICAL));
        }

        //频道详情滑动加载
        channelAlbumPicInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged:" + newState + ",canScroll:" + recyclerView.canScrollHorizontally(-1));
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int itemCount = manager.getItemCount(); // 当前item个数
                    int lastPosition = manager.findLastCompletelyVisibleItemPosition(); //最后可见的item的位置
                    Log.d(TAG, "itemCount:" + itemCount + ",lastPosition:" + lastPosition + ",lastItemCount:" + lastItemCount + ",isSildeRight:" + isSlideRight);
                    if (lastItemCount != itemCount && lastPosition == itemCount -1 && itemCount < MAX_TOTAL && itemCount < mLoadTotal) { //如果item个数有变化且右滑到最后一个位置
                        lastItemCount = itemCount;
                        mPage_num = itemCount / PAGE_SIZE + 1;
                        Log.d(TAG, "page_num:" + mPage_num);
                        presenter.loadMoreChannelInfos(mChannelName, mPage_num, PAGE_SIZE, getSifitThirdChannelTagIds());
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled:" + dx + "," + dy);
                isSlideRight = dx > 0;
            }
        });

    }

    //回调频道详情信息
    @Override
    public void onLoadChannelInfo(ChannelInfo channelInfo) {
        Log.d(TAG, "onLoadChannelInfo:" + channelInfo);
        lastItemCount = 0;
        mLoadTotal = channelInfo.getTotal();
        if(mChannelThirdTagRV.getVisibility() == View.VISIBLE) { //隐藏第三级标签列表
            mChannelThirdTagRV.setVisibility(View.GONE);
        }

        if(mChannelInfoEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
            mChannelInfoEmpty.setVisibility(View.GONE);
        }

        //显示频道详情列表
        if (channelAlbumPicInfo.getVisibility() == View.GONE) {
            channelAlbumPicInfo.setVisibility(View.VISIBLE);
        }

        //获取子标签列表
        List<ChannelTag> channelTagList = channelInfo.getTags();
        Log.d(TAG, "channelTagList:" + channelTagList);
        mChannelChildTagAdapter = new ChannelChildTagAdapter(getContext(), channelTagList);
        mChannelChildTagRV.setAdapter(mChannelChildTagAdapter);

        //获取频道详情视频列表信息
        List<VideoInfo> videoInfos = channelInfo.getVideos();
        mChannelInfoAdapter = new ChannelInfoAdapter(getContext(), videoInfos);
        channelAlbumPicInfo.setAdapter(mChannelInfoAdapter);

        //子标签筛选
        mChannelChildTagAdapter.setItemSelectListener(new ChannelChildTagAdapter.ItemSelectListener() {
            @Override
            public void onSelected(ChannelTag data, int position) { //选中的子标签
                Log.d(TAG, "onSelected:" + data);
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5928, data.getName()); //埋点数据
                presenter.loadChannelThirdTag(data);//加载第三级标签
                mChannelChildTagAdapter.setMultiSelect(position); //子标签多选
                mChildTagClear.setEnabled(true); //清空
            }
        });
    }

    //回调加载频道第三级标签
    @Override
    public void onLoadChannelThirdTag(ChannelTag parent) {
        Log.d(TAG, "onLoadChannelThirdTag:" + parent);
        if (parent == null || parent.getChildren().isEmpty()) {
            return;
        }

        if(mChannelInfoEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
            mChannelInfoEmpty.setVisibility(View.GONE);
        }

        if (channelAlbumPicInfo.getVisibility() == View.VISIBLE) { //隐藏频道详情
            channelAlbumPicInfo.setVisibility(View.GONE);
        }

        if(mChannelThirdTagRV.getVisibility() == View.GONE) { //显示第三级标签列表
            mChannelThirdTagRV.setVisibility(View.VISIBLE);
        }

        mChannelThirdTagRV.setAdapter(new ChannelThirdTagAdapter(getContext(), parent)); //第三级子标签
    }

    //回调筛选加载频道详情信息
    @Override
    public void onSiftChannelInfo(ChannelInfo channelInfo) {
        Log.d(TAG, "onSiftChannelInfo:" + channelInfo);
        if (channelInfo != null) {
            lastItemCount = 0;
            mLoadTotal = channelInfo.getTotal();
            if (channelAlbumPicInfo.getVisibility() == View.GONE) { //显示频道详情
                channelAlbumPicInfo.setVisibility(View.VISIBLE);
            }

            if(mChannelThirdTagRV.getVisibility() == View.VISIBLE) { //隐藏第三级标签列表
                mChannelThirdTagRV.setVisibility(View.GONE);
            }

            if(mChannelInfoEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
                mChannelInfoEmpty.setVisibility(View.GONE);
            }

            if (mChannelInfoAdapter != null) {
                mChannelInfoAdapter.updateData(channelInfo.getVideos());
            }
        }
    }

    //加载更多
    @Override
    public void onLoadChannelInfoMore(ChannelInfo channelInfo) {
        if (mChannelInfoAdapter != null && channelInfo != null) {
            mLoadTotal = channelInfo.getTotal();
            mChannelInfoAdapter.addData(channelInfo.getVideos());
        }
    }

    //筛选为空
    @Override
    public void showLoadEmptyView() {
        super.showLoadEmptyView();
        if(mChannelInfoEmpty.getVisibility() == View.GONE) { //显示空数据
            mChannelInfoEmpty.setVisibility(View.VISIBLE);
        }

        if (channelAlbumPicInfo.getVisibility() == View.VISIBLE) { //隐藏频道详情
            channelAlbumPicInfo.setVisibility(View.GONE);
        }

        if(mChannelThirdTagRV.getVisibility() == View.VISIBLE) { //隐藏第三级标签列表
            mChannelThirdTagRV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        if(eventType == EventConstant.EVENT_TYPE_SELECT_THIRD_CHANNEL_TAG) {
            ChannelTag childChannelTag = eventParams.getParcelable(EventConstant.EVENT_PARAMS_CHILD_CHANNEL_TAG);
            ChannelTag thirdChannelTag = eventParams.getParcelable(EventConstant.EVENT_PARAMS_THIRD_CHANNEL_TAG);
            Log.d(TAG, "childChannelTag:" + childChannelTag +  ";thirdChannelTag:" + thirdChannelTag);
            if(!thirdChannelTag.getId().startsWith("all")) {
                mSelectChannelTag.put(childChannelTag, thirdChannelTag);
                childChannelTag.setAlias(thirdChannelTag.getAlias());
                if(mChannelChildTagAdapter != null) {
                    mChannelChildTagAdapter.changeAliasName(childChannelTag, true);
                }
            } else {
                mSelectChannelTag.remove(childChannelTag);
                childChannelTag.setAlias(thirdChannelTag.getName());
                if(mChannelChildTagAdapter != null) {
                    mChannelChildTagAdapter.changeAliasName(childChannelTag, false);
                }
            }
            presenter.siftChannelInfos(mChannelName, 1, 5, getSifitThirdChannelTagIds());
        }
    }

}
