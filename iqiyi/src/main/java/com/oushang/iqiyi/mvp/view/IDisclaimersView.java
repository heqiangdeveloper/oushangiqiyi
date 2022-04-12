package com.oushang.iqiyi.mvp.view;

import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_service.entries.HistoryRecord;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/20 0020  20:00
 * @Since: 1.0
 */
public interface IDisclaimersView extends IBaseView {

    default void onLoadRecent(HistoryRecord record){}

    default void onLoadEmptyRecent(){};
}
