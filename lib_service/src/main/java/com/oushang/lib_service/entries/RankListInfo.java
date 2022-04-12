package com.oushang.lib_service.entries;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 风云榜列表信息
 * @Time: 2021/6/30 15:54
 * @Since:
 */
public class RankListInfo {

    //所属频道ID
    private int chnId;

    //频道名称
    private String chnName;

    //视频信息
    private VideoInfo videoInfo;

    //多频道视频节目信息
    private List<VideoInfo> videoInfoList;

    public RankListInfo(int chnId, String chnName, List<VideoInfo> videoInfoList) {
        this.chnId = chnId;
        this.chnName = chnName;
        this.videoInfoList = videoInfoList;
    }

    public RankListInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public int getChnId() {
        return chnId;
    }

    public void setChnId(int chnId) {
        this.chnId = chnId;
    }

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public List<VideoInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoInfo> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }
}
