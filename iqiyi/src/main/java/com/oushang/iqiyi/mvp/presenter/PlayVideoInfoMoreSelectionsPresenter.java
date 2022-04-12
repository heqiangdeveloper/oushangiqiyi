package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.PlayVideoInfoMoreSelectionsModel;
import com.oushang.iqiyi.mvp.model.PlayerModel;
import com.oushang.iqiyi.mvp.view.IPlayVideoInfoMoreSelectionsView;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集逻辑
 * @Time: 2021/8/23 15:36
 * @Since: 1.0
 */
public class PlayVideoInfoMoreSelectionsPresenter extends BaseServicePresenter<IPlayVideoInfoMoreSelectionsView, PlayerModel> {
    private static final String TAG = PlayVideoInfoMoreSelectionsPresenter.class.getSimpleName();

    @Override
    protected PlayerModel createModel() {
        return new PlayerModel();
    }

    //加载更多选集
    public void loadMoreSelection(long qipuId, int year,int pos, int num) {
        Log.d(TAG, "loadMoreSelection");
        if (isAttach()) {
            execute(model.queryEpisodeInfoList(qipuId, year, pos, num),
                    episodeInfoList -> {
                        Log.d(TAG, "queryEpisodeInfoList");
                        long sourceCode = episodeInfoList.getSourceCode();
                        if (sourceCode == 0) {
                            Log.d(TAG, "onLoadMoreSelectionByDigital");
                            getView().onLoadMoreSelectionByDigital(episodeInfoList);
                        } else {
                            Log.d(TAG, "onLoadMoreSelectionByTime");
                            getView().onLoadMoreSelectionByTime(episodeInfoList);
                        }
                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }

    //加载本地更多选集
    public void loadLocalMoreSelection() {
        Log.d(TAG, "loadLocalMoreSelection");
        if (isAttach()) {
            EpisodeInfo episodeInfo = model.getEpisodeInfo();
            List<VideoInfo> videoInfoList = model.getVideoInfoList();
            if (episodeInfo != null && videoInfoList != null && !videoInfoList.isEmpty()) {
                EpisodeInfoList episodeInfoList = new EpisodeInfoList();
                episodeInfoList.setEpisodeInfo(episodeInfo);
                episodeInfoList.setEpg(videoInfoList);
                long sourceCode = episodeInfoList.getSourceCode();
                if (sourceCode == 0) {
                    Log.d(TAG, "onLoadMoreSelectionByDigital");
                    getView().onLoadMoreSelectionByDigital(episodeInfoList);
                } else {
                    Log.d(TAG, "onLoadMoreSelectionByTime");
                    getView().onLoadMoreSelectionByTime(episodeInfoList);
                }
            }
        }
    }

    public void loadUpdateMoreSelection(EpisodeInfoList episodeInfoList) {
        if (episodeInfoList != null) {
            long sourceCode = episodeInfoList.getSourceCode();
            if (sourceCode == 0) {
                Log.d(TAG, "onLoadMoreSelectionByDigital");
                getView().onLoadMoreSelectionByDigital(episodeInfoList);
            } else {
                Log.d(TAG, "onLoadMoreSelectionByTime");
                getView().onLoadMoreSelectionByTime(episodeInfoList);
            }
        }
    }

    public boolean isFullScreen() {
        return model.isFullScreen();
    }
}
