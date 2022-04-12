package com.oushang.lib_service.entries;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

/**
 * @Author: zeelang
 * @Description: 收藏记录
 * @Time: 2021/10/20 0020  18:25
 * @Since: 1.0
 */
public class FavoriteRecord implements Parcelable {
    private int albumType;
    private int subType;
    private String subKey;
    private int chnId;
    private String chnName;
    private long qipuId;
    private long albumId;
    private String albumName;
    private String name;
    private String subTitle;
    private String focus;
    private String albumPic;
    private String posterPic;
    private String score;
    private String drm;
    private String hdr;
    private String shortName;
    private int isExclusive;
    private int is3D;
    private long sourceCode;
    private int order;
    private long superId;
    private String initIssueTime;
    private long len;
    private String iconId;
    private String vipType;
    private String publishTime;
    private int isSeries;
    private int contentType;
    private int pCount;
    private String desc;
    private String tag;
    private String cast;
    private String type4k;
    private String dolby;
    private int vipinfo_isVip;
    private int vipinfo_isTvod;
    private int vipinfo_isCoupon;
    private int vipinfo_isPkg;
    private int vipinfo_sttlPrc;
    private int vipinfo_orgPrc;
    private String vipinfo_validTime;
    private String playtime;
    private long addTime;
    private int total;
    private int current;
    private int episodeType;
    private long duration;
    private String tvId;

    private boolean isSelected = false; //是否选择,默认未勾选

    public FavoriteRecord() {
    }

