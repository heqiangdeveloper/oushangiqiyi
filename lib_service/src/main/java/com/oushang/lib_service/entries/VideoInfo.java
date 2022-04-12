package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @Author: zeelang
 * @Description: 视频节目信息（包括专辑、剧集）
 * @Time: 2021/7/1 11:56
 * @Since: 1.0
 */
public class VideoInfo implements Parcelable {

    //所属频道Id
    private int chnId;

    //所属频道名称
    private String chnName;

    //专辑奇谱ID，01或08结尾 / 视频奇谱ID, 00或07结尾（剧集）
    private long qipuId;

    //专辑/剧集名称
    private String name;

    //二级标题
    private String subTitle;

    //一句话看点
    private String focus;

    //封面图
    private String albumPic;

    //海报图
    private String posterPic;

    //用户评分
    private String score;

    //是否多集，1是多集剧，其它表示不是多集剧
    private int isSeries;

    //短标题
    private String shortName;

    //是否独播,1 是独播，其它表示不是独播
    private int isExclusive;

    //来源code sourceCode大于0是来源专辑，非来源专辑sourceCode 为0
    private long sourceCode;

    //视频的发布日期，格式：20160711
    private String publishTime;

    //vip信息
    private VipInfo vipInfo;

    //0-普通会员，1-网球会员，多个用逗号分隔
    private String vipType;

    //奇艺首次上线时间（格式：2016-07-11 21:56:07）
    private String initIssueTime;

    //播放次数
    private int pCount;

    //专辑描述
    private String desc;

    //标签列表（Type_音乐,Place_内地，客户端只要汉字名可按照最后一个_分隔）
    private String tag;

    //全体演员
    private CastInfo cast;

    //是否是爱奇艺自制 0 不是 1是
    private int qiyiProd;

    /*###############专辑属性###############*/

    //默认视频节点
    private DefaultEpisode defaultEpi;

    //总集数
    private long total;

    //（专辑/来源）总视频数或（视频）当前集数
    private int count;

    //跟播剧更新策略（"每周日、一 24:00 两集连播"）
    private String stragegy;

    //是否完结: 0 未完成；1 已完成；-1 此字段为空（默认值）
    private int isFinished;

    /*###############剧集属性###############*/

    //视频所属专辑ID，01后08结尾
    private long albumId;

    //所属专辑的名称
    private String albumName;

    //封面图
    private String pic;

    //drm属性,多个用逗号分隔；1:DRM_NONE 2:DRM_INTERTRUST 3:DRM_CHINA
    private String drm;

    //HDR字段
    private String hdr;

    //是否3D
    private int is3D;

    //正片或者预告片的集数 当前播放到第几集
    private int order;

    //视频时长
    private long len;

    //默认视频的内容类型（正片、片花、预告片等）,参看ContentType
    private int contentType;

    //4k类型
    private String type4k;

    //杜比类型
    private String dolby;

    public VideoInfo() {}

    protected VideoInfo(Parcel in) {
        chnId = in.readInt();
        chnName = in.readString();
        qipuId = in.readLong();
        name = in.readString();
        subTitle = in.readString();
        focus = in.readString();
        albumPic = in.readString();
        posterPic = in.readString();
        score = in.readString();
        isSeries = in.readInt();
        shortName = in.readString();
        isExclusive = in.readInt();
        sourceCode = in.readLong();
        publishTime = in.readString();
        vipInfo = in.readParcelable(VipInfo.class.getClassLoader());
        vipType = in.readString();
        initIssueTime = in.readString();
        pCount = in.readInt();
        desc = in.readString();
        tag = in.readString();
        cast = in.readParcelable(CastInfo.class.getClassLoader());
        qiyiProd = in.readInt();
        total = in.readLong();
        count = in.readInt();
        stragegy = in.readString();
        isFinished = in.readInt();
        albumId = in.readLong();
        albumName = in.readString();
        pic = in.readString();
        drm = in.readString();
        hdr = in.readString();
        is3D = in.readInt();
        order = in.readInt();
        len = in.readLong();
        contentType = in.readInt();
        type4k = in.readString();
        dolby = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chnId);
        dest.writeString(chnName);
        dest.writeLong(qipuId);
        dest.writeString(name);
        dest.writeString(subTitle);
        dest.writeString(focus);
        dest.writeString(albumPic);
        dest.writeString(posterPic);
        dest.writeString(score);
        dest.writeInt(isSeries);
        dest.writeString(shortName);
        dest.writeInt(isExclusive);
        dest.writeLong(sourceCode);
        dest.writeString(publishTime);
        dest.writeParcelable(vipInfo, flags);
        dest.writeString(vipType);
        dest.writeString(initIssueTime);
        dest.writeInt(pCount);
        dest.writeString(desc);
        dest.writeString(tag);
        dest.writeParcelable(cast, flags);
        dest.writeInt(qiyiProd);
        dest.writeLong(total);
        dest.writeInt(count);
        dest.writeString(stragegy);
        dest.writeInt(isFinished);
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeString(pic);
        dest.writeString(drm);
        dest.writeString(hdr);
        dest.writeInt(is3D);
        dest.writeInt(order);
        dest.writeLong(len);
        dest.writeInt(contentType);
        dest.writeString(type4k);
        dest.writeString(dolby);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    /**
     *  是否是专辑的id
     * @param qipuId 专辑ID或视频ID
     * @return boolean
     */
    public static boolean isAlbumId(long qipuId) {
        if (qipuId > 0) {
            String qipuIdstr = String.valueOf(qipuId);
            return qipuIdstr.endsWith("01") || qipuIdstr.endsWith("08");
        }
        return false;
    }

