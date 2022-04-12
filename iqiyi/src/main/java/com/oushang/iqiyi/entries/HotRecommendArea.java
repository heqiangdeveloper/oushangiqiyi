package com.oushang.iqiyi.entries;

import android.graphics.drawable.Drawable;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 热门推荐功能区（金刚区）
 * @Time: 2021/7/9 14:38
 * @Since: 1.0
 */
public class HotRecommendArea implements ISpanItem {
    private String title;

    private int icon;

    public HotRecommendArea(String title) {
        this.title = title;
    }

    public HotRecommendArea(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public HotRecommendArea() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    @Override
    public int getSpanSize() {
        return 1;
    }

    @Override
    public int getViewType() {
        return MutiType.TEXT_IMAGE;
    }
}
