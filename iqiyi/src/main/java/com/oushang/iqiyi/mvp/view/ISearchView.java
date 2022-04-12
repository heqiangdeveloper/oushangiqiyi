package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.base.mvp.view.IView;

/**
 * @Author: zeelang
 * @Description: 搜索界面视图接口
 * @Time: 2021/8/6 11:57
 * @Since: 1.0
 */
public interface ISearchView extends IBaseView {

    void onLoadSearchHistory();

    void onLoadSearchResult(String searchKey, int searchType, int resultMode);
}