     /**
     * 是否是视频的id
     * @param qipuId 专辑ID或视频ID
     * @return boolean
     */
    public static boolean isVideoId(long qipuId) {
        if (qipuId > 0) {
            String qipuIdstr = String.valueOf(qipuId);
            return qipuIdstr.endsWith("00") || qipuIdstr.endsWith("07");
        }
        return false;
    }

    //是否是专辑
    public boolean isAlbum() {
        return isAlbumId(qipuId);
    }

    //是否是 视频/剧集
    public boolean isVideo() {
        return isVideoId(qipuId);
    }

    public boolean isVip() {
        if (vipInfo != null) {
            int vip = vipInfo.getIsVip();
            return vip == VipInfo.VIP;
        }
        return false;
    }

    //是否独播
    public boolean isExclusive() {
        return 1 == isExclusive;
    }

    //是否多集
    public boolean isSeries() {
        return 1 == isSeries;
    }

    //获取频道名Id
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getPosterPic() {
        return posterPic;
    }

    public void setPosterPic(String posterPic) {
        this.posterPic = posterPic;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(int isSeries) {
        this.isSeries = isSeries;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(int isExclusive) {
        this.isExclusive = isExclusive;
    }

    public long getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(long sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public VipInfo getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(VipInfo vipInfo) {
        this.vipInfo = vipInfo;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public String getInitIssueTime() {
        return initIssueTime;
    }

    public void setInitIssueTime(String initIssueTime) {
        this.initIssueTime = initIssueTime;
    }

    public int getpCount() {
        return pCount;
    }

    public void setpCount(int pCount) {
        this.pCount = pCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CastInfo getCast() {
        return cast;
    }

    public void setCast(CastInfo cast) {
        this.cast = cast;
    }

    public int getQiyiProd() {
        return qiyiProd;
    }

    public void setQiyiProd(int qiyiProd) {
        this.qiyiProd = qiyiProd;
    }

    public DefaultEpisode getDefaultEpi() {
        return defaultEpi;
    }

    public void setDefaultEpi(DefaultEpisode defaultEpi) {
        this.defaultEpi = defaultEpi;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStragegy() {
        return stragegy;
    }

    public void setStragegy(String stragegy) {
        this.stragegy = stragegy;
    }

    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDrm() {
        return drm;
    }

    public void setDrm(String drm) {
        this.drm = drm;
    }

    public String getHdr() {
        return hdr;
    }

    public void setHdr(String hdr) {
        this.hdr = hdr;
    }

    public int getIs3D() {
        return is3D;
    }

    public void setIs3D(int is3D) {
        this.is3D = is3D;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getType4k() {
        return type4k;
    }

    public void setType4k(String type4k) {
        this.type4k = type4k;
    }

    public String getDolby() {
        return dolby;
    }

    public void setDolby(String dolby) {
        this.dolby = dolby;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoInfo videoInfo = (VideoInfo) o;
        return qipuId == videoInfo.qipuId && albumId == videoInfo.albumId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qipuId, albumId);
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "chnId=" + chnId +
                ", chnName='" + chnName + '\'' +
                ", qipuId=" + qipuId +
                ", DefaultEpisode=" + defaultEpi +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", focus='" + focus + '\'' +
                ", albumPic='" + albumPic + '\'' +
                ", posterPic='" + posterPic + '\'' +
                ", score='" + score + '\'' +
                ", isSeries=" + isSeries +
                ", shortName='" + shortName + '\'' +
                ", isExclusive=" + isExclusive +
                ", sourceCode=" + sourceCode +
                ", publishTime='" + publishTime + '\'' +
                ", vipInfo=" + vipInfo +
                ", vipType='" + vipType + '\'' +
                ", initIssueTime='" + initIssueTime + '\'' +
                ", pCount=" + pCount +
                ", desc='" + desc + '\'' +
                ", tag='" + tag + '\'' +
                ", cast=" + cast +
                ", qiyiProd=" + qiyiProd +
                ", defaultEpi=" + defaultEpi +
                ", total=" + total +
                ", count=" + count +
                ", stragegy='" + stragegy + '\'' +
                ", isFinished=" + isFinished +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", pic='" + pic + '\'' +
                ", drm='" + drm + '\'' +
                ", hdr='" + hdr + '\'' +
                ", is3D=" + is3D +
                ", order=" + order +
                ", len=" + len +
                ", contentType=" + contentType +
                ", type4k='" + type4k + '\'' +
                ", dolby='" + dolby + '\'' +
                '}';
    }
}
