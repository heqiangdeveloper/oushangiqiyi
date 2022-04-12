package com.oushang.iqiyi.entries;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 热门推荐title
 * @Time: 2021/7/9 14:32
 * @Since: 1.0
 */
public class HotRecommendTitle implements ISpanItem {
    private String title;
    private boolean hasMore = false;

    public HotRecommendTitle(String title) {
        this.title = title;
    }

    public HotRecommendTitle() {
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
        return 5;
    }

    @Override
    public int getViewType() {
        return MutiType.TEXT;
    }
}
