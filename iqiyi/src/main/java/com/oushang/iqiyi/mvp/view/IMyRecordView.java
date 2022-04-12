package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfoRecord;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 我的历史-view接口
 * @Time: 2021/7/28 18:29
 * @Since: 1.0
 */
public interface IMyRecordView extends IBaseView {

    void onLoadVideoInfoRecord(List<VideoInfoRecord> videoInfoRecordList, int recordType);

    void onLoadEmptyRecord(int recordType);

    void onLoadFail(int recordType);

    void onLoadHistoryRecord(List<HistoryRecord> historyRecordList);

    void onUpdateData();

}
