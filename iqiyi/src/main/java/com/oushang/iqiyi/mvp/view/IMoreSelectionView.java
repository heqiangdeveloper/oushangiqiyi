package com.oushang.iqiyi.mvp.view;

import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.lib_base.base.mvp.view.IBaseView;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集接口
 * @Time: 2021/10/18 0018  11:41
 * @Since: 1.0
 */
public interface IMoreSelectionView extends IBaseView {
    
    void onLoadSelection(List<SelectionEntry> selectionEntries);
}
