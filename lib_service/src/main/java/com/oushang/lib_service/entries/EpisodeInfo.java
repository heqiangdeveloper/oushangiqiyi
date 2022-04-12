package com.oushang.lib_service.entries;

/**
 * @Author: zeelang
 * @Description: 剧集信息
 * @Time: 2021/10/26 16:12
 * @Since: 1.0
 */
public class EpisodeInfo {

    // 如果是非来源类型专辑，值为专辑下视频的总集数（电视剧的总集数）；如果是
    //来源类型专辑，值为此专辑下能够分页获取的鉴权前数据总量
    private long total;

    //剧集总集数，包括已上线正片集数+预告片集数
    private int mixedCount;

    //下一页回传给接口的pos值
    private int pos;

    //来源类节目发布年份yyyy，逗号分隔
    private String publishYear;

    //是否还有下一页：true 有，其它没有。客户端根据此字段确认是否请求下一页
    private boolean hasMore;

    //所属频道ID
    private int chnId;

    //所属频道的名称
    private String chnName;

    //来源code，如果有值且大于0，标明是来源专辑。其它情况标明是非来源专辑
    private long sourceCode;

    //视频所属专辑ID，01结尾
    private long albumId;

    //所属专辑的名称
    private String albumName;

    public EpisodeInfo() {
    }

    public EpisodeInfo(long total, int mixedCount, int pos, String publishYear, boolean hasMore,
                       int chnId, String chnName, long sourceCode, long albumId, String albumName) {
        this.total = total;
        this.mixedCount = mixedCount;
        this.pos = pos;
        this.publishYear = publishYear;
        this.hasMore = hasMore;
        this.chnId = chnId;
        this.chnName = chnName;
        this.sourceCode = sourceCode;
        this.albumId = albumId;
        this.albumName = albumName;
    }

    public EpisodeInfo(int chnId, String chnName, long sourceCode, long albumId, String albumName) {
        this.chnId = chnId;
        this.chnName = chnName;
        this.sourceCode = sourceCode;
        this.albumId = albumId;
        this.albumName = albumName;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getMixedCount() {
        return mixedCount;
    }

    public void setMixedCount(int mixedCount) {
        this.mixedCount = mixedCount;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
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

    public long getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(long sourceCode) {
        this.sourceCode = sourceCode;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public String toString() {
        return "EpisodeInfo{" +
                "total=" + total +
                ", mixedCount=" + mixedCount +
                ", pos=" + pos +
                ", publishYear='" + publishYear + '\'' +
                ", hasMore=" + hasMore +
                ", chnId=" + chnId +
                ", chnName='" + chnName + '\'' +
                ", sourceCode=" + sourceCode +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
