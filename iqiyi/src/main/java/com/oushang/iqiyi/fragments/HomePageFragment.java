package com.oushang.iqiyi.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.HomeRecommendAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.mvp.presenter.HomePagePresenter;
import com.oushang.iqiyi.mvp.view.IHomePageView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.HorizontalLinearLayoutManager;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.NetworkUtils;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.entries.RecommendInfo;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 首页Fragment
 * @Time: 2021/7/12 10:00
 * @Since: 1.0
 */
public class HomePageFragment extends BaseLazyFragment<HomePagePresenter> implements IHomePageView {
    private static final String TAG = HomePageFragment.class.getSimpleName();

    @BindView(R.id.home_page_rv)
    RecyclerView mHomePagerv; //内容

    @BindView(R.id.home_data_loading_layout)
    LinearLayout mLoadingLayout; //加载数据进度布局

    @BindView(R.id.data_loading)
    ImageView mLoadingAnim; //加载动画

    @BindView(R.id.home_data_loading)
    AVLoadingIndicatorView mLoadingView; //加载进度条

    @BindView(R.id.home_net_error_layout)
    LinearLayout mNetErrorLayout; //网络异常布局

    @BindView(R.id.home_network_refresh_btn)
    Button mRefreshBtn; //刷新按钮

    private int lastItemCount = 0;

    private boolean isSlideRight = false;

    List<ChannelTag> channelTags;

    private HorizontalLinearLayoutManager mLinearLayoutManager;

    private HomeRecommendAdapter<IMultiItem> mHomeRecommendAdapter;//首页适配器

    private boolean isLoadingData = false;//是否正在访问推荐

    private List<RecommendInfo> mRecommendInfoList; //首页推荐数据

    private RecyclerView.OnScrollListener mOnScrollListener; //滚动回调

    @Override
    protected int setLayout() {
        return R.layout.fragment_homepage;
    }

