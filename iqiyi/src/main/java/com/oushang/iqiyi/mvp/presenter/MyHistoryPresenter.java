package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.MeFragment;
import com.oushang.iqiyi.mvp.model.MyHistoryModel;
import com.oushang.iqiyi.mvp.view.IMyRecordView;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.IDeleteRecordListCallback;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.entries.VideoInfoRecord;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 我的历史-逻辑处理
 * @Time: 2021/7/28 18:28
 * @Since: 1.0
 */
public class MyHistoryPresenter extends BaseServicePresenter<IMyRecordView, MyHistoryModel> {
    private static final String TAG = MyHistoryPresenter.class.getSimpleName();

    @Override
    protected MyHistoryModel createModel() {
        return new MyHistoryModel();
    }

    private List<VideoInfoRecord> convertHistoryVideoInfoRecord(List<HistoryRecord> historyRecordList) {
        List<VideoInfoRecord> videoInfoRecordList = new ArrayList<>();
        if (historyRecordList != null && !historyRecordList.isEmpty()) {
            int size = historyRecordList.size();
            for (int i = 0; i < size; i++) {
                HistoryRecord historyRecord = historyRecordList.get(i);
                VideoInfoRecord record = new VideoInfoRecord();
                String videoId = historyRecord.getVideoId();
                int id = 0;
                if (videoId != null && !videoId.isEmpty()) {
                    id = Integer.parseInt(historyRecord.getVideoId());
                }
                record.setId(id);
                record.setAddTime(historyRecord.addtime);
                record.setAlbumType(historyRecord.type);
                record.setSubType(1);
                record.setSubKey("history");
                String time = AppUtils.parseSecond(historyRecord.getVideoPlayTime());
                record.setVideoPlayTime(historyRecord.getVideoPlayTime());
                record.setPlayTime(time);
                String order = historyRecord.videoOrder;
                if (order != null && !order.isEmpty()) {
                    record.setCurrent(Integer.parseInt(order));
                }

                VideoInfo videoInfo = new VideoInfo();
                String qipuId = historyRecord.getTvId();
                String albumId = historyRecord.getAlbumId();
                String name = historyRecord.getVideoName();
                String albumPic = historyRecord.getVideoImageUrl();
                long len = historyRecord.getVideoDuration();
                videoInfo.setQipuId(Long.parseLong(qipuId));
                videoInfo.setAlbumId(Long.parseLong(albumId));
                videoInfo.setName(name);
                videoInfo.setAlbumPic(albumPic);
                videoInfo.setLen(len);
                record.setVideoInfo(videoInfo);
                videoInfoRecordList.add(record);
            }
        }
        return videoInfoRecordList;
    }

    private List<VideoInfoRecord> convertStoreVideoInfoRecord(List<FavoriteRecord> favoriteRecordList) {
        List<VideoInfoRecord> videoInfoRecordList = new ArrayList<>();
        if (favoriteRecordList != null && !favoriteRecordList.isEmpty()) {
            for (FavoriteRecord record : favoriteRecordList) {
                VideoInfoRecord videoInfoRecord = new VideoInfoRecord();
                videoInfoRecord.setAlbumType(record.getAlbumType());
                videoInfoRecord.setPlayTime(record.getPlaytime());
                videoInfoRecord.setAddTime(record.getAddTime());

                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setLen(record.getLen());
                videoInfo.setQipuId(record.getQipuId());
                videoInfo.setAlbumPic(record.getAlbumPic());
                videoInfo.setAlbumId(record.getAlbumId());
                videoInfo.setName(record.getName());
                videoInfo.setAlbumName(record.getAlbumName());

                videoInfoRecord.setVideoInfo(videoInfo);

                videoInfoRecordList.add(videoInfoRecord);
            }
        }

        return videoInfoRecordList;
    }

