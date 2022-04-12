package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 分类详情接口
 * @Time: 2021/7/19 14:58
 * @Since: 1.0
 */
public interface IClassifyDetailsView extends IBaseView {

    //回调加载频道详情信息
    void onLoadChannelInfo(ChannelInfo channelInfo);

    //回调加载频道第三级标签
    void onLoadChannelThirdTag(ChannelTag parent);

    //筛选频道详情信息
    void onSiftChannelInfo(ChannelInfo channelInfo);

    //加载更多频道详情信息
    void onLoadChannelInfoMore(ChannelInfo channelInfo);
}
