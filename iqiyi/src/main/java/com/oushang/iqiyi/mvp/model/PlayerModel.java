package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;
import com.oushang.lib_service.interfaces.VideoManager;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 播放数据模型
 * @Time: 2021/7/20 17:32
 * @Since: 1.0
 */
public class PlayerModel extends BaseServiceModel {
    private static final String TAG = PlayerModel.class.getSimpleName();

    @Autowired(name = Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager; //视频管理

    @Autowired(name = Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager; //播放列表管理

    /**
     * 获取视频详情信息
     * @param qipuId 专辑/视频id
     * @return 视频详情信息
     */
    public Observable<VideoInfo> getVideoInfo(long qipuId) {
        return mVideoManager.getVideoInfo(qipuId);
    }

    /**
     * 获取剧集列表
     * @param qipuId 专辑/视频id
     * @param year 年份
     * @param pos  页码
     * @param num  数量
     * @return 剧集列表
     */
    public Observable<EpisodeInfoList> queryEpisodeInfoList(long qipuId, int year, int pos, int num) {
        return mVideoManager.getEpisodeInfoList(qipuId, year, pos, num);
    }

    /**
     * 添加收藏记录
     * @param callback 收藏回调
     * @param record 收藏记录
     */
    public void addFavoriteRecord(ISubscriptionCallback callback, FavoriteRecord record) {
        mPlayManager.subScript(callback, record);
    }

    /**
     * 添加视频列表信息
     * @param videoInfoList 视频列表
     */
    public void addVideoInfoList(List<VideoInfo> videoInfoList) {
        mPlayListManager.addVideoInfoList(videoInfoList);
    }

    /**
     * 获取收藏记录列表
     * @param callback 收藏记录列表回调
     */
    public void getSubscriptionList(ISubscriptionListCallback callback) {
        mPlayManager.getSubscriptionList(callback);
    }

    /**
     * 移除收藏记录
     * @param callback 移除收藏记录回调
     * @param record 收藏记录
     */
    public void removeFavoirteRecord(ISubscriptionCallback callback, FavoriteRecord record) {
        mPlayManager.unSubscript(callback, record);
    }

    /**
     * 获取剧集所有视频信息列表
     * @return 视频信息列表
     */
    public List<VideoInfo> getVideoInfoList() {
        return mPlayListManager.getVideoInfoList();
    }

    /**
     * 设置剧集信息
     * @param episodeInfo 剧集信息
     */
    public void setEpisodeInfo(EpisodeInfo episodeInfo) {
        mPlayListManager.setEpisodeInfo(episodeInfo);
    }

    /**
     * 获取剧集信息
     * @return 剧集信息
     */
    public EpisodeInfo getEpisodeInfo() {
        return mPlayListManager.getEpisodeInfo();
    }

    /**
     * 通知剧集加载完成
     */
    public void notifyLoadComplete() {
        mPlayListManager.notifyLoadComplete();
    }

    /**
     * 是否全屏
     * @return true 是， false 否
     */
    public boolean isFullScreen() {
        return mPlayManager.isFullScreen();
    }

    /**
     * 启用分集
     * @param enable true 启用，false 不启用
     */
    public void setPartitionEnable(boolean enable) {
        mPlayListManager.setPartitionEnable(enable);
    }

    /**
     * 获取分集剧集视频列表
     * @param partitionPos 分集位置
     * @return 剧集视频列表
     */
    public List<VideoInfo> getPartitionEpisode(int partitionPos) {
        return mPlayListManager.getPartitionEpisode(partitionPos);
    }

    public int getPartitionSize() {
        return mPlayListManager.getPartitionSize();
    }

    public int getPartitionPos(VideoInfo videoInfo) {
        return mPlayListManager.getPartitionPos(videoInfo);
    }

    /**
     * 获取本地历史记录
     * @return 历史记录列表
     */
    public List<HistoryRecord> loadHistoryRecord() {
        return mPlayManager.getLocalRecord();
    }

    /**
     * 获取历史记录
     * @param pageNum 页码
     * @param callback 回调
     * @param isMerge 是否合并，已废弃
     */
    public void loadHistoryRecordByPage(int pageNum, IDownloadCallback callback, boolean isMerge) {
        mPlayManager.downloadRecordByPage(pageNum, callback, isMerge);
    }

}
