package com.oushang.iqiyi.ui;

import com.oushang.lib_base.base.rv.IMultiItem;

/**
 * @Author: DELL
 * @Description: 占行或列RecyclerView item
 * @Time: 2021/7/8 14:08
 * @Since: 1.0
 */
public interface ISpanItem extends IMultiItem {

    //占多少行或多少列
    int getSpanSize();
}