    public FavoriteRecord(VideoInfo videoInfo, String playtime) {
        if (videoInfo != null) {
            this.chnId = videoInfo.getChnId();
            this.chnName = videoInfo.getChnName();
            this.qipuId = videoInfo.getQipuId();
            this.albumId = videoInfo.getAlbumId();
            this.albumName = videoInfo.getAlbumName();
            this.name = videoInfo.getName();
            this.subTitle = videoInfo.getSubTitle();
            this.focus = videoInfo.getFocus();
            this.albumPic = videoInfo.getAlbumPic();
            this.posterPic = videoInfo.getPosterPic();
            this.score = videoInfo.getScore();
            this.drm = videoInfo.getDrm();
            this.hdr = videoInfo.getHdr();
            this.shortName = videoInfo.getShortName();
            this.isExclusive = videoInfo.getIsExclusive();
            this.is3D = videoInfo.getIs3D();
            this.sourceCode = videoInfo.getSourceCode();
            this.order = videoInfo.getOrder();
            this.initIssueTime = videoInfo.getInitIssueTime();
            this.len = videoInfo.getLen();
            this.vipType = videoInfo.getVipType();
            this.publishTime =videoInfo.getPublishTime();
            this.isSeries = videoInfo.getIsSeries();
            this.contentType = videoInfo.getContentType();
            this.pCount = videoInfo.getpCount();
            this.desc = videoInfo.getDesc();
            this.tag = videoInfo.getTag();
            this.type4k = videoInfo.getType4k();
            this.dolby = videoInfo.getDolby();
            this.tvId = videoInfo.getQipuId() + "";

            VipInfo vipInfo = videoInfo.getVipInfo();
            if (vipInfo != null) {
                this.vipinfo_isVip = vipInfo.getIsVip();
                this.vipinfo_isTvod = vipInfo.getIsTvod();
                this.vipinfo_isCoupon = vipInfo.getIsCoupon();
                this.vipinfo_isPkg = vipInfo.getIsPkg();
                this.vipinfo_sttlPrc = vipInfo.getSttlPrc();
                this.vipinfo_orgPrc = vipInfo.getOrgPrc();
                this.vipinfo_validTime = vipInfo.getValidTime();
            }
        }
        this.playtime = playtime;
        this.addTime = System.currentTimeMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected FavoriteRecord(Parcel in) {
        albumType = in.readInt();
        subType = in.readInt();
        subKey = in.readString();
        chnId = in.readInt();
        chnName = in.readString();
        qipuId = in.readLong();
        albumId = in.readLong();
        albumName = in.readString();
        name = in.readString();
        subTitle = in.readString();
        focus = in.readString();
        albumPic = in.readString();
        posterPic = in.readString();
        score = in.readString();
        drm = in.readString();
        hdr = in.readString();
        shortName = in.readString();
        isExclusive = in.readInt();
        is3D = in.readInt();
        sourceCode = in.readLong();
        order = in.readInt();
        superId = in.readLong();
        initIssueTime = in.readString();
        len = in.readLong();
        iconId = in.readString();
        vipType = in.readString();
        publishTime = in.readString();
        isSeries = in.readInt();
        contentType = in.readInt();
        pCount = in.readInt();
        desc = in.readString();
        tag = in.readString();
        cast = in.readString();
        type4k = in.readString();
        dolby = in.readString();
        vipinfo_isVip = in.readInt();
        vipinfo_isTvod = in.readInt();
        vipinfo_isCoupon = in.readInt();
        vipinfo_isPkg = in.readInt();
        vipinfo_sttlPrc = in.readInt();
        vipinfo_orgPrc = in.readInt();
        vipinfo_validTime = in.readString();
        playtime = in.readString();
        addTime = in.readLong();
        total = in.readInt();
        current = in.readInt();
        episodeType = in.readInt();
        isSelected = in.readBoolean();
    }

    public static final Creator<FavoriteRecord> CREATOR = new Creator<FavoriteRecord>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public FavoriteRecord createFromParcel(Parcel in) {
            return new FavoriteRecord(in);
        }

        @Override
        public FavoriteRecord[] newArray(int size) {
            return new FavoriteRecord[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public int getIs3D() {
        return is3D;
    }

    public void setIs3D(int is3D) {
        this.is3D = is3D;
    }

    public long getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(long sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getSuperId() {
        return superId;
    }

    public void setSuperId(long superId) {
        this.superId = superId;
    }

    public String getInitIssueTime() {
        return initIssueTime;
    }

    public void setInitIssueTime(String initIssueTime) {
        this.initIssueTime = initIssueTime;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(int isSeries) {
        this.isSeries = isSeries;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
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

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
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

    public int getVipinfo_isVip() {
        return vipinfo_isVip;
    }

    public void setVipinfo_isVip(int vipinfo_isVip) {
        this.vipinfo_isVip = vipinfo_isVip;
    }

    public int getVipinfo_isTvod() {
        return vipinfo_isTvod;
    }

    public void setVipinfo_isTvod(int vipinfo_isTvod) {
        this.vipinfo_isTvod = vipinfo_isTvod;
    }

    public int getVipinfo_isCoupon() {
        return vipinfo_isCoupon;
    }

    public void setVipinfo_isCoupon(int vipinfo_isCoupon) {
        this.vipinfo_isCoupon = vipinfo_isCoupon;
    }

    public int getVipinfo_isPkg() {
        return vipinfo_isPkg;
    }

    public void setVipinfo_isPkg(int vipinfo_isPkg) {
        this.vipinfo_isPkg = vipinfo_isPkg;
    }

    public int getVipinfo_sttlPrc() {
        return vipinfo_sttlPrc;
    }

    public void setVipinfo_sttlPrc(int vipinfo_sttlPrc) {
        this.vipinfo_sttlPrc = vipinfo_sttlPrc;
    }

    public int getVipinfo_orgPrc() {
        return vipinfo_orgPrc;
    }

    public void setVipinfo_orgPrc(int vipinfo_orgPrc) {
        this.vipinfo_orgPrc = vipinfo_orgPrc;
    }

    public String getVipinfo_validTime() {
        return vipinfo_validTime;
    }

    public void setVipinfo_validTime(String vipinfo_validTime) {
        this.vipinfo_validTime = vipinfo_validTime;
    }

    public String getPlaytime() {
        return playtime;
    }

    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getEpisodeType() {
        return episodeType;
    }

    public void setEpisodeType(int episodeType) {
        this.episodeType = episodeType;
    }

    @Override
    public String toString() {
        return "StoreRecord{" +
                "albumType=" + albumType +
                ", subType=" + subType +
                ", subKey='" + subKey + '\'' +
                ", chnId=" + chnId +
                ", chnName='" + chnName + '\'' +
                ", qipuId=" + qipuId +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", focus='" + focus + '\'' +
                ", albumPic='" + albumPic + '\'' +
                ", posterPic='" + posterPic + '\'' +
                ", score='" + score + '\'' +
                ", drm='" + drm + '\'' +
                ", hdr='" + hdr + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isExclusive=" + isExclusive +
                ", is3D=" + is3D +
                ", sourceCode=" + sourceCode +
                ", order=" + order +
                ", superId=" + superId +
                ", initIssueTime='" + initIssueTime + '\'' +
                ", len=" + len +
                ", iconId='" + iconId + '\'' +
                ", vipType='" + vipType + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", isSeries=" + isSeries +
                ", contentType=" + contentType +
                ", pCount=" + pCount +
                ", desc='" + desc + '\'' +
                ", tag='" + tag + '\'' +
                ", cast='" + cast + '\'' +
                ", type4k='" + type4k + '\'' +
                ", dolby='" + dolby + '\'' +
                ", vipinfo_isVip=" + vipinfo_isVip +
                ", vipinfo_isTvod=" + vipinfo_isTvod +
                ", vipinfo_isCoupon=" + vipinfo_isCoupon +
                ", vipinfo_isPkg=" + vipinfo_isPkg +
                ", vipinfo_sttlPrc=" + vipinfo_sttlPrc +
                ", vipinfo_orgPrc=" + vipinfo_orgPrc +
                ", vipinfo_validTime='" + vipinfo_validTime + '\'' +
                ", playtime='" + playtime + '\'' +
                ", addTime=" + addTime +
                ", total=" + total +
                ", current=" + current +
                ", episodeType=" + episodeType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(albumType);
        dest.writeInt(subType);
        dest.writeString(subKey);
        dest.writeInt(chnId);
        dest.writeString(chnName);
        dest.writeLong(qipuId);
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeString(name);
        dest.writeString(subTitle);
        dest.writeString(focus);
        dest.writeString(albumPic);
        dest.writeString(posterPic);
        dest.writeString(score);
        dest.writeString(drm);
        dest.writeString(hdr);
        dest.writeString(shortName);
        dest.writeInt(isExclusive);
        dest.writeInt(is3D);
        dest.writeLong(sourceCode);
        dest.writeInt(order);
        dest.writeLong(superId);
        dest.writeString(initIssueTime);
        dest.writeLong(len);
        dest.writeString(iconId);
        dest.writeString(vipType);
        dest.writeString(publishTime);
        dest.writeInt(isSeries);
        dest.writeInt(contentType);
        dest.writeInt(pCount);
        dest.writeString(desc);
        dest.writeString(tag);
        dest.writeString(cast);
        dest.writeString(type4k);
        dest.writeString(dolby);
        dest.writeInt(vipinfo_isVip);
        dest.writeInt(vipinfo_isTvod);
        dest.writeInt(vipinfo_isCoupon);
        dest.writeInt(vipinfo_isPkg);
        dest.writeInt(vipinfo_sttlPrc);
        dest.writeInt(vipinfo_orgPrc);
        dest.writeString(vipinfo_validTime);
        dest.writeString(playtime);
        dest.writeLong(addTime);
        dest.writeInt(total);
        dest.writeInt(current);
        dest.writeInt(episodeType);
//        dest.writeBoolean(isSelected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteRecord that = (FavoriteRecord) o;
        return qipuId == that.qipuId && albumId == that.albumId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qipuId, albumId);
    }
}
