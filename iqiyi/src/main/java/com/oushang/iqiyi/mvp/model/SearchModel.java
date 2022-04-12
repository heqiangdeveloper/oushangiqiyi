package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.SearchManager;
import com.oushang.lib_service.interfaces.VideoManager;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 搜索业务逻辑
 * @Time: 2021/8/6 12:02
 * @Since: 1.0
 */
public class SearchModel extends BaseServiceModel {

    @Autowired(name = Constant.PATH_SERVICE_SEARCH_MANAGER)
    SearchManager mSearchManager;

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager;

    public Observable<List<VideoInfo>> getRankList(int cid, String cids, int vip, int num) {
        return mVideoManager.getRankListInfo(cid, cids, vip, num);
    }

}
