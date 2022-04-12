package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.List;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/20 0020  19:46
 * @Since: 1.0
 */
public class DisclaimersModel extends BaseServiceModel {

    @Autowired(name = Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager;

    public void loadRecent(IDownloadCallback callback) {
        mPlayManager.downloadRecordByPage(0, callback, true);
    }

    public List<HistoryRecord> loadLocalRecent() {
        return mPlayManager.getLocalRecord();
    }

}
