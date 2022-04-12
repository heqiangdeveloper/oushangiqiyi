package com.oushang.lib_service.impls;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.common.collect.Lists;
import com.oushang.lib_service.callback.IPlayListChangeListener;
import com.oushang.lib_service.callback.IPlayListLoadCompleteListener;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayListManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放列表管理服务（剧集）
 * @Date: 2021/6/25
 */
@Route(path = Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
public class PlayListManagerService  implements PlayListManager {
    private static final String TAG = PlayListManagerService.class.getSimpleName();

    public static final int DEFAULT_PARTITION_NUM = 10;//默认分集划分的数量

    //剧集信息
    private EpisodeInfo mEpisodeInfo;

    //视频信息，播单列表
    private List<VideoInfo> mVideoInfoList = new ArrayList<>();

    //是否开启分集
    private boolean isEnablePartition = false;

    //分集数
    private int mPartitionNum = DEFAULT_PARTITION_NUM;

    //当前分集位置
    private volatile int mCurrentPartitionPos = 0;

    //分集个数
    private int mPartitionSize = 0;

    //分集列表
    private volatile List<List<VideoInfo>> mPartitionEpisode;

    //播单列表监听
    private List<IPlayListChangeListener> mPlayListChangeListenerList = new ArrayList<>();

    //剧集加载完成回调
    private List<IPlayListLoadCompleteListener> mPlayListLoadCompleteListenerList = new ArrayList<>();

    //当前播放位置
    private volatile int mCurrentPlayPos;

    public PlayListManagerService() {}

    @Override
    public void init(Context context) {

    }

    /**
     * 播放位置移到上一个
     * @return true or false
     */
    @Override
    public synchronized boolean moveToPrev() {
        mCurrentPlayPos--;
        if (mCurrentPlayPos < 0) {
            mCurrentPlayPos = 0;
        }
        return mCurrentPlayPos > 0;
    }

    /**
     * 播放位置移到下一个
     * @return true or false
     */
    @Override
    public synchronized boolean moveToNext() {
        mCurrentPlayPos++;
        if (mCurrentPlayPos >= mVideoInfoList.size()) {
            mCurrentPlayPos = 0;
        }
        return mCurrentPlayPos < mVideoInfoList.size();
    }

    /**
     * 更新当前播单位置
     * @param pos 位置
     */
    @Override
    public synchronized void updatePlayPos(int pos) {
        Log.d(TAG, "update play pos:" + pos);
        if (pos >= mVideoInfoList.size()) {
            pos = 0;
        }
        mCurrentPlayPos = pos;
    }

    /**
     * 获取上一个视频
     * @return 视频信息
     */
    @Override
    public VideoInfo setToPrev() {
        if (mVideoInfoList == null || mVideoInfoList.isEmpty()) {
            return null;
        }
        int pos = mCurrentPlayPos -1;
        if (pos < 0) {
            pos = 0;
        }
        return mVideoInfoList.get(pos);
    }

    /**
     * 获取下一个视频
     * @return 视频信息
     */
    @Override
    public VideoInfo setToNext() {
        if (mVideoInfoList == null || mVideoInfoList.isEmpty()) {
            return null;
        }
        int pos = mCurrentPlayPos + 1;
        if (pos >= mVideoInfoList.size()) {
            pos = mVideoInfoList.size();
        }
        if (pos < 0) {
            pos = 0;
        }
        return mVideoInfoList.get(pos);
    }

    /**
     * 获取当前播放的位置
     * @return 位置
     */
    @Override
    public int getCurrentPlayPos() {
        return mCurrentPlayPos;
    }

    /**
     * 获取当前视频信息
     * @return 视频信息
     */
    @Override
    public VideoInfo getCurrentVideoInfo() {
        if (mVideoInfoList != null && !mVideoInfoList.isEmpty()) {
            if(mCurrentPlayPos >= 0 && mCurrentPlayPos < mVideoInfoList.size()) {
                Log.d(TAG, "mCurrentPlayPos:" + mCurrentPlayPos);
                return mVideoInfoList.get(mCurrentPlayPos);
            }
        }
        return null;
    }

    /**
     * 获取视频位置
     * @param videoInfo 视频
     * @return 位置 不存在返回-1
     */
    @Override
    public int getPos(VideoInfo videoInfo) {
        if (mVideoInfoList != null && !mVideoInfoList.isEmpty()) {
            return mVideoInfoList.indexOf(videoInfo);
        }
        return -1;
    }

    /**
     * 是否是第一个位置
     * @return true of false
     */
    @Override
    public boolean isFirstPos() {
        return mCurrentPlayPos == 0;
    }

    /**
     * 是否是最后一个位置
     * @return true of false
     */
    @Override
    public boolean isLastPos() {
        if (mVideoInfoList == null || mVideoInfoList.isEmpty()) { //如果是null或空，则视为最后一个位置
            return true;
        }
        return mCurrentPlayPos == mVideoInfoList.size() -1;
    }

    /**
     * 清除数据
     */
    @Override
    public synchronized void clear() {
        mCurrentPlayPos = 0; //当前播放位置复位
        mEpisodeInfo = null; //清空专辑/剧集信息
        mVideoInfoList.clear(); //清空视频信息列表
        isEnablePartition = false; //开启分集开关复位
        mCurrentPartitionPos = 0; //当前分集位置复位
        mPartitionEpisode = null; //清空分集列表
    }

    /**
     * 设置专辑/剧集信息
     * @param episodeInfo 剧集信息
     */
    @Override
    public void setEpisodeInfo(EpisodeInfo episodeInfo) {
        this.mEpisodeInfo = episodeInfo;
    }

    /**
     * 获取专辑/剧集信息
     * @return 剧集信息
     */
    @Override
    public EpisodeInfo getEpisodeInfo() {
        return mEpisodeInfo;
    }

    /**
     * 是否启用分集
     * @param enable true 启用，false 不启用
     */
    @Override
    public void setPartitionEnable(boolean enable) {
        this.isEnablePartition = enable;
    }

    /**
     * 是否已启用分集
     * @return true 是，false 否
     */
    @Override
    public boolean isPartitionEnable() {
        return this.isEnablePartition;
    }

    /**
     *  设置分集，默认 @see #DEFAULT_PARTTION_NUM
     * @param partitionNum 分集数
     */
    @Override
    public void setPartitionNum(int partitionNum) {
        this.mPartitionNum = partitionNum;
    }

    /**
     * 获取当前分集位置
     * @return 分集位置
     */
    @Override
    public int getCurrentPartitionPos() {
        return mCurrentPartitionPos;
    }

    @Override
    public int getPartitionSize() {
        return mPartitionSize;
    }

    /**
     * 获取分集视频列表
     * @param partitionPos 分集位置
     * @return 分集视频列表
     */
    @Override
    public List<VideoInfo> getPartitionEpisode(int partitionPos) {
        if(mPartitionEpisode != null && !mPartitionEpisode.isEmpty()) {
            int size = mPartitionEpisode.size();
            if (partitionPos >= 0 && partitionPos < size) {
                return mPartitionEpisode.get(partitionPos);
            }
        } else {
            Log.e(TAG, "PartitionEpisode in null");
        }
        return null;
    }

    /**
     * 移到上一个分集位置
     * @return true or false
     */
    @Override
    public synchronized boolean moveToPrePartition() {
        if (!isEnablePartition || mPartitionEpisode == null || mPartitionEpisode.isEmpty()) {
            return false;
        }
        mCurrentPartitionPos --;
        if (mCurrentPartitionPos < 0) {
            mCurrentPartitionPos = 0;
        }

        return mCurrentPartitionPos > 0;
    }

    /**
     * 移到下一个分集位置
     * @return true or false
     */
    @Override
    public synchronized boolean moveToNextPartition() {
        if (!isEnablePartition || mPartitionEpisode == null || mPartitionEpisode.isEmpty()) {
            return false;
        }
        mCurrentPartitionPos ++;
        if (mCurrentPartitionPos >= mPartitionEpisode.size()) {
            mCurrentPartitionPos = 0;
        }
        return mCurrentPartitionPos < mPartitionEpisode.size();
    }

    /**
     * 更新分集位置
     * @param partitionPos 分集位置
     */
    @Override
    public synchronized void updatePartitionPos(int partitionPos) {
        this.mCurrentPartitionPos = partitionPos;
    }

    /**
     * 获取视频在分集列表中的位置
     * @param videoInfo 视频信息
     * @return 分集的位置
     */
    @Override
    public synchronized int getPartitionPos(VideoInfo videoInfo) {
        if(mPartitionEpisode != null && !mPartitionEpisode.isEmpty()) {
            int size = mPartitionEpisode.size();
            for(int pos = 0; pos < size; pos++) {
                List<VideoInfo> videoInfos = mPartitionEpisode.get(pos);
                for(VideoInfo info: videoInfos) {
                    if(info.equals(videoInfo)) {
                        return pos;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 获取播单列表
     * @return 视频列表
     */
    @Override
    public List<VideoInfo> getVideoInfoList() {
        return mVideoInfoList;
    }

    /**
     * 添加视频信息至播单列表
     * @param videoInfo 视频信息
     */
    @Override
    public void addVideoInfo(VideoInfo videoInfo) {
        mVideoInfoList.add(videoInfo);
    }

    /**
     *  添加视频信息列表至播放列表
     * @param videoInfoList 视频信息列表
     */
    @Override
    public synchronized void addVideoInfoList(List<VideoInfo> videoInfoList) {
        if (mVideoInfoList != null) {
            mVideoInfoList.addAll(videoInfoList);
        }
    }

    /**
     * 更新播放视频列表
     * @param videoInfoList 视频列表
     */
    @Override
    public void updateVideoInfoList(List<VideoInfo> videoInfoList) {
        mVideoInfoList.clear();
        mVideoInfoList = videoInfoList;
        mCurrentPlayPos = 0;
        if (mPlayListChangeListenerList != null) {
            for (IPlayListChangeListener listener: mPlayListChangeListenerList) {
                listener.onChange(mVideoInfoList);
            }
        }
    }

    /**
     * 注册播单列表变更监听
     * @param listener 变更监听
     */
    @Override
    public void addPlayListChangeListener(IPlayListChangeListener listener) {
        if (mPlayListChangeListenerList != null && !mPlayListChangeListenerList.contains(listener)) {
            mPlayListChangeListenerList.add(listener);
        }

    }

    /**
     * 移除播单列表变更监听
     * @param listener 变更监听
     */
    @Override
    public void removePlayListChangeListener(IPlayListChangeListener listener) {
        if (listener != null && mPlayListChangeListenerList != null && mPlayListChangeListenerList.contains(listener)) {
            mPlayListChangeListenerList.remove(listener);
        }
    }

    /**
     *  加载完成监听
     * @param listener 完成监听
     */
    @Override
    public void addLoadCompleteListener(IPlayListLoadCompleteListener listener) {
        Log.d(TAG, "addLoadCompleteListener:" + listener);
        if (mPlayListLoadCompleteListenerList != null) {
            Log.d(TAG, "add listener:" + listener);
            mPlayListLoadCompleteListenerList.add(listener);
        }
    }

    /**
     * 通知完成
     */
    @Override
    public void notifyLoadComplete() {
        if (mPlayListLoadCompleteListenerList != null && !mPlayListLoadCompleteListenerList.isEmpty()) {
            if (isEnablePartition) {
                Log.d(TAG, "partition enable true");
                mPartitionEpisode = Lists.partition(mVideoInfoList, mPartitionNum);
                mPartitionSize = mPartitionEpisode.size();
            }
            for (IPlayListLoadCompleteListener listener: mPlayListLoadCompleteListenerList) {
                Log.d(TAG, "notify listener:" + listener);
                listener.onCompleted();
            }
        } else {
            Log.e(TAG, "PlayListLoadCompleteListenerList is null or empty!");
        }
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.d(TAG, "onDestroy");
        clear();
        mPlayListLoadCompleteListenerList.clear();
    }
}
