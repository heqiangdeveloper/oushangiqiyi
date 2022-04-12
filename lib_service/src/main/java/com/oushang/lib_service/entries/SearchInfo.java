package com.oushang.lib_service.entries;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 节目搜索信息
 * @Time: 2021/7/1 15:20
 * @Since: 1.0
 */
public class SearchInfo {

    int total;

    List<VideoInfo> videos;

    String eventId;

    String bkt;

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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getBkt() {
        return bkt;
    }

    public void setBkt(String bkt) {
        this.bkt = bkt;
    }

    @Override
    public String toString() {
        return "SearchInfo{" +
                "total=" + total +
                ", videos=" + videos +
                ", eventId='" + eventId + '\'' +
                ", bkt='" + bkt + '\'' +
                '}';
    }
}
