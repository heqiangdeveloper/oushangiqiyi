package com.oushang.lib_service.entries;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 频道详情
 * @Time: 2021/6/28 20:14
 * @Since: 1.0
 */
public class ChannelInfo {

    //结果数
    private int total;

    //标签列表，注：如果requireTags参数为0，后台不返回tags字段
    private List<ChannelTag> tags;

    //节目列表
    private List<VideoInfo> videos;

    public ChannelInfo() {
    }

    public ChannelInfo(int total, List<ChannelTag> tags, List<VideoInfo> videos) {
        this.total = total;
        this.tags = tags;
        this.videos = videos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<VideoInfo> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoInfo> videos) {
        this.videos = videos;
    }

    public List<ChannelTag> getTags() {
        return tags;
    }

    public void setTags(List<ChannelTag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "total=" + total +
                ", channelTagList=" + tags +
                ", videoInfoList=" + videos +
                '}';
    }
}
