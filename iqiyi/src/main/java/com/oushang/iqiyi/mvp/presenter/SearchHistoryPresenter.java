package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.mvp.model.SearchModel;
import com.oushang.iqiyi.mvp.view.ISearchHistoryView;
import com.oushang.lib_base.utils.SPUtils;

import java.util.List;

/**
 * @Author: DELL
 * @Description: 搜索历史视图逻辑
 * @Time: 2021/8/6 15:10
 * @Since:
 */
public class SearchHistoryPresenter extends BaseServicePresenter<ISearchHistoryView, SearchModel> {
    private static final String TAG = SearchHistoryPresenter.class.getSimpleName();

    @Override
    protected SearchModel createModel() {
        return new SearchModel();
    }

    public void loadSearchHistory() {
        if (getView() != null) {
            List<String> searchKeyWordList =  SPUtils.getShareValue(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT,
                    Constant.SP_KEY_SEARCH_HISTORY_KEYWORD, String.class);
            if (!searchKeyWordList.isEmpty()) {
                getView().onLoadSearchHistory(searchKeyWordList);
                loadHotSearch(8);
            } else {
                getView().onLoadEmptySearchHistory();
            }
        }
    }

    public void loadHotSearch(int num) {
        if (isAttach()) {
            execute(model.getRankList(-1, null, 0, num),
                    videoInfoList -> {
                        getView().onLoadHotSearch(videoInfoList);
                    },
                    throwable -> {
                        Log.e(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);

        }
    }

    public void loadEmptySearchHistory() {
        if (getView() != null) {
            getView().onLoadEmptySearchHistory();
        }
    }
}
