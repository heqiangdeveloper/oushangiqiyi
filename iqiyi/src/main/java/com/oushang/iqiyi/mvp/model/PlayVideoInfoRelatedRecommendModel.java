package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.VideoManager;
import com.oushang.lib_service.response.BaseResponse;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 相关推荐数据模型
 * @Time: 2021/7/27 15:16
 * @Since: 1.0
 */
public class PlayVideoInfoRelatedRecommendModel extends BaseServiceModel {

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager;

    public Observable<List<VideoInfo>> loadRelatedRecommendData(long qipuId, int num, String ispurchase, String ondemand) {
       return mVideoManager.getRelatedRecommendList(qipuId, num, ispurchase, ondemand);
    }




}
