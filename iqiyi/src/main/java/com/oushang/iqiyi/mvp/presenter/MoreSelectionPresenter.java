package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.google.common.collect.Lists;
import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.mvp.model.PlayerModel;
import com.oushang.iqiyi.mvp.view.IMoreSelectionView;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/10/18 0018  11:44
 * @Since: 1.0
 */
public class MoreSelectionPresenter extends BaseServicePresenter<IMoreSelectionView, PlayerModel> {
    private static final String TAG = MoreSelectionPresenter.class.getSimpleName();

    @Override
    protected PlayerModel createModel() {
        return new PlayerModel();
    }

    /**
     * 加载选集（网络）
     * @param qipuId 视频id
     * @param year 年份
     * @param pos  位置
     * @param num  数量
     */
    public void loadMoreSelection(long qipuId, int year, int pos, int num) {
        Log.d(TAG, "loadMoreSelection");
        if (isAttach()) {
            execute(model.queryEpisodeInfoList(qipuId, year, pos, num),
                    episodeInfoList -> {
                        Log.d(TAG, "queryEpisodeInfoList");
                        long sourceCode = episodeInfoList.getSourceCode();
                        if (sourceCode == 0) {
                            List<VideoInfo> epg = episodeInfoList.getEpg();
                            List<SelectionEntry> selectionEntryList = new ArrayList<>();
                            for (VideoInfo videoInfo : epg) {
                                SelectionEntry entry = new SelectionEntry(videoInfo, false);
                                selectionEntryList.add(entry);
                            }
                            getView().onLoadSelection(selectionEntryList);
                        } else {
                        }
                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }

    }

    /**
     * 加载本地选集
     * @param pos 开始位置
     * @param num 数据个数
     */
    public void loadLocalMoreSelection(int pos, int num) {
        Log.d(TAG, "loadLocalMoreSelection: pos:" + pos + ",num:" + num);
        if (isAttach()) {
            EpisodeInfo episodeInfo = model.getEpisodeInfo();
            if (episodeInfo != null && episodeInfo.getSourceCode() == 0) { //如果剧集是数字型
                List<VideoInfo> videoInfoList = model.getVideoInfoList();
                if (videoInfoList != null && !videoInfoList.isEmpty() && pos >=0 && num > 0) {
                    int size = videoInfoList.size();
                    if(pos < size && (pos + num) <= size) {
                        List<SelectionEntry> selectionEntryList = new ArrayList<>();
                        for(int i = 0; i < num; i++) {
                            VideoInfo videoInfo = videoInfoList.get(pos + i);
                            SelectionEntry entry = new SelectionEntry(videoInfo, false);
                            selectionEntryList.add(entry);
                        }
                        getView().onLoadSelection(selectionEntryList);
                    }
                } else {
                    Log.e(TAG, "videoinfo list is null or empty!");
                }
            }
        }

    }

}
