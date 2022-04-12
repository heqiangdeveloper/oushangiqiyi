package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.mvp.model.SearchModel;
import com.oushang.iqiyi.mvp.view.ISearchView;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;

/**
 * @Author: zeelang
 * @Description: 搜索界面视图逻辑
 * @Time: 2021/8/6 12:00
 * @Since: 1.0
 */
public class SearchPresenter extends BaseServicePresenter<ISearchView, SearchModel> {
    private static final String TAG = SearchPresenter.class.getSimpleName();

    @Override
    protected SearchModel createModel() {
        return new SearchModel();
    }

    public void loadSearchHistory() {
        if (getView() != null) {
            getView().onLoadSearchHistory();
        }
    }

    public void loadSearchResult(String searchKey, int searchType, int resultMode) {
        if (getView() != null) {
            getView().onLoadSearchResult(searchKey, searchType, resultMode);
        }
    }
}
