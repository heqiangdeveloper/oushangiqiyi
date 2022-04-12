package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.entries.SearchResultInfo;
import com.oushang.iqiyi.mvp.model.SearchResultModel;
import com.oushang.iqiyi.mvp.view.ISearchResultView;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 搜索结果视图逻辑
 * @Time: 2021/8/6 17:22
 * @Since: 1.0
 */
public class SearchResultPresenter extends BaseServicePresenter<ISearchResultView, SearchResultModel> {
    private static final String TAG = SearchResultPresenter.class.getSimpleName();

    @Override
    protected SearchResultModel createModel() {
        return new SearchResultModel();
    }


    /**
     * 搜索结果
     * @param key 关键字
     * @param pageNum 页码
     * @param pageSize 数量
     * @param mode 排序方式
     */
    public void loadSearchResult(String key, int pageNum, int pageSize, int mode) {
        Log.d(TAG, "loadSearchResult:key:" + key + ",pageNum:" + pageNum + ",pageSize:" + pageSize + ",mode:" + mode);
        if (isAttach()) {
            execute(model.querySearchResult(key, pageNum, pageSize, mode),
                    searchInfo -> {
                        if(searchInfo == null || searchInfo.getVideos().isEmpty()) {
                            getView().onLoadEmptySearchResult();
                        } else {
                            int total = searchInfo.getTotal();
                            List<VideoInfo> videoInfoList = searchInfo.getVideos();
                            int count = videoInfoList.size();
                            List<IMultiItem> searchResultList = new ArrayList<>();
                            for(VideoInfo videoInfo: videoInfoList) {
                                SearchResultInfo searchResultInfo = new SearchResultInfo(videoInfo);
                                searchResultList.add(searchResultInfo);
                            }
                            Log.d(TAG, "total:" + total + ",count:" + count);
                            getView().onLoadSearchResultInfo(searchResultList, count <= total && total > pageSize);
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
     * 搜索结果
     * @param key 关键字
     * @param pageNum 页码
     * @param pageSize 数量
     * @param mode 排序方式
     */
    public void loadMoreSearchResult(String key, int pageNum, int pageSize, int mode) {
        Log.d(TAG, "loadMoreSearchResult:key:" + key + ",pageNum:" + pageNum + ",pageSize:" + pageSize + ",mode:" + mode);
        if (isAttach()) {
            execute(model.querySearchResult(key, pageNum, pageSize, mode),
                    searchInfo -> {
                        List<IMultiItem> searchResultList = new ArrayList<>();
                        if(searchInfo != null && !searchInfo.getVideos().isEmpty()) {
                            List<VideoInfo> videoInfoList = searchInfo.getVideos();
                            int size = videoInfoList.size();
                            for(int i = 0; i < size && i < 20; i++) {
                                SearchResultInfo searchResultInfo = new SearchResultInfo(videoInfoList.get(i));
                                searchResultList.add(searchResultInfo);
                            }
                        }
                        getView().onLoadMoreSearchResultInfo(searchResultList);
                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }
}
