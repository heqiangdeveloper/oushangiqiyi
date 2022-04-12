package com.oushang.iqiyi.mvp.model;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.iqiyi.entries.HotRecommendArea;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.RankListInfo;
import com.oushang.lib_service.entries.RecommendInfo;
import com.oushang.lib_service.interfaces.ChannelManager;
import com.oushang.lib_service.interfaces.VideoManager;
import com.oushang.lib_service.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author: zeelang
 * @Description: 首页数据
 * @Time: 2021/7/12 10:16
 * @Since: 1.0
 */
public class HomePageModel extends BaseServiceModel {

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager;

    @Autowired(name = Constant.PATH_SERVICE_CHANNEL_MANAGER)
    ChannelManager mChannelManager;

    public Observable<List<RecommendInfo>> loadRecommendInfo() {
        return mVideoManager.getRecommendInfo(null, null, -1, -1);
    }

    public Observable<ChannelInfo> loadChannelInfo(String channelName, int pageNum, int pageSize) {
        return mChannelManager.getChannelInfos(channelName, null, null, pageNum, pageSize, -1, -1, true);
    }
}
