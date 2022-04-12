package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 搜索历史视图接口
 * @Time: 2021/8/6 14:07
 * @Since: 1.0
 */
public interface ISearchHistoryView extends IBaseView {

    void onLoadSearchHistory(List<String> searchKeyWord);

    void onLoadHotSearch(List<VideoInfo> videoInfoList);

    void onLoadEmptySearchHistory();

}
