package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.entries.HotRecommendAlbumPic;
import com.oushang.iqiyi.entries.HotRecommendBanner;
import com.oushang.iqiyi.mvp.model.HomePageModel;
import com.oushang.iqiyi.mvp.view.IHomePageView;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.entries.RecommendInfo;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 首页逻辑
 * @Time: 2021/7/12 10:05
 * @Since: 1.0
 */
public class HomePagePresenter extends BaseServicePresenter<IHomePageView, HomePageModel> {
    private static final String TAG = HomePagePresenter.class.getSimpleName();

    @Override
    protected HomePageModel createModel() {
        return new HomePageModel();
    }

    /**
     * 加载推荐列表信息
     */
    public void loadRecommendInfo() {
        Log.d(TAG, "loadRecommendInfo");
        if (isAttach()) {
            getView().showLoading();//显示加载进度
            execute(model.loadRecommendInfo(), recommendInfos -> {
                getView().hideLoading();//隐藏加载进度
                getView().onLoadRecommondInfo(recommendInfos);//回调推荐数据
            }, throwable -> {
                Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                getView().hideLoading();
                return true;
            }, aBoolean -> true);
        }
    }

    /**
     * 加载首页推荐信息列表
     * @param recommendInfoList 推荐信息列表
     * @param page 页码
     * @return 首页推荐数据
     */
    public List<IMultiItem> LoadMultiData(List<RecommendInfo> recommendInfoList, int page) {
        List<IMultiItem> hotRecommendList = new ArrayList<>(); //首页adapter Multi数据容器
        if(recommendInfoList != null && !recommendInfoList.isEmpty()) {
            int size = recommendInfoList.size();
            if(page <= 0) {
                page = 1;
            } else if(page > size) {
                page = size;
            }
            if(page == 1) { //如果是第一页，则遍历获取视频列表首个视频海报图作为banner数据
                HotRecommendBanner hotRecommendBanner = new HotRecommendBanner();//热门推荐banner
                for(RecommendInfo recommendInfo: recommendInfoList) { //遍历获取推荐数据
                    hotRecommendBanner.addRecommenBanner(convertRecommendBanner(recommendInfo)); //每个推荐频道视频列表中第一个视频信息作为热门推荐banner数据
                }
                hotRecommendList.add(hotRecommendBanner); //加入推荐banner数据
            }
            HotRecommendAlbumPic hotRecommendAlbumPic = convertHotRecommendAlbumPic(recommendInfoList.get(page - 1));//推荐频道海报图数据列表
            hotRecommendList.add(hotRecommendAlbumPic); //加载推荐频道数据
        }
        return hotRecommendList;
    }

    private HotRecommendBanner.RecommendBanner convertRecommendBanner(RecommendInfo recommendInfo) {
        HotRecommendBanner.RecommendBanner recommendBanner = null;
        if(recommendInfo != null) {
            List<VideoInfo> videoList = recommendInfo.getVideoList(); //获取视频信息列表
            if(videoList != null && !videoList.isEmpty()) {
                VideoInfo videoInfo = videoList.get(0); //获取列表中首个视频信息
                long qipuId = videoInfo.getQipuId(); //视频id
                long albumId = videoInfo.getAlbumId(); //剧集id
                String pic = videoInfo.getAlbumPic(); //海报图
                if (pic == null || pic.isEmpty()) { //如果海报图为空，则获取剧照图片
                    pic = videoInfo.getAlbumPic(); //获取剧集图片
                }
                String focus = videoInfo.getFocus(); //看点
                recommendBanner = new HotRecommendBanner.RecommendBanner(qipuId, albumId, pic, focus);//热门banner数据
            }
        }
        return recommendBanner;
    }

    private HotRecommendAlbumPic convertHotRecommendAlbumPic(RecommendInfo recommendInfo) {
        HotRecommendAlbumPic hotRecommendAlbumPic = new HotRecommendAlbumPic();//推荐频道海报图数据列表
        if(recommendInfo != null) {
            String chnId = recommendInfo.getChnId(); //频道id
            String chnName = recommendInfo.getChnName(); //频道名称
            List<VideoInfo> videoList = recommendInfo.getVideoList(); //获取视频列表
            if(videoList != null && !videoList.isEmpty()) {
                for(int i = 1; i <= 6; i++) { //遍历获取列表1-6个数据
                    VideoInfo videoInfo = videoList.get(i); //视频信息
                    long qipuId = videoInfo.getQipuId(); //视频id
                    long albumId = videoInfo.getAlbumId(); //剧集id
                    String albumPic = videoInfo.getAlbumPic();//剧照
                    String focus = videoInfo.getFocus(); //看点
                    String name = videoInfo.getName(); //视频名称
                    String shortName = videoInfo.getShortName(); //视频简称
                    HotRecommendAlbumPic.RecommendAlbumPic recommendAlbumPic = new HotRecommendAlbumPic.RecommendAlbumPic(Integer.parseInt(chnId), chnName, qipuId, albumId,
                            albumPic, focus, name, shortName, videoInfo.isExclusive(), videoInfo.isVip(), videoInfo.getQiyiProd() != 0);
                    hotRecommendAlbumPic.addRecommendAlbumPic(recommendAlbumPic);
                }
            }
        }
        return hotRecommendAlbumPic;
    }



