package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_service.callback.IDeleteRecordListCallback;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.interfaces.MyAccountManager;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 我的历史-数据模型
 * @Time: 2021/7/28 18:30
 * @Since: 1.0
 */
public class MyHistoryModel extends BaseServiceModel {

    @Autowired
    PlayManager mPlayManager;

    @Autowired
    MyAccountManager mMyAccountManager;

    public List<HistoryRecord> loadHistoryRecord() {
        return mPlayManager.getLocalRecord();
    }

    public void loadHistoryRecordByPage(int pageNum, IDownloadCallback callback, boolean isMerge) {
        mPlayManager.downloadRecordByPage(pageNum, callback, isMerge);
    }

    public void loadFavoriteRecord(ISubscriptionListCallback callback) {
        mPlayManager.getSubscriptionList(callback);
    }

    public void deleteHistoryRecord(List<HistoryRecord> records, boolean needClear, IDeleteRecordListCallback callback) {
        mPlayManager.deleteRecord(records, needClear, callback);
    }

    public void deleteFavoriteRecord(ISubscriptionCallback callback, FavoriteRecord... favoriteRecords) {
        mPlayManager.unSubscript(callback, favoriteRecords);
    }

    public void clearHistoryRecord(IDeleteRecordListCallback callback) {
        mPlayManager.clearHistoryRecord(callback);
    }

    public void clearFavoriteRecord(ISubscriptionCallback callback) {
        mPlayManager.clearFavoriteRecord(callback);
    }

    public boolean isLogin() {
        return mMyAccountManager.isLogin();
    }

}
