package com.oushang.iqiyi.mvp.view;

import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.entries.ChannelTag;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 分类view接口
 * @Time: 2021/7/12 10:15
 * @Since: 1.0
 */
public interface IClassifyView extends IBaseView {

    //回调常用频道
    default void onLoadCommonChannelTagList(List<ChannelTag> channelTagList){};

    //回调所有频道
    default void onLoadAllChannelTagList(List<ChannelTag> channelTagList){};


    default void onLoadCommonChannelParentTag(List<ChannelParentTag> channelParentTagList){};

    default void onLoadAllChannelParentTag(List<ChannelParentTag> channelParentTagList){};

    default void onLoadEmptyCommonChannelParentTag(){};
}
