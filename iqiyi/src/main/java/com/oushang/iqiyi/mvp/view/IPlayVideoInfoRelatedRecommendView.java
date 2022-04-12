package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 相关推荐view接口
 * @Time: 2021/7/27 15:18
 * @Since: 1.0
 */
public interface IPlayVideoInfoRelatedRecommendView extends IBaseView {

    void onRelatedRecommendVideoInfo(List<VideoInfo> videoInfos);


}
