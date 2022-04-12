package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集接口
 * @Time: 2021/8/23 16:03
 * @Since: 1.0
 */
public interface IPlayVideoInfoMoreSelectionsView extends IBaseView {
    public static final int TYPE_DIGITAL = 1;
    public static final int TYPE_TIME = 2;

    /**
     * 加载更多选集 (时间型）
     * @param infoList 剧集列表
     */
    void onLoadMoreSelectionByTime(EpisodeInfoList infoList);

    /**
     * 加载更多选集（数字型）
     * @param infoList 剧集列表
     */
    void onLoadMoreSelectionByDigital(EpisodeInfoList infoList);

}
