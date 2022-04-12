package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.DisclaimersModel;
import com.oushang.iqiyi.mvp.view.IDisclaimersView;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.entries.HistoryRecord;

import java.util.List;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/20 0020  19:45
 * @Since: 1.0
 */
public class DisclaimersPresenter extends BaseServicePresenter<IDisclaimersView, DisclaimersModel> {
    private static final String TAG = DisclaimersPresenter.class.getSimpleName();

    @Override
    protected DisclaimersModel createModel() {
        return new DisclaimersModel();
    }

    public void loadRecent() {
        Log.d(TAG, "loadRecent");
        if(isAttach()) {
            model.loadRecent(new IDownloadCallback() {
                @Override
                public void onSuccess(List<HistoryRecord> historyRecordList) {
                    if(historyRecordList != null && !historyRecordList.isEmpty()) {
                        HistoryRecord record = historyRecordList.get(0);
                        Log.d(TAG, "onLoadRecent:" + record);
                        getView().onLoadRecent(record);
                    } else {
                        Log.e(TAG, "onLoadEmptyRecent");
                        getView().onLoadEmptyRecent();
                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    Log.e(TAG, "errCode:" + errCode + ",errMsg:" + errMsg);
                    getView().onLoadEmptyRecent();
                }
            });
        }
    }

    public void loadLocalRecent() {
        Log.d(TAG, "loadLocalRecent");
        if(isAttach()) {
            List<HistoryRecord> historyRecordList = model.loadLocalRecent();
            if(historyRecordList != null && !historyRecordList.isEmpty()) {
                Log.d(TAG, "onLoadRecent");
                getView().onLoadRecent(historyRecordList.get(0));
            } else {
                Log.e(TAG, "onLoadEmptyRecent");
                getView().onLoadEmptyRecent();
            }
        }
    }
}
