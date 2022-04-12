package com.oushang.iqiyi.mvp.view;

import com.oushang.iqiyi.entries.SearchResultInfo;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 搜索结果视图接口
 * @Time: 2021/8/6 14:20
 * @Since: 1.0
 */
public interface ISearchResultView extends IBaseView {

    /**
     * 回调加载搜索结果信息
     * @param searchResultInfoList 搜索结果信息
     * @param hasMore 还有更多
     */
    void onLoadSearchResultInfo(List<IMultiItem> searchResultInfoList, boolean hasMore);

    /**
     * 回调加载更多搜索结果信息
     * @param searchResultInfoList 搜索结果信息
     */
    void onLoadMoreSearchResultInfo(List<IMultiItem> searchResultInfoList);

    /**
     * 搜索为空
     */
    void onLoadEmptySearchResult();

}
