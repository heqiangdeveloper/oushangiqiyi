package com.oushang.iqiyi.entries;

import com.oushang.lib_service.entries.ChannelTag;

/**
 * @Author: zeelang
 * @Description: ***
 * @Time: 2021/10/21 0021  17:25
 * @Since: 1.0
 */
public class SelectChannelChildTag {

    private boolean isSelected = false;

    private ChannelTag channelTag;

    public SelectChannelChildTag(ChannelTag channelTag, boolean isSelected) {
        this.isSelected = isSelected;
        this.channelTag = channelTag;
    }

    public SelectChannelChildTag(ChannelTag channelTag) {
        this.channelTag = channelTag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ChannelTag getChannelTag() {
        return channelTag;
    }

    public void setChannelTag(ChannelTag channelTag) {
        this.channelTag = channelTag;
    }
}
