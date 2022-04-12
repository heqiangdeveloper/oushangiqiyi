package com.oushang.iqiyi.entries;

import android.widget.ImageView;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 热门频道图片
 * @Time: 2021/7/8 14:49
 * @Since: 1.0
 */
public class HotChannelImage implements ISpanItem {

    private ImageView image;

    private String videoName;

    private String videoFocus;

    public HotChannelImage(ImageView image) {
        this.image = image;
    }

    public HotChannelImage() {
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoFocus() {
        return videoFocus;
    }

    public void setVideoFocus(String videoFocus) {
        this.videoFocus = videoFocus;
    }

    @Override
    public int getSpanSize() {
        return 1;
    }

    @Override
    public int getViewType() {
        return MutiType.IMAGE;
    }
}