    /**
     * 加载本地播放历史记录
     */
    public void loadLocalHistoryRecord() {
        Log.d(TAG, "loadLocalHistoryRecord");
        if(isAttach()) {
            showLoading();
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                if(isInit) {
                    List<HistoryRecord> historyRecordList = model.loadHistoryRecord();
                    Log.d(TAG, "historyRecordList:" + historyRecordList);
                    if (isAttach()) {
                        if (historyRecordList == null || historyRecordList.isEmpty()) {
                            hideLoading();
                            getView().onLoadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                        } else {
                            hideLoading();
                            getView().onLoadHistoryRecord(historyRecordList);
                        }
                    }
                }
            });
        }
    }

    /**
     * 登录状态下，加载远程历史记录
     *
     * @param pageNum 页
     */
    public void loadRemoteHistoryRecord(int pageNum) {
        Log.d(TAG, "loadRemoteHistoryRecord");
        if (isAttach()) {
            showLoading();
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                Log.d(TAG, "sdk is init success:" + isInit);
                if (isInit) { //如果已初始化完成
                    model.loadHistoryRecordByPage(pageNum, new IDownloadCallback() {
                        @Override
                        public void onSuccess(List<HistoryRecord> historyRecordList) {
                            if (historyRecordList == null || historyRecordList.isEmpty()) {
                                hideLoading();
                                if(null != getView()) getView().onLoadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                            } else {
                                hideLoading();
                                if(null != getView()) getView().onLoadHistoryRecord(historyRecordList);
                            }
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            Log.d(TAG, "loadHistoryRecord onFailure:{errCode:" + errCode + ",errMsg:" + errMsg + "}");
                            hideLoading();
                            if(null != getView()) getView().onLoadFail(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                        }
                    }, true);
                }
            });
        }
    }

    /**
     * 加载收藏记录
     */
    public void loadStoreRecord() {
        Log.d(TAG, "loadStoreRecord");
        if (isAttach()) {
            executeTimeOut(0, 60, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                Log.d(TAG, "sdk is init success:" + isInit);
                if (isInit) { //如果已初始化完成
                    model.loadFavoriteRecord(new ISubscriptionListCallback() {
                        @Override
                        public void onSuccess(List<FavoriteRecord> favoriteRecordList) {
                            Log.d(TAG, "loadStoreRecord onSuccess:");
                            if (favoriteRecordList == null || favoriteRecordList.isEmpty()) {
                                Disposable disposable = Observable.just(Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(integer -> {
                                            Log.d(TAG, "onLoadEmptyRecord:" + integer);
                                            getView().onLoadEmptyRecord(integer);
                                        });
                                return;
                            }
                            Disposable disposable = Observable.just(convertStoreVideoInfoRecord(favoriteRecordList))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(videoInfoRecords -> getView().onLoadVideoInfoRecord(videoInfoRecords, Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE));
                        }

                        @Override
                        public void onFailure(int errCode, String msg) {
                            Log.e(TAG, "errCode:" + errCode + ",msg:" + msg);
                            Disposable disposable = Observable.just(Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(integer -> {
                                        getView().onLoadEmptyRecord(integer);
                                    });
                        }
                    });
                }
            });


        }
    }

    /**
     * 加载空的记录
     *
     * @param recordType 记录类型
     */
    public void loadEmptyRecord(int recordType) {
        if (getView() != null) {
            getView().onLoadEmptyRecord(recordType);
        }
    }

    public void deleteHistoryRecord(List<HistoryRecord> historyRecordList) {
        Log.d(TAG, "deleteHistoryRecord:" + historyRecordList);
        model.deleteHistoryRecord(historyRecordList, false, new IDeleteRecordListCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "删除成功");
                showToast("删除成功");
            }

            @Override
            public void onFailure(int errCode, String msg) {
                Log.d(TAG, "删除失败:errCode:" + errCode + ",msg:" + msg);
                showToast("删除失败");
            }
        });
    }

    /**
     * 清空历史记录
     */
    public void clearHistoryRecord() {
        Log.d(TAG, "clearHistoryRecord");
        if (isAttach()) {
            model.clearHistoryRecord(new IDeleteRecordListCallback() {
                @Override
                public void onSuccess() {
                    getView().onLoadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                    showToast("清除成功");
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.e(TAG, "clear failure: errCode:" + errCode + ",msg:" + msg);
                    showToast("清除失败");
                }
            });
        }

    }

    /**
     * 是否登录
     * @return true 是， false 否
     */
    public boolean isLogin() {
        return model.isLogin();
    }

}
