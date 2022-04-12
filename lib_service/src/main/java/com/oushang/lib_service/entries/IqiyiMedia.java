package com.oushang.lib_service.entries;


import com.iqy.iv.player.IMedia;

import java.util.Map;

/**
 * @Author: zeelang
 * @Description: 视频播放信息(sdk需要构建的播放信息)
 * @Date: 2021/6/24
 */
public class IqiyiMedia implements IMedia {

    //专辑Id 点播必须保证传入正确的值
    private String albumId;

    //视频Id 点播必须保证传入正确的值
    private String tvId;

    //播放开始位置 播放历史起播需传入此值，非常不建议起播后 seek
    private int startPosition;

    //直播频道id 直播必须保证传入正确的值
    private String liveChannelId;

    //直播节目id 临时直播必须保证传入正确的值
    private String liveProgramId;

    //视频时长
    private long playLength;

    //直播类型
    private int liveType;

    //视频DRM属性
    private int drmType;

    //是否Vip视频 是否 VIP 视频，直播/点播必须保证传入正确的值
    private boolean isVip;

    //提供是否是直播，直播 /轮播返回 true，点播 false
    private boolean isLive;

    private int mediaType;

    private Map<String, Object> extra;

    private boolean isOffline;

    public IqiyiMedia(){}

    public IqiyiMedia(String albumId, String tvId, int startPosition,
                      String liveChannelId, String liveProgramId, long playLength,
                      int liveType, int drmType, boolean isVip, boolean isLive,
                      int mediaType, Map<String, Object> extra, boolean isOffline) {
        this.albumId = albumId;
        this.tvId = tvId;
        this.startPosition = startPosition;
        this.liveChannelId = liveChannelId;
        this.liveProgramId = liveProgramId;
        this.playLength = playLength;
        this.liveType = liveType;
        this.drmType = drmType;
        this.isVip = isVip;
        this.isLive = isLive;
        this.mediaType = mediaType;
        this.extra = extra;
        this.isOffline = isOffline;
    }

    public static IqiyiMedia create(String albumId,String tvId, int startPosition, boolean isVip) {
        IqiyiMedia iqiyiMedia = new IqiyiMedia();
        iqiyiMedia.setAlbumId(albumId);
        iqiyiMedia.setTvId(tvId);
        iqiyiMedia.setStartPosition(startPosition);
        iqiyiMedia.setIsVip(isVip);
        return iqiyiMedia;
    }

    @Override
    public String getAlbumId() {
        return albumId;
    }

    @Override
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public String getVid() {
        return null;
    }


    @Override
    public String getTvId() {
        return tvId;
    }

    @Override
    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    @Override
    public int getStartPosition() {
        return startPosition;
    }

    @Override
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public String getLiveChannelId() {
        return liveChannelId;
    }

    @Override
    public void setLiveChannelId(String liveChannelId) {
        this.liveChannelId = liveChannelId;
    }

    @Override
    public String getLiveProgramId() {
        return liveProgramId;
    }

    @Override
    public void setLiveProgramId(String liveProgramId) {
        this.liveProgramId = liveProgramId;
    }

    @Override
    public long getPlayLength() {
        return playLength;
    }

    @Override
    public void setPlayLength(long playLength) {
        this.playLength = playLength;
    }

    @Override
    public int getLiveType() {
        return liveType;
    }

    @Override
    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    @Override
    public int getDrmType() {
        return drmType;
    }

    @Override
    public void setDrmType(int drmType) {
        this.drmType = drmType;
    }

    @Override
    public boolean isVip() {
        return isVip;
    }

    @Override
    public void setIsVip(boolean vip) {
        this.isVip = vip;
    }

    @Override
    public boolean isLive() {
        return isLive;
    }

    @Override
    public void setIsLive(boolean live) {
        this.isLive = live;
    }

    @Override
    public int getMediaType() {
        return mediaType;
    }

    @Override
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public Map<String, Object> getExtra() {
        return extra;
    }

    @Override
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public boolean isOffline() {
        return isOffline;
    }
}