    @Override
    protected HomePagePresenter createPresenter() {
        return new HomePagePresenter();
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        if(!NetworkUtils.isNetworkAvailable()) {
            showLoadNetErrorView();
        } else {
            boolean isMobile = NetworkUtils.isMobileConnected();
            Log.d(TAG, "isMobile:" + isMobile);
            String networkInfo = NetworkUtils.getNetworkInfo();
            Log.d(TAG, "networkInfo:" + networkInfo);
            presenter.loadRecommendInfo();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mLinearLayoutManager = new HorizontalLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHomePagerv.setLayoutManager(mLinearLayoutManager);
        if (mHomePagerv.getItemDecorationCount() == 0) {
            mHomePagerv.addItemDecoration(new LineItemDecoration(30, LineItemDecoration.HORIZONTAL));
        }
        mOnScrollListener = new RecyclerView.OnScrollListener() { //滑动加载
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                HorizontalLinearLayoutManager layoutManager = (HorizontalLinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int itemCount = layoutManager.getItemCount();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:" + newState + ",lastItemCount:" + lastItemCount + ",itemCount:" + itemCount + ",lastPosition:" + lastPosition + ",isSlideRight:" + isSlideRight);
                    int page = lastPosition + 1;
                    int size = mRecommendInfoList.size();
                    if(page <= size && lastPosition == itemCount-1 && isSlideRight) {
                        if (mHomeRecommendAdapter != null) {
                            mHomeRecommendAdapter.addData(presenter.LoadMultiData(mRecommendInfoList, page));
                            mLinearLayoutManager.setScrollEnabled(true);
                        }
                    }

                    int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisiblePosition == itemCount) {
                        mLinearLayoutManager.setScrollEnabled(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    isSlideRight = true;
                } else  {
                    isSlideRight = false;
                }
            }
        };
        mHomePagerv.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onLoadRecommondInfo(List<RecommendInfo> recommendInfoList) {
        if(mRecommendInfoList != null) {
            mRecommendInfoList.clear();
        }
        mRecommendInfoList = recommendInfoList;
        List<IMultiItem> multiData = presenter.LoadMultiData(recommendInfoList, 1);
        if(multiData != null && !multiData.isEmpty()) {
            mHomeRecommendAdapter = new HomeRecommendAdapter<IMultiItem>(getContext(), multiData);
            mHomeRecommendAdapter.setOnBannerScrollListener(new HomeRecommendAdapter.OnBannerScrollListener() {
                @Override
                public void onScrollStart() {
                    mLinearLayoutManager.setScrollEnabled(false);
                }

                @Override
                public void onScrollEnd() {
                    mLinearLayoutManager.setScrollEnabled(true);
                }
            });
            mHomePagerv.setAdapter(mHomeRecommendAdapter);
            mLinearLayoutManager.setScrollEnabled(true);
        }
    }

    @Override
    public void onLoadData(List<IMultiItem> data) {
        Log.d(TAG, "onLoadData:" + data);
        isLoadingData = false;//重置为false
        if (data != null) {
            mHomeRecommendAdapter = new HomeRecommendAdapter<IMultiItem>(getContext(), data);
            mHomeRecommendAdapter.setOnBannerScrollListener(new HomeRecommendAdapter.OnBannerScrollListener() {
                @Override
                public void onScrollStart() {
                    mLinearLayoutManager.setScrollEnabled(false);
                }

                @Override
                public void onScrollEnd() {
                    mLinearLayoutManager.setScrollEnabled(true);
                }
            });
            mHomePagerv.setAdapter(mHomeRecommendAdapter);
            mLinearLayoutManager.setScrollEnabled(true);
        }else{
            if(null != presenter) presenter.showNetWorkError();
        }
    }

    @Override
    public void onLoadMoreData(List<IMultiItem> data) {
        Log.d(TAG, "onLoadMoreData");
        if (mHomeRecommendAdapter != null) {
            mHomeRecommendAdapter.addData(data);
            mLinearLayoutManager.setScrollEnabled(true);
        }
    }

    @Override
    public void onAllChannelTags(List<ChannelTag> channelTags) {
        Log.d(TAG, "onAllChannelTags:" + channelTags);
        this.channelTags = channelTags;
    }

    @Override
    public void showLoading() {
        Log.d(TAG, "showLoading");
        mHomePagerv.setVisibility(View.GONE);
        mNetErrorLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingAnim.setImageResource(R.drawable.data_loading_anim_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        animationDrawable.start();
//        mLoadingView.show();
    }

    @Override
    public void hideLoading() {
        Log.d(TAG, "hideLoading");
        mHomePagerv.setVisibility(View.VISIBLE);
//        mLoadingView.hide();
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoadNetErrorView() {
        Log.d(TAG, "showLoadNetErrorView");
        mLoadingLayout.setVisibility(View.GONE);
        mLoadingView.hide();
        mNetErrorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadNetErrorView() {
        Log.d(TAG, "hideLoadNetErrorView");
        mNetErrorLayout.setVisibility(View.GONE);
    }

    //点击刷新
    @OnClick(R.id.home_network_refresh_btn)
    public void onClickRefresh() {
        Log.d(TAG, "onClickRefresh");
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5904);
        if(!NetworkUtils.isNetworkAvailable()) {
            presenter.showToast(getString(R.string.network_not_connect));
        } else {
            if(null != presenter) presenter.hideNetWorkError();
            if (mHomeRecommendAdapter == null) {
                if(!isLoadingData){
                    isLoadingData = true;
                    Log.d(TAG, "onClickRefresh loadData()");
                    if(null != presenter) presenter.loadRecommendInfo();
                }
            } else {
                Log.d(TAG, "onClickRefresh already loadData");
            }
        }
    }

    @Override
    public void onNetWorkChanged(boolean isConnected) {
        super.onNetWorkChanged(isConnected);
        Log.d(TAG, "onNetworkChanged:" + isConnected);
        if (!isConnected) {
            HandlerUtils.postDelayOnMainThread(new Runnable() {
                @Override
                public void run() {
                        Log.d(TAG,"onNetworkChanged after 1s: " + NetworkUtils.isNetworkAvailable());
                        if (!NetworkUtils.isNetworkAvailable()){
                            if (mHomeRecommendAdapter != null) {
                                if("com.oushang.iqiyi".equals(AppUtils.getTopPackage(getContext()))){//当前应用处于前台时
                                    if(null != presenter) presenter.showToast(getString(R.string.network_try_again_later));
                                }
                            } else {
                                if(!isLoadingData){
                                    if(null != presenter) presenter.showNetWorkError();
                                }
                            }
                        }
                    }
            },1000);
        } else {
            if(null != presenter) presenter.hideNetWorkError();
            if (mHomeRecommendAdapter == null) {
                if(!isLoadingData){
                    isLoadingData = true;
                    Log.d(TAG, "loadData()");
                    if(null != presenter) presenter.loadRecommendInfo();
                }
            } else {
                Log.d(TAG, "already loadData");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mHomePagerv != null) {
            mHomePagerv.removeOnScrollListener(mOnScrollListener);
        }
        super.onDestroy();
    }
}
