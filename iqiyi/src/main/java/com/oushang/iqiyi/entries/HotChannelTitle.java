package com.oushang.iqiyi.entries;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 热门频道标签
 * @Time: 2021/7/8 14:44
 * @Since: 1.0
 */
public class HotChannelTitle implements ISpanItem {
    private String title;
    private boolean hasMore = true;

    public HotChannelTitle(String title) {
        this.title = title;
    }

    public HotChannelTitle() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void setMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public int getSpanSize() {
        return 3;
    }

    @Override
    public int getViewType() {
        return MutiType.TEXT;
    }
}
