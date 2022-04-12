package com.oushang.iqiyi.fragments;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.BannerImagerAdapter;
import com.oushang.iqiyi.entries.BannerEntry;
import com.oushang.iqiyi.mvp.presenter.HotRecommentPresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.youth.banner.Banner;
import com.youth.banner.indicator.RectangleIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Author: DELL
 * @Description: 热门推荐fragment
 * @Time: 2021/7/6 15:45
 * @Since: 1.0
 */
@Deprecated
public class HotRecommendFragment extends BaseFragmentMVP<HotRecommentPresenter> {

    @BindView(R.id.hot_recommend_banner)
    Banner banner;

    private List<BannerEntry> bannerEntryList;

    @Override
    protected int setLayout() {
        return R.layout.fragment_hotrecomment;
    }

    @Override
    protected HotRecommentPresenter createPresenter() {
        return new HotRecommentPresenter();
    }


    @Override
    protected void initView() {
        super.initView();
        banner.addBannerLifecycleObserver(this)
                .setIndicator(new RectangleIndicator(this.getContext()))
                .setAdapter(new BannerImagerAdapter(bannerEntryList))
                .setIndicatorRadius(5)
                .setBannerRound(5);
    }

    @Override
    protected void initData() {
        super.initData();
//        bannerEntryList = new ArrayList<>();
//        bannerEntryList.add(new BannerEntry("https://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg", ""));
//        bannerEntryList.add(new BannerEntry("https://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg", ""));
//        bannerEntryList.add(new BannerEntry("https://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg", ""));
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        banner.destroy();
    }
}
