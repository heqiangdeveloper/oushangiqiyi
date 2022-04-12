package com.oushang.lib_service.entries;

import java.util.List;

/**
 * @Author: Administrator
 * @Description: 推荐信息
 * @Time: 2021/10/9 0009  13:57
 * @Since: 1.0
 */
public class RecommendInfo {
    private String title;
    private String chnId;
    private String chnName;
    private List<VideoInfo> videoList;

    public RecommendInfo(String title, String chnId, String chnName, List<VideoInfo> videoList) {
        this.title = title;
        this.chnId = chnId;
        this.chnName = chnName;
        this.videoList = videoList;
    }

    public RecommendInfo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChnId() {
        return chnId;
    }

    public void setChnId(String chnId) {
        this.chnId = chnId;
    }

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public List<VideoInfo> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoInfo> videoList) {
        this.videoList = videoList;
    }

    @Override
    public String toString() {
        return "RecommendInfo{" +
                "title='" + title + '\'' +
                ", chnId='" + chnId + '\'' +
                ", chnName='" + chnName + '\'' +
                ", videoList=" + videoList +
                '}';
    }
}
