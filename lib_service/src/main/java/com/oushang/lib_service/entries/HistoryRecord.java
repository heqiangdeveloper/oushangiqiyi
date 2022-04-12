package com.oushang.lib_service.entries;

import java.util.Objects;

/**
 * @Author: zeelang
 * @Description: 历史记录
 * @Time: 2021/10/14 0014  20:09
 * @Since: 1.0
 */
public class HistoryRecord {
    public String tvId = ""; //视频qipuId
    public String videoOrder = ""; //正片或者预告片的集数 当前播放到第几集
    public String videoName = ""; //专辑/剧集名称
    public long videoPlayTime; //播放时长
    public long videoDuration; //视频总时长
    public String albumId = ""; //视频所属专辑ID，01后08结尾
    public long addtime; //添加时间
    public String videoId = "";
    public String sourceName = "";
    public int terminalId;
    public int channelId; //频道id
    public String userId = "";
    public int isSeries;
    public String videoImageUrl = ""; //封面图
    public int _pc = -1; //是否付费：0 免费；1 付费&未划价；2 付费&已划价，可用
    public int t_pc = -1; //0 免费；1 会员免费；2 会员点播付费； 3 点播卷
    public int _pc_next = -1; //下一集收费信息， 0 免费； 1 付费未划价；2 付费，可用
    public int com = 1;
    public int keyType;
    public String ctype = "";
    private boolean toDelete = false;
    public int videoType = -1;
    public String sourceId = "";
    public int type = 1;
    public int playcontrol;
    public String albumName = ""; //所属专辑的名称
    private boolean isBlockShown;
    public int mpd; //更新到多少集/期
    public int isEnd; //是否更新结束， 1 未结束，2 结束

    private boolean isSelected; // 是否选择

    public HistoryRecord() {
    }

    public String getID() {
        if (this.type == 1) {
            switch(this.keyType) {
                case 0:
                    return String.valueOf(this.albumId);
                case 1:
                    return String.valueOf(this.tvId);
                case 2:
                    return String.valueOf(this.sourceId);
                default:
                    return String.valueOf(this.albumId);
            }
        } else {
            return String.valueOf(this.tvId);
        }
    }

    public int compareTo(HistoryRecord another) {
        return another != null && this != null ? (int)(another.addtime - this.addtime) : 0;
    }

    public String getTvId() {
        return tvId;
    }

    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    public String getVideoOrder() {
        return videoOrder;
    }

    public void setVideoOrder(String videoOrder) {
        this.videoOrder = videoOrder;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public long getVideoPlayTime() {
        return videoPlayTime;
    }

    public void setVideoPlayTime(long videoPlayTime) {
        this.videoPlayTime = videoPlayTime;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(int isSeries) {
        this.isSeries = isSeries;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public int get_pc() {
        return _pc;
    }

    public void set_pc(int _pc) {
        this._pc = _pc;
    }

    public int getT_pc() {
        return t_pc;
    }

    public void setT_pc(int t_pc) {
        this.t_pc = t_pc;
    }

    public int get_pc_next() {
        return _pc_next;
    }

    public void set_pc_next(int _pc_next) {
        this._pc_next = _pc_next;
    }

    public int getCom() {
        return com;
    }

    public void setCom(int com) {
        this.com = com;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlaycontrol() {
        return playcontrol;
    }

    public void setPlaycontrol(int playcontrol) {
        this.playcontrol = playcontrol;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public boolean isBlockShown() {
        return isBlockShown;
    }

    public void setBlockShown(boolean blockShown) {
        isBlockShown = blockShown;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryRecord record = (HistoryRecord) o;
        return Objects.equals(tvId, record.tvId) && Objects.equals(albumId, record.albumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tvId, albumId);
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "tvId='" + tvId + '\'' +
                ", videoOrder='" + videoOrder + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoPlayTime=" + videoPlayTime +
                ", videoDuration=" + videoDuration +
                ", albumId='" + albumId + '\'' +
                ", addtime=" + addtime +
                ", videoId='" + videoId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", terminalId=" + terminalId +
                ", channelId=" + channelId +
                ", userId='" + userId + '\'' +
                ", isSeries=" + isSeries +
                ", videoImageUrl='" + videoImageUrl + '\'' +
                ", _pc=" + _pc +
                ", t_pc=" + t_pc +
                ", _pc_next=" + _pc_next +
                ", com=" + com +
                ", keyType=" + keyType +
                ", ctype='" + ctype + '\'' +
                ", toDelete=" + toDelete +
                ", videoType=" + videoType +
                ", sourceId='" + sourceId + '\'' +
                ", type=" + type +
                ", playcontrol=" + playcontrol +
                ", albumName='" + albumName + '\'' +
                ", isBlockShown=" + isBlockShown +
                '}';
    }
}
