package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.VideoInfo;

/**
 * @Author: zeelang
 * @Description: 更新播放视频监听
 * @Time: 2021/10/26 0026  13:49
 * @Since: 1.0
 */
public interface IPlayVideoInfoChangeListener {

    void onChange(VideoInfo videoInfo);

}
