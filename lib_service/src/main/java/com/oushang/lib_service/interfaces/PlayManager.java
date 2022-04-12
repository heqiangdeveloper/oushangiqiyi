package com.oushang.lib_service.interfaces;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.callback.IDeleteRecordListCallback;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.IPlayVideoInfoChangeListener;
import com.oushang.lib_service.callback.IPlayerFullScreenListener;
import com.oushang.lib_service.callback.IPlayerWindowListener;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.player.IPlayView;
import com.oushang.lib_service.player.IPlayer;
import com.oushang.lib_service.player.PlayerState;

import androidx.annotation.NonNull;

import java.util.List;

public interface PlayManager extends IProvider, LifecycleObserver {

    public static final int PLAYER_TYPE_IQIYI = 1;//爱奇艺播放器

    /**
     * 创建播放器
     * @param playerType 播放器类型
     * @return 播放器实例
     */
    IPlayer createPlayer(int playerType);

    /**
     *  设置播放器接口
     * @param iPlayer  播放器接口
     */
    void setPlayer(IPlayer iPlayer);

    /**
     * 设置播放视图接口
     * @param iPlayView 播放视图接口
     */
    void setPlayView(IPlayView iPlayView);

    /**
     *  设置当前播放视频信息
     * @param videoInfo 视频信息
     */
    void setCurrentVideoInfo(VideoInfo videoInfo);

    /**
     * 获取当前播放的视频信息
     * @return VideoInfo 视频信息
     */
    VideoInfo getCurrentVideoInfo();

    /**
     * 播放上一个节目（剧集）
     *
     */
    void playPrev();

    /**
     * 播放上一个节目（剧集）
     *
     */
    void playNext();

    /**
     * 播放 (剧集）
     */
    void play();

    /**
     * 播放视频
     * @param videoInfo 视频信息
     */
    void play(VideoInfo videoInfo);

    /**
     * 起播
     * @param albumId 专辑id
     * @param qipuId 视频id
     */
    void doPlay(long albumId, long qipuId);

    /**
     * 开始/继续播放
     */
    void start();

    void start(boolean clearUserPause);

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 暂停播放
     * @param fromUser 是否是用户操作
     */
    void pause(boolean fromUser);

    /**
     * 停止播放
     */
    void stop();

    void stop(boolean val);

    /**
     * 当前播放视频是否是专辑
     * @return 是 true, 否 false
     */
    boolean isAlbum();

    /**
     * 判断播放器是否正在播放
     * @return true 是，false 否
     */
    boolean isPlaying();

    /**
     * 判断播放器是否暂停
     * @return true 是，false 否
     */
    boolean isPaused();

    /**
     * 是否睡眠
     * @return true 是，false 否
     */
    boolean isSleeping();

    /**
     * 是否全屏
     * @return 是 true, 否 false
     */
    boolean isFullScreen();

    /**
     * 跳转到设置时间
     * @param pos
     */
    void seekTo(long pos);

    /**
     * 设置是否跳过片头片尾
     * @param skip true 表示跳过，false 表示不跳过
     */
    void skipHeadAndTail(boolean skip);

    /**
     * 设置全屏
     * @param full 是否全屏
     */
    void setFullScreen(boolean full);

    /**
     * 切换清晰度
     * @param bitStream 清晰度
     */
    void switchBitStream(int bitStream);

    /**
     * 获取视频播放长度
     * @return long
     */
    long getDuration();

    /**
     * 获取当前播放时间
     * @return long
     */
    long getCurrentPos();

    /**
     * 唤醒播放器，播放器时恢复到前台时调用
     */
    void wakeUpPlayer();

    /**
     * 休眠播放器，播放器切换到后台时调用
     */
    void sleepPlayer();


    /**
     * 获取当前播放状态
     * @return 播放状态
     */
    int getCurrentPlayState();

    boolean hasAudioFocus();

    /**
     * 更新播放状态
     * @param state 播放状态
     */
    void updatePlayState(@PlayerState.State int state);


    /**
     * 更新当前播放视频
     * @param videoInfo 视频信息
     */
    void updatePlayVideoInfo(VideoInfo videoInfo);


    /**
     * 全屏播放监听
     * @param listener 监听
     */
    void setPlayerFullScreenListener(IPlayerFullScreenListener listener);

    /**
     * 添加播放视频监听
     * @param listener 播放视频监听
     */
    void addPlayVideoInfoChangeListener(IPlayVideoInfoChangeListener listener);

    /**
     * 移除播放视频监听
     * @param listener 播放视频监听
     */
    void removePlayVideoInfoChangeListener(IPlayVideoInfoChangeListener listener);

    /**
     * 添加播放窗口监听
     * @param listener 播放窗口监听
     */
    void addPlayerWindowListener(IPlayerWindowListener listener);

    /**
     * 移除播放窗口监听
     * @param listener 播放窗口监听
     */
    void removePlayerWindowListener(IPlayerWindowListener listener);

    /**
     * 释放播放器
     */
    void releasePlayer();


    /**
     * 获取本地播放记录
     * @return 本地播放记录
     */
    List<HistoryRecord> getLocalRecord();

    /**
     * 分页加载云端播放记录
     * @param pageNum 分页
     * @param callback  加载回调
     * @param isMerge 是否合并本地数据
     */
    void downloadRecordByPage(int pageNum, IDownloadCallback callback, boolean isMerge);

    /**
     * 删除播放记录，clear 用于判断是不是要清空
     * @param records 播放记录列表
     * @param needClear 是否清空
     * @param callback 删除回调，成功或失败
     */
    void deleteRecord(List<HistoryRecord> records, boolean needClear, IDeleteRecordListCallback callback);

    /**
     * 清空历史记录
     * @param callback 回调
     */
    void clearHistoryRecord(IDeleteRecordListCallback callback);

    /**
     * 清空收藏记录
     * @param callback 回调
     */
    void clearFavoriteRecord(ISubscriptionCallback callback);

    /**
     * 添加收藏
     * @param callback 收藏回调
     * @param record 收藏记录
     */
    void subScript(ISubscriptionCallback callback, FavoriteRecord record);

    /**
     * 删除收藏记录
     * @param callback 收藏回调
     * @param favoriteRecords 收藏记录
     */
    void unSubscript(ISubscriptionCallback callback, FavoriteRecord... favoriteRecords);

    /**
     * 获取收藏记录
     * @param callback 收藏记录回调
     */
    void getSubscriptionList(ISubscriptionListCallback callback);

    /**
     *
     * 以下是生命周期回调
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(@NonNull LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(@NonNull LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(@NonNull LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(@NonNull LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop(@NonNull LifecycleOwner owner);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NonNull LifecycleOwner owner);

}
