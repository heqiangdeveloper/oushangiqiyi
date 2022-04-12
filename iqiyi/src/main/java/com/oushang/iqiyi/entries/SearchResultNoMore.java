package com.oushang.iqiyi.entries;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 没有更多搜索信息
 * @Time: 2021/12/16 0016  16:50
 * @Since: 1.0
 */
public class SearchResultNoMore implements ISpanItem {
    private String text;

    public SearchResultNoMore(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getSpanSize() {
        return 4;
    }

    @Override
    public int getViewType() {
        return MutiType.TEXT;
    }
}
