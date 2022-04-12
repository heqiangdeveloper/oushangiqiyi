package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 频道子标签接口
 * @Time: 2021/7/20 9:50
 * @Since: 1.0
 */
public interface IChannelChildTagView extends IBaseView {

    //回调加载频道子标签
    void onLoadChannelChildTag(List<ChannelTag> tags);
}
