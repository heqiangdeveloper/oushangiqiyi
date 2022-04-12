package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.interfaces.ChannelManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 分类详情数据模型
 * @Time: 2021/7/19 15:03
 * @Since: 1.0
 */
public class ClassifyDetailsModel extends BaseServiceModel {

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_CHANNEL_MANAGER)
    ChannelManager mChannelManager;


    public Observable<ChannelInfo> getChannelInfos(String channelName, int pageNum, int pageSize) {
        return mChannelManager.getChannelInfos(channelName, null,null, pageNum, pageSize, -1, -1, true);
    }

    public Observable<ChannelInfo> getChannelInfos(String channelName, int pageNum, int pageSize, String treeCateGoryId) {
        return mChannelManager.getChannelInfos(channelName, null,treeCateGoryId, pageNum, pageSize, -1, -1, true);
    }


}
