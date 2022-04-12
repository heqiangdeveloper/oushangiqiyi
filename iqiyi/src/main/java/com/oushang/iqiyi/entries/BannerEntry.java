package com.oushang.iqiyi.entries;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: zeelang
 * @Description: Banner数据
 * @Time: 2021/7/7 15:00
 * @Since: 1.0
 */
public class BannerEntry implements Parcelable {
    //视频id
    private long qipuId;
    //专辑id
    private long albumId;
    //图片地址
    private String imgUrl;
    //图片标题
    private String title;

    public BannerEntry(long qipuId, long albumId, String imgUrl, String title) {
        this.qipuId = qipuId;
        this.albumId = albumId;
        this.imgUrl = imgUrl;
        this.title = title;
    }

    public BannerEntry() {
    }

    protected BannerEntry(Parcel in) {
        qipuId = in.readLong();
        albumId = in.readLong();
        imgUrl = in.readString();
        title = in.readString();
    }

    public static final Creator<BannerEntry> CREATOR = new Creator<BannerEntry>() {
        @Override
        public BannerEntry createFromParcel(Parcel in) {
            return new BannerEntry(in);
        }

        @Override
        public BannerEntry[] newArray(int size) {
            return new BannerEntry[size];
        }
    };

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(qipuId);
        dest.writeLong(albumId);
        dest.writeString(imgUrl);
        dest.writeString(title);
    }

    public void readFromParcel(Parcel source) {
        this.qipuId = source.readLong();
        this.albumId = source.readLong();
        this.imgUrl = source.readString();
        this.title = source.readString();
    }

    @Override
    public String toString() {
        return "BannerEntry{" +
                "qipuId=" + qipuId +
                ", albumId" + albumId +
                ", imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
