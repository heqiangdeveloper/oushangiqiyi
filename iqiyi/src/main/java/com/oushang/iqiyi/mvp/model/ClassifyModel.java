package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.entries.ChannelTag;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.interfaces.ChannelManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;


/**
 * @Author: zeelang
 * @Description: 分类数据
 * @Time: 2021/7/12 10:30
 * @Since: 1.0
 */
public class ClassifyModel extends BaseServiceModel {

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_CHANNEL_MANAGER)
    ChannelManager mChannelManager;

    public List<ChannelTag> getNormalChannelTagList() {
        List<ChannelTag> channelTagList = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);
        if (!channelTagList.isEmpty()) {
            for (ChannelTag channelTag: channelTagList) {
                if (!channelTag.isCommonChannel()) {
                    channelTag.setCommonChannel(true);
                }
                if (channelTag.isEditEnable()) {
                    channelTag.setEditEnable(false);
                }
            }
        }
        return channelTagList;
    }

    public List<ChannelParentTag> getCommonChannelParentTagList() {
        List<ChannelParentTag> channelTagList = SPUtils.getShareValue(Constant.SP_SPACE_COMMON_CHANNEL_PARENT_TAG, Constant.SP_KEY_COMMON_CHANNEL_PARENT_TAG, ChannelParentTag.class);
        if (!channelTagList.isEmpty()) {
            for (ChannelParentTag channelTag: channelTagList) {
                if (!channelTag.isCommonChannel()) {
                    channelTag.setCommonChannel(true);
                }
                if (channelTag.isEditEnable()) {
                    channelTag.setEditEnable(false);
                }
            }
        }
        return channelTagList;
    }

    public Observable<List<com.oushang.lib_service.entries.ChannelTag>> getAllChannelTags() {
        return mChannelManager.getAllChannelTags();
    }
}
