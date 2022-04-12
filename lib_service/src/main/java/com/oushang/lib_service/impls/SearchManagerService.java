package com.oushang.lib_service.impls;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.SearchInfo;
import com.oushang.lib_service.interfaces.SearchManager;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 搜索管理服务
 * @Time: 2021/7/1 15:46
 * @Since: 1.0
 */
@Route(path = Constant.PATH_SERVICE_SEARCH_MANAGER)
public class SearchManagerService implements SearchManager {

    @Override
    public void init(Context context) {

    }

    /**
     *
     * @param key         节目搜索关键字
     * @param ispurchase  付费方式，若无此参数，则返回所有付费和非付费
     * @param ondemand    是否付费点播,若无此参数，则默认返回付费点播和非付费点播结果
     * @param pageNum     查询页码，1代表第一页
     * @param pageSize    每页数据量，最大60条
     * @param channelName 频道名称，支持多频道搜索，多频道请以英文逗号分隔
     * @param mode        排序方式 1:相关性； 4:最新；11:最热 （非纪录片频道）; 10:最热 （纪录片频道）
     * @return
     */
    @Override
    public Observable<SearchInfo> getSearchInfo(String key, int ispurchase, int ondemand, int pageNum, int pageSize, String channelName, int mode) {
        return IqiyiApi.search(key, ispurchase, ondemand,pageNum,pageSize,channelName,mode).map(s -> new Gson().fromJson(s, SearchInfo.class));
    }
}
