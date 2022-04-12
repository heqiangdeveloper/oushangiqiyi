package com.oushang.iqiyi.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.PlayerModel;
import com.oushang.iqiyi.mvp.view.IPlayerView;
import com.oushang.iqiyi.player.PlayerControlEvent;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.entries.EpisodeInfo;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 播放处理逻辑
 * @Time: 2021/7/20 17:28
 * @Since: 1.0
 */
public class PlayerPresenter extends BaseServicePresenter<IPlayerView, PlayerModel> {
    private static final String TAG = PlayerPresenter.class.getSimpleName();

    private static final int MAX_NUM = 60; //每次最大条目

    @Override
    protected PlayerModel createModel() {
        return new PlayerModel();
    }

    /**
     * 加载视频信息
     *
     * @param qipuId 视频id
     */
    public void postLoadVideoInfo(long qipuId) {
        Log.d(TAG, "postLoadVideoInfo");
        execute(model.getVideoInfo(qipuId),
                videoInfo -> {
                    if (isAttach()) {
                        getView().onPlayerVideoInfo(videoInfo);
                    }
                },
                throwable -> {
                    Log.e(TAG, "error:"+ Log.getStackTraceString(throwable));
                    return true;
                },
                aBoolean -> false);
    }

    /**
     * 加载视频信息，如果是专辑/剧集
     * @param albumId 剧集id
     * @param year 年份
     * @param pos 页码
     * @param num 数量
     */
    public void postLoadEpisodeInfo(long albumId, int year, int pos, int num) {
        if(isAttach()) {
            execute(model.queryEpisodeInfoList(albumId, year, pos, num), episodeInfoList -> {
                        List<VideoInfo> epg = episodeInfoList.getEpg();
                        if(epg != null && !epg.isEmpty() && isAttach()) {
                            getView().onPlayerVideoInfo(epg.get(0));
                        }

                    },throwable -> {
                        Log.e(TAG, "error:"+ Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }


    /**
     * 获取所有剧集列表信息
     * @param albumId 专辑id
     * @param year 年份
     * @param pos 位置
     */
    public void getAllEpisodeInfoList(long albumId, int year, int pos) {
        Log.d(TAG, "getAllEpisodeInfoList:{albumId:" + albumId + ",year:" + year + ",pos:" +  pos + "}");
        execute(model.queryEpisodeInfoList(albumId, year, pos, MAX_NUM),
                episodeInfoList -> {
                    boolean hasMore = episodeInfoList.isHasMore();
                    int nPos = episodeInfoList.getPos();
                    Log.d(TAG, "hasMore:" + hasMore + ",nPos:" + nPos);
                    List<VideoInfo> videoInfoList = episodeInfoList.getEpg();
                    Log.d(TAG, "add videoInfoList:" + videoInfoList);
                    if (model.getEpisodeInfo() == null) {
                        long total = episodeInfoList.getTotal();
                        String publishYear = episodeInfoList.getPublishYear();
                        int mixedCount = episodeInfoList.getMixedCount();
                        int chnId = episodeInfoList.getChnId();
                        String chnName = episodeInfoList.getChnName();
                        long sourceCode = episodeInfoList.getSourceCode();
                        long aId = episodeInfoList.getAlbumId();
                        String albumName = episodeInfoList.getAlbumName();
                        EpisodeInfo episodeInfo = new EpisodeInfo(total, mixedCount, nPos, publishYear, hasMore, chnId, chnName, sourceCode, aId, albumName);
                        model.setEpisodeInfo(episodeInfo);
                        model.setPartitionEnable(true); //启用分集
//                        if(sourceCode == 0) { //如果是数字型
//
//                        }
                    } else {
                        EpisodeInfo episodeInfo =  model.getEpisodeInfo();
                        episodeInfo.setPos(nPos);
                        episodeInfo.setHasMore(hasMore);
                    }
                    if(videoInfoList != null && !videoInfoList.isEmpty()) {
                        model.addVideoInfoList(videoInfoList);
                    }
                    if (hasMore) {
                        getAllEpisodeInfoList(albumId, year, nPos);
                    } else {
                        Log.d(TAG, "notifyLoadComplete");
                        model.notifyLoadComplete();//通知加载完成
                    }
                },
                throwable -> {
                    Log.e(TAG, "getAllEpisodeInfoList error:"+ Log.getStackTraceString(throwable));
                    return true;
                },
                aBoolean -> false);
    }


    /**
     * 点击播放
     */
    public void clickPlay() {
        if (isAttach()) {
            getView().onPlayerControlEventListener(PlayerControlEvent.PLAYER_EVENT_PLAY);
        }
    }


    /**
     *  点击切换下一集
     */
    public void clickNext() {
        if (isAttach()) {
            getView().onPlayerControlEventListener(PlayerControlEvent.PLAYER_EVENT_NEXT);
        }
    }

    /**
     *  点击收藏
     * @param videoInfo 视频信息
     * @param playTime 播放时间
     */
    public void addFavorite(VideoInfo videoInfo, int playTime) {
        if (isAttach()) {
            model.addFavoriteRecord(new ISubscriptionCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "addFavorite success");
                    getView().onPlayerControlEventListener(PlayerControlEvent.PLAYER_EVENT_SUBSCRIPT);
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.d(TAG, "addFavorite fail:" + errCode + ",msg:" + msg);

                    showToast("收藏失败");
                }
            }, new FavoriteRecord(videoInfo, String.valueOf(playTime)));
        }
    }

    public void removeFavorite(VideoInfo videoInfo) {
        if (isAttach()) {
            model.removeFavoirteRecord(new ISubscriptionCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "removeFavorite success");
                    getView().onSubscription(false);
                    showToast("取消收藏成功");
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.d(TAG, "removeFavorite failure:errCode:" + errCode + ",msg:" + msg);
                    showToast("取消收藏失败");
                }
            }, new FavoriteRecord(videoInfo, ""));
        }
    }

    public void postContentEvent(int event, Bundle args) {
        if (isAttach()) {
            getView().onPlayerContentEventListener(event, args);
        }
    }

    public void checkIsFavorite(final long albumId, final long qipuId) {
        Log.d(TAG, "checkIsFavorite:" + qipuId);
        if (isAttach()) {
            model.getSubscriptionList(new ISubscriptionListCallback() {
                @Override
                public void onSuccess(List<FavoriteRecord> favoriteRecordList) {
                    Log.d(TAG, "checkIsFavorite: onSuccess");
                    for(FavoriteRecord record: favoriteRecordList) {
                        long alId = record.getAlbumId();
                        long tvId = record.getQipuId();
                        if(VideoInfo.isAlbumId(alId) && albumId == alId) {
                            Log.d(TAG, "checkIsFavorite:true " + albumId);
                            if(null != getView()) getView().onSubscription(true);
                            return;
                        }

                        if(qipuId == tvId) {
                            Log.d(TAG, "checkIsFavorite:true " + tvId);
                            if(null != getView()) getView().onSubscription(true);
                            return;
                        }
                    }
                    if(null != getView()) getView().onSubscription(false);
                }

                @Override
                public void onFailure(int errCode, String msg) {
                    Log.e(TAG, "checkIsFavorite: onFailure: errCode:" + errCode + ",msg:" + msg);
                    if(null != getView()) getView().onSubscription(false);
                }
            });
        }

    }

    @Deprecated
    public void loadHistoryRecordRemote() {
        if(isAttach()) {
            model.loadHistoryRecordByPage(1, new IDownloadCallback() {
                @Override
                public void onSuccess(List<HistoryRecord> historyRecordList) {
                    if(historyRecordList != null && historyRecordList.isEmpty()) {

                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {

                }
            }, true);
        }
    }

    @Deprecated
    public void loadHistoryRecordLocal() {
        
    }

}
