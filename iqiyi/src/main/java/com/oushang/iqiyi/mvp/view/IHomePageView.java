package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.entries.RecommendInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 首页view接口
 * @Time: 2021/7/12 10:14
 * @Since: 1.0
 */
public interface IHomePageView extends IBaseView {

    //回调加载推荐信息
    void onLoadRecommondInfo(List<RecommendInfo> recommendInfoList);

    //回调加载数据
    void onLoadData(List<IMultiItem> data);

    void onLoadMoreData(List<IMultiItem> data);

    void onAllChannelTags(List<ChannelTag> channelTags);

}
