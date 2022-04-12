package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.MyHistoryModel;
import com.oushang.iqiyi.mvp.view.IMyFavoriteView;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.entries.FavoriteRecord;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 收藏记录业务逻辑
 * @Time: 2021/10/29 0029  15:31
 * @Since: 1.0
 */
public class MyFavoritePresenter extends BaseServicePresenter<IMyFavoriteView, MyHistoryModel> {
    private static final String TAG = MyFavoritePresenter.class.getSimpleName();

    @Override
    protected MyHistoryModel createModel() {
        return new MyHistoryModel();
    }

    /**
     * 加载收藏记录
     */
    public void loadFavoriteRecord() {
        Log.d(TAG, "loadFavoriteRecord");
        if (isAttach()) {
            showLoading();
            executeTimeOut(0, 20, 0, 1000, aLong -> {
                Log.d(TAG, "long:" + aLong);
                return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized()); //是否被始化完成
            }, isInit -> isInit, isInit -> {
                Log.d(TAG, "sdk is init success:" + isInit);
                if (isInit) { //如果已初始化完成
                    if(model.isLogin()) {
                        Log.d(TAG, "account is login");
                        model.loadFavoriteRecord(new ISubscriptionListCallback() {
                            @Override
                            public void onSuccess(List<FavoriteRecord> favoriteRecordList) {
                                Log.d(TAG, "onSuccess:" + favoriteRecordList);
                                if (favoriteRecordList == null || favoriteRecordList.isEmpty()) {
                                    hideLoading();
                                    if (null != getView()) getView().onLoadEmptyRecord();
                                } else {
                                    hideLoading();
                                    if (null != getView())
                                        getView().onLoadFavoriteRecord(favoriteRecordList);
                                }
                            }

                            @Override
                            public void onFailure(int errCode, String msg) {
                                Log.e(TAG, "onFailure errCode:" + errCode + ",msg:" + msg);
                                hideLoading();
                                if (null != getView()) getView().onLoadFailRecord();
                            }
                        });
                    } else {
                        Log.d(TAG, "account is logout");
                        hideLoading();
                        loadEmptyRecord();
                    }
                }
            });
        }
    }

    /**
     * 未登录，显示空
     */
    public void loadEmptyRecord() {
        Log.d(TAG, "loadEmptyRecord");
        if (isAttach()) {
            getView().onLoadEmptyRecord();
        }
    }

    /**
     * 删除收藏记录
     *
     * @param favoriteRecords 收藏记录
     */
    public void deleteFavoriteRecord(FavoriteRecord... favoriteRecords) {
        Log.d(TAG, "deleteFavoriteRecord");
        if (isAttach()) {
            model.deleteFavoriteRecord(new ISubscriptionCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "deleteFavoriteRecord onSuccess");
                    showToast("删除收藏记录成功!");
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.e(TAG, "deleteFavoriteRecord onFailure:" + msg);
                    showToast("删除收藏记录失败!");
                }
            }, favoriteRecords);
        }
    }

    /**
     * 清空收藏记录
     */
    public void clearFavoriteRecord() {
        if (isAttach()) {
            model.clearFavoriteRecord(new ISubscriptionCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "clear favorite record success");
                    showToast("清空收藏记录成功!");
                    getView().onLoadEmptyRecord();
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.e(TAG, "clear favorite record failure");
                    showToast("清空收藏记录失败!");
                }
            });
        }
    }

    public boolean isLogin() {
        return model.isLogin();
    }
}
