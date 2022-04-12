package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放视频信息-剧集简介接口
 * @Time: 2021/7/21 14:18
 * @Since: 1.0
 */
public interface IPlayVideoInfoSynopsisView extends IBaseView {

    int TYPE_DIGITAL = 1; //数字型
    int TYPE_TIME = 2; //时间型

    /**
     * 加载视频信息（剧集简介）
     * @param videoInfo 视频信息
     */
    void onLoadVideoInfo(VideoInfo videoInfo);

    /**
     * 加载视频选集
     * @param videoInfoList 选集视频列表
     * @param type 选集类型
     * @see {@link #TYPE_DIGITAL} {@link #TYPE_TIME}
     *
     */
    void onLoadVideoInfoList(List<VideoInfo> videoInfoList, int type);
}
