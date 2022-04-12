package com.oushang.iqiyi.mvp.model;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.base.BaseServiceModel;
import com.oushang.lib_base.base.mvp.model.BaseModel;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.SearchInfo;
import com.oushang.lib_service.interfaces.SearchManager;
import com.oushang.lib_service.response.BaseResponse;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 搜索结果业务逻辑
 * @Time: 2021/8/6 17:22
 * @Since: 1.0
 */
public class SearchResultModel extends BaseServiceModel {

    @Autowired(name = Constant.PATH_SERVICE_SEARCH_MANAGER)
    SearchManager mSearchManager;

    public Observable<SearchInfo> querySearchResult(String key, int pageNum, int pageSize, int mode) {
        return mSearchManager.getSearchInfo(key, -1, -1, pageNum, pageSize, "", mode);
    }

}
