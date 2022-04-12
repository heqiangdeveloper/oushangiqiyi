package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.ClassifyDetailsModel;
import com.oushang.iqiyi.mvp.view.IClassifyDetailsView;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 频道详情处理逻辑
 * @Time: 2021/7/19 11:34
 * @Since: 1.0
 */
public class ClassifyDetailsPresenter extends BaseServicePresenter<IClassifyDetailsView, ClassifyDetailsModel> {
    private static final String TAG = ClassifyDetailsPresenter.class.getSimpleName();

    @Override
    protected ClassifyDetailsModel createModel() {
        return new ClassifyDetailsModel();
    }

    /**
     * 加载频道详情信息
     * @param channelName 频道名称
     * @param pageNum  页码
     * @param pageSize 数量
     */
    public void loadChannelInfos(String channelName, int pageNum, int pageSize) {
        Log.d(TAG, "loadChannelInfos");
        if (isAttach()) {
            execute(model.getChannelInfos(channelName, pageNum, pageSize),
                    channelInfo -> {
                        Log.d(TAG, "getChannelInfos");
                        if (channelInfo != null && channelInfo.getVideos() != null && !channelInfo.getVideos().isEmpty()) {
                            getView().onLoadChannelInfo(channelInfo);
                        } else {
                            getView().showLoadEmptyView();
                        }
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }

    /**
     * 加载第三级标签
     * @param channelTag 子标签
     */
    public void loadChannelThirdTag(ChannelTag channelTag) {
        if (isAttach()) {
            if(channelTag != null) {
                getView().onLoadChannelThirdTag(channelTag);
            }
        }
    }

    /**
     * 筛选频道详情
     * @param channelName 频道名称
     * @param pageNum 第几页
     * @param pageSize 数量
     * @param treeCateGoryId 第三级标签
     */
    public void siftChannelInfos(String channelName, int pageNum, int pageSize, String treeCateGoryId) { //筛选频道详情
        Log.d(TAG, "siftChannelInfos:" + treeCateGoryId);
        if (isAttach()) {
            execute(model.getChannelInfos(channelName, pageNum, pageSize, treeCateGoryId),
                    channelInfo -> {
                        Log.d(TAG, "siftChannelInfos:" + channelInfo);
                        if (channelInfo != null && channelInfo.getVideos() != null && !channelInfo.getVideos().isEmpty()) {
                            //针对付费,有的数据返回的vipInfo内容为空，对应的videoInfo.isVip()为false，需要去掉，bug1098510
                            List<VideoInfo> videoInfos = null;
                            if(treeCateGoryId.contains("VIP")){
                                //过滤掉list,留下videoInfo.isVip()=true的
                                videoInfos = channelInfo.getVideos().stream().filter(videoInfo -> videoInfo.isVip()).collect(Collectors.toList());
                                channelInfo.setVideos(videoInfos);
                            }else if(treeCateGoryId.contains("FREE")){
                                //过滤掉list,留下videoInfo.isVip()=false的
                                videoInfos = channelInfo.getVideos().stream().filter(videoInfo -> !videoInfo.isVip()).collect(Collectors.toList());
                                channelInfo.setVideos(videoInfos);
                            }

                            getView().onSiftChannelInfo(channelInfo);
                        } else {
                            getView().showLoadEmptyView();
                        }

                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }

    /**
     * 加载更多
     * @param channelName 频道名称
     * @param pageNum 页码
     * @param pageSize 数量
     * @param treeCateGoryId 第三级标签
     */
    public void loadMoreChannelInfos(String channelName, int pageNum, int pageSize, String treeCateGoryId) {
        Log.d(TAG, "loadMoreChannelInfos：{" + "channelName:" + channelName + ",pageNum:" + pageNum + ",pageSize:" + pageSize + ",treeCateGoryId:" + treeCateGoryId + "}");
        if (isAttach()) {
            execute(model.getChannelInfos(channelName, pageNum, pageSize, treeCateGoryId),
                    channelInfo -> {
                        Log.d(TAG, "getChannelInfos");
                        //针对付费,有的数据返回的vipInfo内容为空，对应的videoInfo.isVip()为false，需要去掉，bug1098510
                        List<VideoInfo> videoInfos = null;
                        if(treeCateGoryId.contains("VIP")){
                            //过滤掉list,留下videoInfo.isVip()=true的
                            videoInfos = channelInfo.getVideos().stream().filter(videoInfo -> videoInfo.isVip()).collect(Collectors.toList());
                            channelInfo.setVideos(videoInfos);
                        }else if(treeCateGoryId.contains("FREE")){
                            //过滤掉list,留下videoInfo.isVip()=false的
                            videoInfos = channelInfo.getVideos().stream().filter(videoInfo -> !videoInfo.isVip()).collect(Collectors.toList());
                            channelInfo.setVideos(videoInfos);
                        }
                        getView().onLoadChannelInfoMore(channelInfo);
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }
}
