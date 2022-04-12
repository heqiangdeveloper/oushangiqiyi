package com.oushang.lib_service.callback;

import com.oushang.lib_service.entries.FavoriteRecord;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 获取收藏记录回调
 * @Time: 2021/9/3 16:59
 * @Since: 1.0
 */
public interface ISubscriptionListCallback {

    void onSuccess(List<FavoriteRecord> favoriteRecordList);

    void onFailure(int errCode, String msg);

}
