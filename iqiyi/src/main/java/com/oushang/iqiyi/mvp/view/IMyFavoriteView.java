package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.FavoriteRecord;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 收藏记录view接口
 * @Time: 2021/10/29 0029  15:32
 * @Since: 1.0
 */
public interface IMyFavoriteView extends IBaseView {

    void onLoadFavoriteRecord(List<FavoriteRecord> favoriteRecordList);

    void onLoadEmptyRecord();

    void onLoadFailRecord();

}
