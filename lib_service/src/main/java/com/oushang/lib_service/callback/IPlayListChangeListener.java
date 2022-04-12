package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放列表变更监听
 * @Date: 2021/6/25
 */
public interface IPlayListChangeListener {

    void onChange(List<VideoInfo> videoInfoList);
}
