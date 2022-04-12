package com.oushang.iqiyi.mvp.presenter;

import android.os.Bundle;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.ChannelChildTagModel;
import com.oushang.iqiyi.mvp.view.IChannelChildTagView;
import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 频道子标签处理逻辑
 * @Time: 2021/7/19 20:29
 * @Since: 1.0
 */
public class ChannelChildTagPresenter extends BaseServicePresenter<IChannelChildTagView, ChannelChildTagModel> {

    public static final String BUNDLE_ARGS = "channeltag";

    @Override
    protected ChannelChildTagModel createModel() {
        return new ChannelChildTagModel();
    }

    public void loadChannelChildTag(List<ChannelTag> channelTagList) {
        if(isAttach()) {
            getView().onLoadChannelChildTag(channelTagList);
        }
    }
}
