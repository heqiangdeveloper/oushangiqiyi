package com.oushang.lib_service.interfaces;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.callback.IPlayListChangeListener;
import com.oushang.lib_service.callback.IPlayListLoadCompleteListener;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.VideoInfo;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放列表管理
 * @Date: 2021/6/25
 */
public interface PlayListManager extends IProvider, LifecycleObserver {

    /**
     * 移动到上一个位置
     * @return true or false
     */
    boolean moveToPrev();

    /**
     * 移动到下一个位置
     * @return true or false
     */
    boolean moveToNext();

    /**
     * 更新播放位置
     * @param pos 位置
     */
    void updatePlayPos(int pos);

    /**
     * 获取上一个视频
     * @return 视频信息
     */
    VideoInfo setToPrev();

    /**
     * 获取下一个视频
     * @return 视频信息
     */
    VideoInfo setToNext();

    /**
     * 获取当前播放位置
     * @return 位置
     */
    int getCurrentPlayPos();

    /**
     * 获取当前播放的视频
     * @return 视频信息
     */
    VideoInfo getCurrentVideoInfo();

    /**
     * 获取视频的位置
     * @param videoInfo 视频信息
     * @return 位置，-1表示未找到
     */
    int getPos(VideoInfo videoInfo);

    /**
     * 是否是第一个位置
     * @return true or false
     */
    boolean isFirstPos();

    /**
     * 是否是最后一个位置
     * @return true or false
     */
    boolean isLastPos();

    /**
     * 清空
     */
    void clear();

    /**
     * 设置专辑/剧集信息
     * @param episodeInfo 剧集信息
     */
    void setEpisodeInfo(EpisodeInfo episodeInfo);

    /**
     * 获取专辑/剧集信息
     * @return 剧集信息
     */
    EpisodeInfo getEpisodeInfo();

    /**
     * 是否启用分集
     * @param enable true 启用，false 不启用
     */
    void setPartitionEnable(boolean enable);

    /**
     * 是否已启用分集
     * @return true 是，false 否
     */
    boolean isPartitionEnable();

    /**
     *  设置分集数量
     * @param partitionNum 分集数量
     */
    void setPartitionNum(int partitionNum);

    /**
     * 获取当前分集位置
     * @return 分集位置
     */
    int getCurrentPartitionPos();

    /**
     * 获取分集个数
     * @return 分集个数
     */
    int getPartitionSize();

    /**
     * 获取分集列表
     * @param partitionPos 分集位置
     * @return 分集列表
     */
    List<VideoInfo> getPartitionEpisode(int partitionPos);

    /**
     * 移到上一个分集位置
     * @return true , false
     */
    boolean moveToPrePartition();

    /**
     * 移动到下一个分集付位置
     * @return 移动成功
     */
    boolean moveToNextPartition();

    /**
     * 更新分集位置
     * @param partitionPos 分集位置
     */
    void updatePartitionPos(int partitionPos);

    /**
     * 获取视频在分集列表的位置
     * @param videoInfo 视频信息
     * @return 分集列表的位置
     */
    int getPartitionPos(VideoInfo videoInfo);

    /**
     * 获取播放视频列表信息
     * @return 视频列表
     */
    List<VideoInfo> getVideoInfoList();

    void addVideoInfo(VideoInfo videoInfo);

    /**
     * 添加播放视频列表
     * @param videoInfoList 视频列表
     */
    void addVideoInfoList(List<VideoInfo> videoInfoList);

    void updateVideoInfoList(List<VideoInfo> videoInfoList);

    void addPlayListChangeListener(IPlayListChangeListener listener);

    void removePlayListChangeListener(IPlayListChangeListener listener);

    /**
     * 添加视频列表加载完成监听
     * @param listener 视频列表加载完成监听
     */
    void addLoadCompleteListener(IPlayListLoadCompleteListener listener);

    /**
     * 通知视频列表加载完成
     */
    void notifyLoadComplete();

    /**
     *
     * 以下是生命周期回调
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    default void onCreate(@NonNull LifecycleOwner owner){}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    default void onStart(@NonNull LifecycleOwner owner){}

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    default void onResume(@NonNull LifecycleOwner owner){}

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    default void onPause(@NonNull LifecycleOwner owner){}

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    default void onStop(@NonNull LifecycleOwner owner){}

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    default void onDestroy(@NonNull LifecycleOwner owner){}

}
