package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.PlayerModel;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoMoreSelectionsView;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoSynopsisView;
import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-剧集简介-逻辑处理
 * @Time: 2021/7/21 14:16
 * @Since: 1.0
 */
public class PlayVideoInfoSynopsisPresenter extends BaseServicePresenter<IPlayVideoInfoSynopsisView, PlayerModel> {
    private static final String TAG = PlayVideoInfoSynopsisPresenter.class.getSimpleName();

    private static final int MAX_NUM = 60; //每次最大条目

    @Override
    protected PlayerModel createModel() {
        return new PlayerModel();
    }

    //加载更多选集(网络加载）
    public void loadMoreSelection(long qipuId, int year,int pos, int num) {
        Log.d(TAG, "loadMoreSelection");
        if (isAttach()) {
            execute(model.queryEpisodeInfoList(qipuId, year, pos, num),
                    episodeInfoList -> {
                        Log.d(TAG, "queryEpisodeInfoList");
                        long sourceCode = episodeInfoList.getSourceCode();
                        List<VideoInfo> videoInfoList = episodeInfoList.getEpg();
                        if (sourceCode == 0) {
                            getView().onLoadVideoInfoList(videoInfoList, getView().TYPE_DIGITAL);
                        } else {
                            getView().onLoadVideoInfoList(videoInfoList, getView().TYPE_TIME);
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
     * 加载剧集详情信息
     * @param albumId 剧集id
     */
    public synchronized void loadVideoInfo(long albumId) {
        Log.d(TAG, "loadVideoInfo:" + albumId);
        if(albumId <= 0) {
            Log.e(TAG, "albumId is under 0");
            return;
        }
        execute(model.getVideoInfo(albumId),
                videoInfo -> {
                    if (isAttach()) {
                        getView().onLoadVideoInfo(videoInfo);
                    }
                },
                throwable -> {
                    Log.e(TAG, "error:"+ Log.getStackTraceString(throwable));
                    return true;
                },
                aBoolean -> false);
    }

    /**
     *  本地加载更多选集
     * @param num 集数
     */
    public void loadLocalMoreSelection(int num) {
        Log.d(TAG, "loadLocalMoreSelection");
        if (isAttach()) {
            EpisodeInfo episodeInfo = model.getEpisodeInfo();
            List<VideoInfo> videoInfoList = model.getVideoInfoList();
            if (episodeInfo != null && videoInfoList!= null && !videoInfoList.isEmpty()) {
                long sourceCode = episodeInfo.getSourceCode();
                List<VideoInfo> videoInfos = videoInfoList.stream().limit(num).collect(Collectors.toList());
                if (sourceCode == 0) {
                    getView().onLoadVideoInfoList(videoInfos, getView().TYPE_DIGITAL);
                } else {
                    getView().onLoadVideoInfoList(videoInfos, getView().TYPE_TIME);
                }
            } else {
                Log.e(TAG, "videoinfo list is null or empty!");
            }
        }
    }

    /**
     *  加载部分剧集
     * @param videoInfoList 剧集视频
     * @param num 多少集
     */
    public void loadLocalPartMoreSelection(List<VideoInfo> videoInfoList, int num) {
        if (isAttach()) {
            EpisodeInfo episodeInfo = model.getEpisodeInfo();
            if (episodeInfo != null && videoInfoList!= null && !videoInfoList.isEmpty()) {
                long sourceCode = episodeInfo.getSourceCode();
                List<VideoInfo> videoInfos = videoInfoList.stream().limit(num).collect(Collectors.toList());
                if (sourceCode == 0) {
                    getView().onLoadVideoInfoList(videoInfos, getView().TYPE_DIGITAL);
                } else {
                    getView().onLoadVideoInfoList(videoInfos, getView().TYPE_TIME);
                }
            }
        }
    }

    /**
     * 加载所有剧集列表信息
     * @param albumId 专辑id
     * @param year 年份
     * @param pos 位置
     */
    public void loadAllEpisodeInfoList(long albumId, int year, int pos) {
        Log.d(TAG, "loadAllEpisodeInfoList:{albumId:" + albumId + ",year:" + year + ",pos:" +  pos + "}");
        execute(model.queryEpisodeInfoList(albumId, year, pos, MAX_NUM),
                episodeInfoList -> {
                    boolean hasMore = episodeInfoList.isHasMore();
                    int nPos = episodeInfoList.getPos();
                    Log.d(TAG, "hasMore:" + hasMore + ",nPos:" + nPos);
                    List<VideoInfo> videoInfoList = episodeInfoList.getEpg();
                    Log.d(TAG, "add videoInfoList:" + videoInfoList);
                    if (model.getEpisodeInfo() == null) {
                        long total = episodeInfoList.getTotal();
                        String publishYear = episodeInfoList.getPublishYear();
                        int mixedCount = episodeInfoList.getMixedCount();
                        int chnId = episodeInfoList.getChnId();
                        String chnName = episodeInfoList.getChnName();
                        long sourceCode = episodeInfoList.getSourceCode();
                        long aId = episodeInfoList.getAlbumId();
                        String albumName = episodeInfoList.getAlbumName();
                        EpisodeInfo episodeInfo = new EpisodeInfo(total, mixedCount, nPos, publishYear, hasMore, chnId, chnName, sourceCode, aId, albumName);
                        model.setEpisodeInfo(episodeInfo);
                        model.setPartitionEnable(true); //启用分集
                    } else {
                        EpisodeInfo episodeInfo =  model.getEpisodeInfo();
                        episodeInfo.setPos(nPos);
                        episodeInfo.setHasMore(hasMore);
                    }
                    if(videoInfoList != null && !videoInfoList.isEmpty()) {
                        model.addVideoInfoList(videoInfoList);
                    }
                    if (hasMore) { //是否还有
                        loadAllEpisodeInfoList(albumId, year, nPos);
                    } else {
                        Log.d(TAG, "notifyLoadComplete");
                        model.notifyLoadComplete();//通知加载完成
                    }
                },
                throwable -> {
                    Log.e(TAG, "loadAllEpisodeInfoList error:"+ Log.getStackTraceString(throwable));
                    return true;
                },
                aBoolean -> false);
    }

    /**
     * 加载分集选集
     * @param partitionPos 分集位置
     */
    public void loadPartitionSelection(int partitionPos) {
        Log.d(TAG, "loadPartitionSelection:" + partitionPos);
        if(partitionPos == -1) {
            partitionPos = 0;
        }
        if(isAttach()) {
            showLoading();
            EpisodeInfo episodeInfo = model.getEpisodeInfo();
            List<VideoInfo> videoInfoList = model.getPartitionEpisode(partitionPos);
            if (episodeInfo != null && videoInfoList!= null && !videoInfoList.isEmpty()) {
                long sourceCode = episodeInfo.getSourceCode();
                if (sourceCode == 0) {
                    getView().onLoadVideoInfoList(videoInfoList, getView().TYPE_DIGITAL);
                } else {
                    getView().onLoadVideoInfoList(videoInfoList, getView().TYPE_TIME);
                }
            } else {
                Log.e(TAG, "videoInfo list is null");
            }
            hideLoading();
        }
    }

    public int getPartitionPos(VideoInfo videoInfo) {
        return model.getPartitionPos(videoInfo);
    }


}
