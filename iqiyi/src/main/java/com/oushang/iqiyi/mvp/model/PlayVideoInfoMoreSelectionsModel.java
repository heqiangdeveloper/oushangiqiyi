package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.interfaces.VideoManager;

/**
 * @Author: zeelang
 * @Description: 更多选集数据模型
 * @Time: 2021/8/23 15:52
 * @Since: 1.0
 */
public class PlayVideoInfoMoreSelectionsModel extends BaseModel {

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager;

    public void loadMoreSelections() {
//        mVideoManager.getEpisodeInfoList()
    }
}
