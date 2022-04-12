package com.oushang.iqiyi.mvp.view;

import android.os.Bundle;

import com.oushang.iqiyi.player.PlayerContentEvent;
import com.oushang.iqiyi.player.PlayerControlEvent;
import com.oushang.iqiyi.player.PlayerPreLoadEvent;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.VideoInfo;

/**
 * @Author: zeelang
 * @Description: 播放接口
 * @Time: 2021/7/20 17:30
 * @Since: 1.0
 */
public interface IPlayerView extends IBaseView {

    /**
     * 播放视频信息
     *
     * @param videoInfo 视频信息
     */
    void onPlayerVideoInfo(VideoInfo videoInfo);


    /**
     * 播放内容
     *
     * @param event 播放内容事件
     * @param args  事件参数
     */
    void onPlayerContentEventListener(int event, Bundle args);

    /**
     * 播放控件
     *
     * @param event 播放控制事件
     */
    void onPlayerControlEventListener(int event);

    /**
     * 是否收藏
     * @param isFavorite true 已收藏， false 未收藏
     */
    void onSubscription(boolean isFavorite);




}
