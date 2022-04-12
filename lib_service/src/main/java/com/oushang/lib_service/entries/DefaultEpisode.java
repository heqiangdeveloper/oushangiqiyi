package com.oushang.lib_service.entries;

/**
 * @Author: DELL
 * @Description: 默认视频节点信息
 * @Time: 2021/6/30 9:27
 * @Since:
 */
public class DefaultEpisode {

    //默认视频ID
    private long qipuId;

    //默认视频名称
    private String name;

    //默认视频是否3D
    private int is3D;

    //默认视频的内容类型
    private int contentType;

    //默认视频的发布日期，格式：20160711
    private String publishTime;

    //默认视频的播放时长
    private long len;


    public DefaultEpisode(long qipuId, String name, int is3D, int contentType, String publishTime, long len) {
        this.qipuId = qipuId;
        this.name = name;
        this.is3D = is3D;
        this.contentType = contentType;
        this.publishTime = publishTime;
        this.len = len;
    }

    public long getQipuId() {
        return qipuId;
    }

    public void setQipuId(long qipuId) {
        this.qipuId = qipuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs3D() {
        return is3D;
    }

    public void setIs3D(int is3D) {
        this.is3D = is3D;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "DefaultEpisode{" +
                "qipuId=" + qipuId +
                ", name='" + name + '\'' +
                ", is3D=" + is3D +
                ", contentType=" + contentType +
                ", publishTime='" + publishTime + '\'' +
                ", len=" + len +
                '}';
    }
}