    public void loadData() {
        Log.d(TAG, "loadData");
        if (isAttach()) {
            getView().showLoading();
            execute(model.loadRecommendInfo(),
                    recommendInfos -> {
                        Log.d(TAG, "loadRecommendInfo:" + recommendInfos);
                        List<IMultiItem> hotRecommendList = new ArrayList<>(); //adapter 数据容器
                        HotRecommendBanner hotRecommendBanner = new HotRecommendBanner();//热门banner
                        HotRecommendAlbumPic hotRecommendAlbumPic = new HotRecommendAlbumPic();//推荐频道海报图数据列表
                        List<ChannelTag> channelTags = new ArrayList<>();//推荐频道名称列表
                        for (RecommendInfo info : recommendInfos) { //遍历首页推荐数据
                            Log.d(TAG, "chnName:" + info.getChnName());
                            channelTags.add(new ChannelTag(info.getChnId(), info.getChnName()));//添加频道名称
                            List<VideoInfo> videoList = info.getVideoList();
                            if (videoList != null && !videoList.isEmpty()) {
                                int size = videoList.size();
                                Log.d(TAG, "size:" + size);
                                for (int i = 0; i < size; i++) { //遍历获取视频数据
                                    VideoInfo videoInfo = videoList.get(i);
                                    int chnId = videoInfo.getChnId(); //频道id
                                    String chnName = videoInfo.getChnName(); //频道名称
                                    long qipuId = videoInfo.getQipuId(); //视频id
                                    long albumId = videoInfo.getAlbumId(); //剧集id
                                    String pic = videoInfo.getPosterPic(); //海报图
                                    if (pic == null || pic.isEmpty()) { //如果海报图为空，则获取剧照图片
                                        pic = videoInfo.getAlbumPic();
                                    }
                                    String albumPic = videoInfo.getAlbumPic();//剧照
                                    String focus = videoInfo.getFocus(); //看点
                                    String name = videoInfo.getName(); //视频名称
                                    String shortName = videoInfo.getShortName(); //视频简称
                                    if (i == 0) { //每个频道首个海报图作为banner数据
                                        Log.d(TAG, "qipuId:" + qipuId + ",posterPic:" + pic + ",focus:" + focus);
                                        HotRecommendBanner.RecommendBanner recommendBanner = new HotRecommendBanner.RecommendBanner(qipuId, albumId, pic, focus);
                                        hotRecommendBanner.addRecommenBanner(recommendBanner); //添加banner
                                        continue;
                                    }

                                    HotRecommendAlbumPic.RecommendAlbumPic recommendAlbumPic = new HotRecommendAlbumPic.RecommendAlbumPic(chnId, chnName, qipuId, albumId,
                                            albumPic, focus, name, shortName, videoInfo.isExclusive(), videoInfo.isVip(), videoInfo.getQiyiProd() != 0);
                                    hotRecommendAlbumPic.addRecommendAlbumPic(recommendAlbumPic); //添加海报
                                }
                            }
                        }

                        hotRecommendList.add(hotRecommendBanner); //banner数据列表装入容器
                        hotRecommendList.add(hotRecommendAlbumPic); //频道海报列表装入容器

                        getView().hideLoading();
                        getView().onAllChannelTags(channelTags); //返回推荐频道列表
                        getView().onLoadData(hotRecommendList); //返回首页数据
                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
//                        getView().onLoadData(null);
                        getView().showLoadNetErrorView();
                        return true;
                    },
                    aBoolean -> {
                        return true;
                    });
        }
    }

    public void loadMoreData(String channelName, int pageNum, int pageSize) {
        Log.d(TAG, "loadMoreData");
        if (isAttach()) {
            execute(model.loadChannelInfo(channelName, pageNum, pageSize),
                    channelInfo -> {
                        Log.d(TAG, "loadChannelInfo:" + channelInfo);
                        List<IMultiItem> hotRecommendList = new ArrayList<>();
                        HotRecommendAlbumPic hotRecommendAlbumPic = new HotRecommendAlbumPic();
                        List<VideoInfo> videoInfoList = channelInfo.getVideos();
                        if (videoInfoList != null && !videoInfoList.isEmpty()) {
                            for (VideoInfo info : videoInfoList) {
                                int chnId = info.getChnId();
                                String chnName = info.getChnName();
                                long qipuId = info.getQipuId();
                                long albumId = info.getAlbumId();
                                String albumPic = info.getAlbumPic();
                                String focus = info.getFocus();
                                String name = info.getName();
                                String shortName = info.getShortName();
                                HotRecommendAlbumPic.RecommendAlbumPic recommendAlbumPic = new HotRecommendAlbumPic.RecommendAlbumPic(chnId, channelName, qipuId, albumId,
                                        albumPic, focus, name, shortName, info.isExclusive(), info.isVip(), info.getQiyiProd() != 0);
                                hotRecommendAlbumPic.addRecommendAlbumPic(recommendAlbumPic);
                            }
                        }
                        hotRecommendList.add(hotRecommendAlbumPic);
                        getView().onLoadMoreData(hotRecommendList);
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }

    public void showNetWorkError() {
        if (isAttach()) {
            getView().showLoadNetErrorView();
        }
    }

    public void hideNetWorkError() {
        if (isAttach()) {
            getView().hideLoadNetErrorView();
        }
    }

}
