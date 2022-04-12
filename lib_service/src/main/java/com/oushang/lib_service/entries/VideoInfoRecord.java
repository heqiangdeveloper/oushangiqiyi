package com.oushang.lib_service.entries;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @Author: zeelang
 * @Description: 视频播放记录
 * @Time: 2021/7/28 18:17
 * @Since: 1.0
 */
@Entity(tableName = "videoInfoRecord")
public class VideoInfoRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id = 1;

    //数据结构类型 1:记录数据 2:收藏数据
    @ColumnInfo(name = "albumType")
    private int albumType;

    //收藏类型
    @ColumnInfo(name = "subType")
    private int subType;

    //收藏数据 key
    @ColumnInfo(name = "subKey")
    private String subKey;

    //视频已播放时长
    @ColumnInfo(name = "playTime")
    private String playTime;

    //播放记录添加的时间
    @ColumnInfo(name = "addTime")
    private long addTime;

    //当前更新集数
    @ColumnInfo(name = "current")
    private int current;

    //视频信息
    @ColumnInfo(name = "videoInfo")
    private VideoInfo videoInfo;

    private long videoPlayTime;

    public VideoInfoRecord() {
    }

    @Ignore
    public VideoInfoRecord(int albumType, int subType, String subKey, String playTime, long addTime, int current, VideoInfo videoInfo) {
        this.albumType = albumType;
        this.subType = subType;
        this.subKey = subKey;
        this.playTime = playTime;
        this.addTime = addTime;
        this.current = current;
        this.videoInfo = videoInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumType() {
        return albumType;
    }

    public void setAlbumType(int albumType) {
        this.albumType = albumType;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey(String subKey) {
        this.subKey = subKey;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public long getVideoPlayTime() {
        return videoPlayTime;
    }

    public void setVideoPlayTime(long videoPlayTime) {
        this.videoPlayTime = videoPlayTime;
    }

    @Override
    public String toString() {
        return "VideoInfoRecord{" +
                "id=" + id +
                ", albumType=" + albumType +
                ", subType=" + subType +
                ", subKey='" + subKey + '\'' +
                ", playTime='" + playTime + '\'' +
                ", addTime=" + addTime +
                ", current=" + current +
                ", videoInfo=" + videoInfo +
                ", videoPlayTime=" + videoPlayTime +
                '}';
    }
}
