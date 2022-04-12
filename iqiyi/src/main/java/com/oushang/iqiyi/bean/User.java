package com.oushang.iqiyi.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL on 2021-10-12 15:18
 *
 * @description:
 * @Last Modified by  DELL on 2021-10-12 15:18
 */
public class User implements Parcelable {
    private String nickName;
    private String vipExpireTime;
    private String uid;
    private String vipLevel;
    private String iconUrl;
    private String vipSurplus;
    private boolean isVip;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getVipExpireTime() {
        return vipExpireTime;
    }

    public void setVipExpireTime(String vipExpireTime) {
        this.vipExpireTime = vipExpireTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVipSurplus() {
        return vipSurplus;
    }

    public void setVipSurplus(String vipSurplus) {
        this.vipSurplus = vipSurplus;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nickName='" + nickName + '\'' +
                ", vipExpireTime='" + vipExpireTime + '\'' +
                ", uid='" + uid + '\'' +
                ", vipLevel='" + vipLevel + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", vipSurplus='" + vipSurplus + '\'' +
                ", isVip=" + isVip +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.vipExpireTime);
        dest.writeString(this.uid);
        dest.writeString(this.vipLevel);
        dest.writeString(this.iconUrl);
        dest.writeString(this.vipSurplus);
        dest.writeByte(this.isVip ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.nickName = source.readString();
        this.vipExpireTime = source.readString();
        this.uid = source.readString();
        this.vipLevel = source.readString();
        this.iconUrl = source.readString();
        this.vipSurplus = source.readString();
        this.isVip = source.readByte() != 0;
    }

    public User() {
    }

    protected User(Parcel in) {
        this.nickName = in.readString();
        this.vipExpireTime = in.readString();
        this.uid = in.readString();
        this.vipLevel = in.readString();
        this.iconUrl = in.readString();
        this.vipSurplus = in.readString();
        this.isVip = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
