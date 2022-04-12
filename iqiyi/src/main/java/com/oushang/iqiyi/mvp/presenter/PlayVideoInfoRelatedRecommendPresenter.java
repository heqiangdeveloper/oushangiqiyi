package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.PlayVideoInfoRelatedRecommendModel;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoRelatedRecommendView;


/**
 * @Author: zeelang
 * @Description: 播放视频信息-相关推荐处理逻辑
 * @Time: 2021/7/21 20:31
 * @Since: 1.0
 */
public class PlayVideoInfoRelatedRecommendPresenter extends BaseServicePresenter<IPlayVideoInfoRelatedRecommendView, PlayVideoInfoRelatedRecommendModel> {
    private static final String TAG = PlayVideoInfoRelatedRecommendPresenter.class.getSimpleName();

    @Override
    protected PlayVideoInfoRelatedRecommendModel createModel() {
        return new PlayVideoInfoRelatedRecommendModel();
    }

    public void LoadRecommendVideoInfo(long qipuId, int num) {
        Log.d(TAG, "LoadRecommendVideoInfo:" + qipuId + ",num:" + num);
        if(isAttach()) {
            execute(model.loadRelatedRecommendData(qipuId,num, "",""),
                    videoInfos -> {
                        Log.d(TAG, "loadRelatedRecommendData");
                        getView().onRelatedRecommendVideoInfo(videoInfos);
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);

        }
    }
}
