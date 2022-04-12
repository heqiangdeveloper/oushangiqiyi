package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfoRecord;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 下载播放记录回调
 * @Time: 2021/9/3 14:31
 * @Since: 1.0
 */
public interface IDownloadCallback {

    void onSuccess(List<HistoryRecord> historyRecordList);

    void onFailure(int errCode, String errMsg);

}
