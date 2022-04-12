package com.oushang.iqiyi.entries;

import com.oushang.iqiyi.ui.ISpanItem;
import com.oushang.lib_service.entries.VideoInfo;

/**
 * @Author: zeelang
 * @Description: 搜索结果信息
 * @Time: 2021/12/16 0016  16:41
 * @Since: 1.0
 */
public class SearchResultInfo implements ISpanItem {
    private VideoInfo videoInfo;

    public SearchResultInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    public int getViewType() {
        return MutiType.IMAGE;
    }

    @Override
    public int getSpanSize() {
        return 1;
    }
}
