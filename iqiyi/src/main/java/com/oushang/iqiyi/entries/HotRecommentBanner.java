package com.oushang.iqiyi.entries;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: DELL
 * @Description: 热门推荐banner
 * @Time: 2021/7/9 14:36
 * @Since: 1.0
 */
public class HotRecommentBanner implements ISpanItem {

    private String imgUrl;

    private String focus;

    public HotRecommentBanner(String url) {
        this.imgUrl = url;
    }

    public HotRecommentBanner() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    @Override
    public int getSpanSize() {
        return 5;
    }

    @Override
    public int getViewType() {
        return MutiType.BANNER;
    }
}
