package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/10/13 0013  14:05
 * @Since: 1.0
 */
public class VideoRate implements Parcelable {
    private int rt;
    private boolean isVip;
    private boolean isSelected;

    public VideoRate(int rt, boolean isVip) {
        this.rt = rt;
        this.isVip = isVip;
    }

    public VideoRate() {
    }

    protected VideoRate(Parcel in) {
        rt = in.readInt();
        isVip = in.readByte() != 0;
    }

    public static final Creator<VideoRate> CREATOR = new Creator<VideoRate>() {
        @Override
        public VideoRate createFromParcel(Parcel in) {
            return new VideoRate(in);
        }

        @Override
        public VideoRate[] newArray(int size) {
            return new VideoRate[size];
        }
    };

    public int getRt() {
        return rt;
    }

    public void setRt(int rt) {
        this.rt = rt;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    @Override
    public String toString() {
        return "VideoRate{" +
                "rt=" + rt +
                ", isVip=" + isVip +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rt);
        dest.writeByte((byte) (isVip ? 1 : 0));
    }
}
